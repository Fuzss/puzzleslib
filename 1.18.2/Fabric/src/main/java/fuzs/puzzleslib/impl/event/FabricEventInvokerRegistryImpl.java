package fuzs.puzzleslib.impl.event;

import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.event.v1.*;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.FabricEventInvokerRegistry;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.api.event.v1.entity.EntityRidingEvents;
import fuzs.puzzleslib.api.event.v1.entity.ProjectileImpactCallback;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityLevelEvents;
import fuzs.puzzleslib.api.event.v1.entity.living.*;
import fuzs.puzzleslib.api.event.v1.entity.player.*;
import fuzs.puzzleslib.api.event.v1.level.*;
import fuzs.puzzleslib.api.event.v1.server.*;
import fuzs.puzzleslib.impl.client.event.FabricClientEventInvokers;
import fuzs.puzzleslib.impl.event.core.EventInvokerLike;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public final class FabricEventInvokerRegistryImpl implements FabricEventInvokerRegistry {
    public static final FabricEventInvokerRegistryImpl INSTANCE = new FabricEventInvokerRegistryImpl();
    private static final Map<Class<?>, EventInvokerLike<?>> EVENT_INVOKER_LOOKUP = Collections.synchronizedMap(Maps.newIdentityHashMap());

    static {
        INSTANCE.register(PlayerInteractEvents.UseBlock.class, UseBlockCallback.EVENT, callback -> {
            return (Player player, Level level, InteractionHand hand, BlockHitResult hitResult) -> {
                InteractionResult interactionResult = callback.onUseBlock(player, level, hand, hitResult).getInterrupt().orElse(InteractionResult.PASS);
                // this fixes an issue with Fabric Api where InteractionResult#CONSUME and InteractionResult#PARTIAL_SUCCESS when returned client-side do not trigger the server packet
                // although InteractionResult#SUCCESS is generally desired for the client, there are cases where it is not, but the interaction should still consume
                if (level.isClientSide && interactionResult != InteractionResult.SUCCESS && interactionResult.consumesAction()) {
                    Proxy.INSTANCE.getClientPacketListener().send(new ServerboundUseItemOnPacket(hand, hitResult));
                }
                return interactionResult;
            };
        });
        INSTANCE.register(PlayerInteractEvents.AttackBlock.class, AttackBlockCallback.EVENT, callback -> {
            return (Player player, Level level, InteractionHand hand, BlockPos pos, Direction direction) -> {
                return callback.onAttackBlock(player, level, hand, pos, direction).getInterrupt().orElse(InteractionResult.PASS);
            };
        });
        INSTANCE.register(PlayerInteractEvents.UseItem.class, UseItemCallback.EVENT, callback -> {
            return (Player player, Level level, InteractionHand hand) -> {
                InteractionResultHolder<ItemStack> result = callback.onUseItem(player, level, hand).getInterrupt().orElse(InteractionResultHolder.pass(ItemStack.EMPTY));
                // this fixes an issue with Fabric Api where InteractionResult#CONSUME and InteractionResult#PARTIAL_SUCCESS when returned client-side do not trigger the server packet
                // although InteractionResult#SUCCESS is generally desired for the client, there are cases where it is not, but the interaction should still consume
                if (level.isClientSide && result.getResult() != InteractionResult.SUCCESS && result.getResult().consumesAction()) {
                    Proxy.INSTANCE.getClientPacketListener().send(new ServerboundUseItemPacket(hand));
                }
                return result;
            };
        });
        INSTANCE.register(PlayerInteractEvents.UseEntity.class, UseEntityCallback.EVENT, callback -> {
            return (Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) -> {
                // this breaks this event on 1.18.2 as the Fabric event only runs for Entity::interactAt which always has a hit result
                // this is different in 1.19+ where the hit result will be null when the Fabric event runs for Entity::interact
//                if (hitResult != null) return InteractionResult.PASS;
                return callback.onUseEntity(player, level, hand, entity).getInterrupt().orElse(InteractionResult.PASS);
            };
        });
        INSTANCE.register(PlayerInteractEvents.UseEntityAt.class, UseEntityCallback.EVENT, callback -> {
            return (Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) -> {
                // this check is not necessary on 1.18.2 as the Fabric event only runs for Entity::interactAt which always has a hit result
                if (hitResult == null) return InteractionResult.PASS;
                return callback.onUseEntityAt(player, level, hand, entity, hitResult.getLocation()).getInterrupt().orElse(InteractionResult.PASS);
            };
        });
        INSTANCE.register(PlayerInteractEvents.AttackEntity.class, AttackEntityCallback.EVENT, callback -> {
            return (Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) -> {
                EventResult result = callback.onAttackEntity(player, level, hand, entity);
                // this isn't a proper item use callback (seen with the server-side and Forge implementations), so the return looks a little odd
                // we return InteractionResult#SUCCESS so the packet is sent to the server either way so the server may handle this on its own as Forge does
                return result.isInterrupt() ? InteractionResult.SUCCESS : InteractionResult.PASS;
            };
        });
        INSTANCE.register(PlayerXpEvents.PickupXp.class, FabricPlayerEvents.PICKUP_XP);
        INSTANCE.register(BonemealCallback.class, FabricPlayerEvents.BONEMEAL);
        INSTANCE.register(LivingExperienceDropCallback.class, FabricLivingEvents.EXPERIENCE_DROP);
        INSTANCE.register(BlockEvents.FarmlandTrample.class, FabricLevelEvents.FARMLAND_TRAMPLE);
        INSTANCE.register(PlayerTickEvents.Start.class, FabricPlayerEvents.PLAYER_TICK_START);
        INSTANCE.register(PlayerTickEvents.End.class, FabricPlayerEvents.PLAYER_TICK_END);
        INSTANCE.register(LivingFallCallback.class, FabricLivingEvents.LIVING_FALL);
        INSTANCE.register(RegisterCommandsCallback.class, CommandRegistrationCallback.EVENT, callback -> {
            return (CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) -> {
                callback.onRegisterCommands(dispatcher, dedicated ? Commands.CommandSelection.DEDICATED : Commands.CommandSelection.INTEGRATED);
            };
        });
        INSTANCE.register(LootTableLoadEvents.Replace.class, LootTableEvents.REPLACE, callback -> {
            return (ResourceManager resourceManager, LootTables lootManager, ResourceLocation id, LootTable original, LootTableSource source) -> {
                // don't filter on source, with our custom event on Forge we can also support non-built-in loot tables
                DefaultedValue<LootTable> lootTable = DefaultedValue.fromValue(original);
                callback.onReplaceLootTable(lootManager, id, lootTable);
                // returning null will prompt no change
                return lootTable.getAsOptional().orElse(null);
            };
        });
        INSTANCE.register(LootTableLoadEvents.Modify.class, LootTableEvents.MODIFY, callback -> {
            return (ResourceManager resourceManager, LootTables lootManager, ResourceLocation id, LootTable.Builder tableBuilder, LootTableSource source) -> {
                // keep this the same as Forge where editing data pack specified loot tables is not supported
                if (source == LootTableSource.DATA_PACK) return;
                callback.onModifyLootTable(lootManager, id, tableBuilder::pool, index -> {
                    MutableInt currentIndex = new MutableInt();
                    MutableBoolean result = new MutableBoolean();
                    tableBuilder.modifyPools(builder -> {
                        if (index == currentIndex.getAndIncrement()) {
                            // there is no way in Fabric Api to remove loot pools, but this seems to work for disabling at least
                            builder.setRolls(ConstantValue.exactly(0.0F));
                            builder.setBonusRolls(ConstantValue.exactly(0.0F));
                            result.setTrue();
                        }
                    });
                    return result.booleanValue();
                });
            };
        });
        INSTANCE.register(AnvilRepairCallback.class, FabricPlayerEvents.ANVIL_REPAIR);
        INSTANCE.register(ItemTouchCallback.class, FabricPlayerEvents.ITEM_TOUCH);
        INSTANCE.register(PlayerEvents.ItemPickup.class, FabricPlayerEvents.ITEM_PICKUP);
        INSTANCE.register(LootingLevelCallback.class, FabricLivingEvents.LOOTING_LEVEL);
        INSTANCE.register(AnvilUpdateCallback.class, FabricPlayerEvents.ANVIL_UPDATE);
        INSTANCE.register(LivingDropsCallback.class, FabricLivingEvents.LIVING_DROPS);
        INSTANCE.register(LivingEvents.Tick.class, FabricLivingEvents.LIVING_TICK);
        INSTANCE.register(ArrowLooseCallback.class, FabricPlayerEvents.ARROW_LOOSE);
        INSTANCE.register(LivingHurtCallback.class, FabricLivingEvents.LIVING_HURT);
        INSTANCE.register(UseItemEvents.Start.class, FabricLivingEvents.USE_ITEM_START);
        INSTANCE.register(UseItemEvents.Tick.class, FabricLivingEvents.USE_ITEM_TICK);
        INSTANCE.register(UseItemEvents.Stop.class, FabricLivingEvents.USE_ITEM_STOP);
        INSTANCE.register(UseItemEvents.Finish.class, FabricLivingEvents.USE_ITEM_FINISH);
        INSTANCE.register(ShieldBlockCallback.class, FabricLivingEvents.SHIELD_BLOCK);
        INSTANCE.register(TagsUpdatedCallback.class, CommonLifecycleEvents.TAGS_LOADED, callback -> {
            return callback::onTagsUpdated;
        });
        INSTANCE.register(ExplosionEvents.Start.class, FabricLevelEvents.EXPLOSION_START);
        INSTANCE.register(ExplosionEvents.Detonate.class, FabricLevelEvents.EXPLOSION_DETONATE);
        INSTANCE.register(SyncDataPackContentsCallback.class, ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS, callback -> {
            return callback::onSyncDataPackContents;
        });
        INSTANCE.register(fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents.ServerStarting.class, ServerLifecycleEvents.SERVER_STARTING, callback -> {
            return callback::onServerStarting;
        });
        INSTANCE.register(fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents.ServerStarted.class, ServerLifecycleEvents.SERVER_STARTING, callback -> {
            return callback::onServerStarted;
        });
        INSTANCE.register(fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents.ServerStopping.class, ServerLifecycleEvents.SERVER_STOPPING, callback -> {
            return callback::onServerStopping;
        });
        INSTANCE.register(fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents.ServerStopped.class, ServerLifecycleEvents.SERVER_STOPPED, callback -> {
            return callback::onServerStopped;
        });
        INSTANCE.register(PlayLevelSoundEvents.AtPosition.class, FabricLevelEvents.PLAY_LEVEL_SOUND_AT_POSITION);
        INSTANCE.register(PlayLevelSoundEvents.AtEntity.class, FabricLevelEvents.PLAY_LEVEL_SOUND_AT_ENTITY);
        INSTANCE.register(ServerEntityLevelEvents.Load.class, FabricEntityEvents.ENTITY_LOAD);
        INSTANCE.register(ServerEntityLevelEvents.LoadV2.class, FabricEntityEvents.ENTITY_LOAD_V2);
        INSTANCE.register(ServerEntityLevelEvents.Spawn.class, FabricEntityEvents.ENTITY_SPAWN);
        INSTANCE.register(ServerEntityLevelEvents.Remove.class, ServerEntityEvents.ENTITY_UNLOAD, callback -> {
            return callback::onEntityRemove;
        });
        INSTANCE.register(LivingDeathCallback.class, FabricLivingEvents.LIVING_DEATH);
        INSTANCE.register(PlayerEvents.StartTracking.class, EntityTrackingEvents.START_TRACKING, callback -> {
            return callback::onStartTracking;
        });
        INSTANCE.register(PlayerEvents.StopTracking.class, EntityTrackingEvents.STOP_TRACKING, callback -> {
            return callback::onStopTracking;
        });
        INSTANCE.register(PlayerEvents.LoggedIn.class, ServerPlayConnectionEvents.JOIN, callback -> {
            return (ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) -> {
                callback.onLoggedIn(handler.getPlayer());
            };
        });
        INSTANCE.register(PlayerEvents.LoggedOut.class, ServerPlayConnectionEvents.DISCONNECT, callback -> {
            return (ServerGamePacketListenerImpl handler, MinecraftServer server) -> {
                callback.onLoggedOut(handler.getPlayer());
            };
        });
        INSTANCE.register(PlayerEvents.AfterChangeDimension.class, ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD, callback -> {
            return callback::onAfterChangeDimension;
        });
        INSTANCE.register(BabyEntitySpawnCallback.class, FabricLivingEvents.BABY_ENTITY_SPAWN);
        INSTANCE.register(AnimalTameCallback.class, FabricLivingEvents.ANIMAL_TAME);
        INSTANCE.register(LivingAttackCallback.class, FabricLivingEvents.LIVING_ATTACK);
        INSTANCE.register(PlayerEvents.Copy.class, ServerPlayerEvents.COPY_FROM, callback -> {
            return callback::onCopy;
        });
        INSTANCE.register(PlayerEvents.Respawn.class, ServerPlayerEvents.AFTER_RESPAWN, callback -> {
            return (ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) -> {
                callback.onRespawn(newPlayer, alive);
            };
        });
        INSTANCE.register(ServerTickEvents.Start.class, net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_SERVER_TICK, callback -> {
            return callback::onStartServerTick;
        });
        INSTANCE.register(ServerTickEvents.End.class, net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK, callback -> {
            return callback::onEndServerTick;
        });
        INSTANCE.register(ServerLevelTickEvents.Start.class, net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_WORLD_TICK, callback -> {
            return (ServerLevel level) -> {
                callback.onStartLevelTick(level.getServer(), level);
            };
        });
        INSTANCE.register(ServerLevelTickEvents.End.class, net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK, callback -> {
            return (ServerLevel level) -> {
                callback.onEndLevelTick(level.getServer(), level);
            };
        });
        INSTANCE.register(ServerLevelEvents.Load.class, ServerWorldEvents.LOAD, callback -> {
            return callback::onLevelLoad;
        });
        INSTANCE.register(ServerLevelEvents.Unload.class, ServerWorldEvents.UNLOAD, callback -> {
            return callback::onLevelUnload;
        });
        INSTANCE.register(ServerChunkEvents.Load.class, net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_LOAD, callback -> {
            return callback::onChunkLoad;
        });
        INSTANCE.register(ServerChunkEvents.Unload.class, net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_UNLOAD, callback -> {
            return callback::onChunkUnload;
        });
        INSTANCE.register(ItemTossCallback.class, FabricPlayerEvents.ITEM_TOSS);
        INSTANCE.register(LivingKnockBackCallback.class, FabricLivingEvents.LIVING_KNOCK_BACK);
        INSTANCE.register(ItemAttributeModifiersCallback.class, FabricLivingEvents.ITEM_ATTRIBUTE_MODIFIERS);
        INSTANCE.register(ProjectileImpactCallback.class, FabricEntityEvents.PROJECTILE_IMPACT);
        INSTANCE.register(PlayerEvents.BreakSpeed.class, FabricPlayerEvents.BREAK_SPEED);
        INSTANCE.register(MobEffectEvents.Affects.class, FabricLivingEvents.MOB_EFFECT_AFFECTS);
        INSTANCE.register(MobEffectEvents.Apply.class, FabricLivingEvents.MOB_EFFECT_APPLY);
        INSTANCE.register(MobEffectEvents.Remove.class, FabricLivingEvents.MOB_EFFECT_REMOVE);
        INSTANCE.register(MobEffectEvents.Expire.class, FabricLivingEvents.MOB_EFFECT_EXPIRE);
        INSTANCE.register(LivingEvents.Jump.class, FabricLivingEvents.LIVING_JUMP);
        INSTANCE.register(LivingEvents.Visibility.class, FabricLivingEvents.LIVING_VISIBILITY);
        INSTANCE.register(LivingChangeTargetCallback.class, FabricLivingEvents.LIVING_CHANGE_TARGET);
        INSTANCE.register(CheckMobDespawnCallback.class, FabricLivingEvents.CHECK_MOB_DESPAWN);
        INSTANCE.register(GatherPotentialSpawnsCallback.class, FabricLevelEvents.GATHER_POTENTIAL_SPAWNS);
        INSTANCE.register(EntityRidingEvents.Start.class, FabricEntityEvents.ENTITY_START_RIDING);
        INSTANCE.register(EntityRidingEvents.Stop.class, FabricEntityEvents.ENTITY_STOP_RIDING);
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            FabricClientEventInvokers.register();
        } else {
            INSTANCE.register(LoadCompleteCallback.class, ServerLifecycleEvents.SERVER_STARTED, callback -> {
                return (MinecraftServer server) -> {
                    callback.onLoadComplete();
                };
            });
        }
    }

    @SuppressWarnings("unchecked")
    public <T> EventInvoker<T> lookup(Class<T> clazz, @Nullable Object context) {
        Objects.requireNonNull(clazz, "type is null");
        EventInvokerLike<T> invokerLike = (EventInvokerLike<T>) EVENT_INVOKER_LOOKUP.get(clazz);
        Objects.requireNonNull(invokerLike, "invoker for type %s is null".formatted(clazz));
        EventInvoker<T> invoker = invokerLike.asEventInvoker(context);
        Objects.requireNonNull(invoker, "invoker for type %s is null".formatted(clazz));
        return invoker;
    }

    @Override
    public <T, E> void register(Class<T> clazz, Event<E> event, Function<T, E> converter) {
        Objects.requireNonNull(clazz, "type is null");
        Objects.requireNonNull(event, "event is null");
        Objects.requireNonNull(converter, "converter is null");
        register(clazz, new FabricEventInvoker<>(event, converter));
    }

    @Override
    public <T, E> void register(Class<T> clazz, Class<E> eventType, Function<T, E> converter, FabricEventContextConsumer<E> consumer) {
        Objects.requireNonNull(clazz, "type is null");
        Objects.requireNonNull(eventType, "event type is null");
        Objects.requireNonNull(converter, "converter is null");
        Objects.requireNonNull(consumer, "consumer is null");
        register(clazz, new FabricForwardingEventInvoker<>(converter, consumer));
    }

    private static <T> void register(Class<T> clazz, EventInvokerLike<T> invoker) {
        if (EVENT_INVOKER_LOOKUP.put(clazz, invoker) != null) {
            throw new IllegalArgumentException("duplicate event invoker for type %s".formatted(clazz));
        }
    }

    private record FabricEventInvoker<T, E>(Event<E> event, Function<T, E> converter, Set<EventPhase> knownEventPhases) implements EventInvoker<T>, EventInvokerLike<T> {

        public FabricEventInvoker(Event<E> event, Function<T, E> converter) {
            this(event, converter, Collections.synchronizedSet(Sets.newIdentityHashSet()));
        }

        @Override
        public EventInvoker<T> asEventInvoker(@Nullable Object context) {
            return this;
        }

        @Override
        public void register(EventPhase phase, T callback) {
            Objects.requireNonNull(phase, "phase is null");
            Objects.requireNonNull(callback, "callback is null");
            // this is the default phase
            if (phase.parent() == null) {
                this.event.register(this.converter.apply(callback));
            } else {
                // make sure phase has consumer phase ordering, we keep track of phases we have already added an ordering for in this event in #knownEventPhases
                this.testEventPhase(phase);
                this.event.register(phase.identifier(), this.converter.apply(callback));
            }
        }

        private void testEventPhase(EventPhase phase) {
            Stack<EventPhase> stack = new Stack<>();
            // find the first parent we already know (probably default)
            while (phase.parent() != null && !this.knownEventPhases.contains(phase)) {
                stack.push(phase);
                phase = phase.parent();
            }
            // add consumer phase ordering for all parents in reverse order until we reach the phase we want to add
            while (!stack.isEmpty()) {
                phase = stack.pop();
                phase.applyOrdering(this.event::addPhaseOrdering);
                this.knownEventPhases.add(phase);
            }
        }
    }

    private record FabricForwardingEventInvoker<T, E>(Function<Event<E>, EventInvoker<T>> factory, FabricEventContextConsumer<E> consumer, Map<Event<E>, EventInvoker<T>> events) implements EventInvokerLike<T> {

        public FabricForwardingEventInvoker(Function<T, E> converter, FabricEventContextConsumer<E> consumer) {
            this(event -> new FabricEventInvoker<>(event, converter), consumer, new MapMaker().weakKeys().makeMap());
        }

        @Override
        public EventInvoker<T> asEventInvoker(@NotNull Object context) {
            Objects.requireNonNull(context, "context is null");
            // keeping track of events and corresponding invoker is not so important since there is only ever one event per context anyway which is guaranteed by the underlying implementation
            // but for managing event phases it becomes necessary to use our FabricEventInvoker to keep track
            return (EventPhase phase, T callback) -> {
                this.consumer.accept(context, event -> {
                    this.events.computeIfAbsent(event, this.factory).register(phase, callback);
                }, this.events::remove);
            };
        }
    }
}
