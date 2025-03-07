package fuzs.puzzleslib.fabric.impl.event;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.event.v1.*;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.entity.*;
import fuzs.puzzleslib.api.event.v1.entity.living.*;
import fuzs.puzzleslib.api.event.v1.entity.player.*;
import fuzs.puzzleslib.api.event.v1.level.*;
import fuzs.puzzleslib.api.event.v1.server.*;
import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.fabric.api.event.v1.*;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventInvokerRegistry;
import fuzs.puzzleslib.fabric.impl.client.event.FabricClientEventInvokers;
import fuzs.puzzleslib.fabric.impl.core.FabricProxy;
import fuzs.puzzleslib.fabric.impl.init.FabricPotionBrewingBuilder;
import fuzs.puzzleslib.impl.event.CopyOnWriteForwardingList;
import fuzs.puzzleslib.impl.event.core.EventInvokerImpl;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;

public final class FabricEventInvokerRegistryImpl implements FabricEventInvokerRegistry {

    @SuppressWarnings("unchecked")
    public static void registerLoadingHandlers() {
        INSTANCE.register(LoadCompleteCallback.class, FabricLifecycleEvents.LOAD_COMPLETE);
        INSTANCE.register(RegistryEntryAddedCallback.class, FabricEventInvokerRegistryImpl::onRegistryEntryAdded);
        INSTANCE.register(FinalizeItemComponentsCallback.class, DefaultItemComponentEvents.MODIFY, (FinalizeItemComponentsCallback callback) -> {
            return (DefaultItemComponentEvents.ModifyContext context) -> {
                for (Item item : BuiltInRegistries.ITEM) {
                    callback.onFinalizeItemComponents(item, (Function<DataComponentMap, DataComponentPatch> function) -> {
                        context.modify(item, (DataComponentMap.Builder builder) -> {
                            function.apply(builder.build()).entrySet().forEach((Map.Entry<DataComponentType<?>, Optional<?>> entry) -> {
                                builder.set((DataComponentType<Object>) entry.getKey(), entry.getValue().orElse(null));
                            });
                        });
                    });
                }
            };
        });
        INSTANCE.register(ComputeItemAttributeModifiersCallback.class, DefaultItemComponentEvents.MODIFY, (ComputeItemAttributeModifiersCallback callback) -> {
            return (DefaultItemComponentEvents.ModifyContext context) -> {
                for (Item item : BuiltInRegistries.ITEM) {
                    ItemAttributeModifiers itemAttributeModifiers = item.components()
                            .getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
                    CopyOnWriteForwardingList<ItemAttributeModifiers.Entry> entries = new CopyOnWriteForwardingList<>(
                            itemAttributeModifiers.modifiers());
                    callback.onComputeItemAttributeModifiers(item, entries);
                    if (entries.delegate() != itemAttributeModifiers.modifiers()) {
                        context.modify(item, (DataComponentMap.Builder builder) -> {
                            builder.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(ImmutableList.copyOf(entries), itemAttributeModifiers.showInTooltip()));
                        });
                    }
                }
            };
        });
        INSTANCE.register(AddBlockEntityTypeBlocksCallback.class, FabricLifecycleEvents.LOAD_COMPLETE, (AddBlockEntityTypeBlocksCallback callback) -> {
            return () -> {
                callback.onAddBlockEntityTypeBlocks((BlockEntityType<?> blockEntityType, Block block) -> {
                    blockEntityType.addSupportedBlock(block);
                });
            };
        });
        INSTANCE.register(CommonSetupCallback.class, (CommonSetupCallback callback, @Nullable Object context) -> {
            callback.onCommonSetup();
        });
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            FabricClientEventInvokers.registerLoadingHandlers();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void onRegistryEntryAdded(RegistryEntryAddedCallback<T> callback, @Nullable Object context) {
        Objects.requireNonNull(context, "context is null");
        ResourceKey<? extends Registry<T>> resourceKey = (ResourceKey<? extends Registry<T>>) context;
        Registry<T> registry = LookupHelper.getRegistry(resourceKey).orElseThrow();
        BiConsumer<ResourceLocation, Supplier<T>> registrar = (ResourceLocation resourceLocation, Supplier<T> supplier) -> {
            Registry.register(registry, resourceLocation, supplier.get());
        };
        net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback.event(registry).register((int rawId, ResourceLocation id, T object) -> {
            callback.onRegistryEntryAdded(registry, id, object, registrar);
        });
        // do not register directly to prevent ConcurrentModificationException
        Map<ResourceLocation, Supplier<T>> toRegister = Maps.newLinkedHashMap();
        for (Map.Entry<ResourceKey<T>, T> entry : registry.entrySet()) {
            callback.onRegistryEntryAdded(registry, entry.getKey().location(), entry.getValue(), toRegister::put);
        }
        toRegister.forEach(registrar);
    }

    public static void registerEventHandlers() {
        INSTANCE.register(AddDataPackReloadListenersCallback.class, FabricLifecycleEvents.ADD_DATA_PACK_RELOAD_LISTENERS);
        INSTANCE.register(TagsUpdatedCallback.class, CommonLifecycleEvents.TAGS_LOADED, (TagsUpdatedCallback callback) -> {
            return callback::onTagsUpdated;
        });
        INSTANCE.register(RegisterCommandsCallback.class, CommandRegistrationCallback.EVENT, (RegisterCommandsCallback callback) -> {
            return callback::onRegisterCommands;
        });
        INSTANCE.register(fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents.Starting.class, ServerLifecycleEvents.SERVER_STARTING, (fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents.Starting callback) -> {
            return callback::onServerStarting;
        });
        INSTANCE.register(PlayerInteractEvents.UseBlock.class, UseBlockCallback.EVENT, (PlayerInteractEvents.UseBlock callback) -> {
            return (Player player, Level level, InteractionHand hand, BlockHitResult hitResult) -> {
                EventResultHolder<InteractionResult> result = callback.onUseBlock(player, level, hand, hitResult);
                return result.getInterrupt().map((InteractionResult interactionResult) -> {
                    if (interactionResult == InteractionResult.PASS) {
                        // this is done for parity with Forge where InteractionResult#PASS can be cancelled, while on Fabric it will mark the event as having done nothing
                        // unfortunately this will prevent the off-hand from being processed (if fired for the main hand), but it's the best we can do
                        interactionResult = InteractionResult.FAIL;
                    }

                    if (level.isClientSide && interactionResult != InteractionResult.SUCCESS) {
                        // this brings parity with Forge where the server is notified regardless of the returned InteractionResult (the Forge event runs after the server packet is sent)
                        ((FabricProxy) Proxy.INSTANCE).startClientPrediction(level, (int id) -> new ServerboundUseItemOnPacket(hand, hitResult, id));
                    }

                    return interactionResult;
                }).orElse(InteractionResult.PASS);
            };
        });
        INSTANCE.register(PlayerInteractEvents.AttackBlock.class, AttackBlockCallback.EVENT, (PlayerInteractEvents.AttackBlock callback) -> {
            return (Player player, Level level, InteractionHand hand, BlockPos pos, Direction direction) -> {
                if (!level.isClientSide || player.isCreative() || ((FabricProxy) Proxy.INSTANCE).shouldStartDestroyBlock(pos)) {
                    EventResult result = callback.onAttackBlock(player, level, hand, pos, direction);
                    // this brings parity with Forge where the server is notified regardless of the returned InteractionResult (achieved by returning InteractionResult#SUCCESS) since the Forge event runs after the server packet is sent
                    // returning InteractionResult#SUCCESS will return true from MultiPlayerGameMode::continueDestroyBlock which will spawn breaking particles and make the player arm swing
                    return result.isInterrupt() ? InteractionResult.SUCCESS : InteractionResult.PASS;
                } else {
                    return InteractionResult.PASS;
                }
            };
        });
        INSTANCE.register(PlayerInteractEvents.UseItem.class, UseItemCallback.EVENT, (PlayerInteractEvents.UseItem callback) -> {
            return (Player player, Level level, InteractionHand hand) -> {
                // parity with Forge, result item stack does not matter for Fabric implementation when result is pass
                if (player.isSpectator()) {
                    return InteractionResult.PASS;
                } else if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
                    return InteractionResult.PASS;
                }

                EventResultHolder<InteractionResult> result = callback.onUseItem(player, level, hand);
                return result.getInterrupt().map((InteractionResult interactionResult) -> {
                    if (interactionResult == InteractionResult.PASS) {
                        // this is done for parity with Forge where InteractionResult#PASS can be cancelled, while on Fabric it will mark the event as having done nothing
                        // unfortunately this will prevent the off-hand from being processed (if fired for the main hand), but it's the best we can do
                        interactionResult = InteractionResult.FAIL;
                    }

                    // this brings parity with Forge where the server is notified regardless of the returned InteractionResult (the Forge event runs after the server packet is sent)
                    // the Fabric implementation already takes care of SUCCESS and PASS ignored (arbitrarily enforced on Forge)
                    if (level.isClientSide && interactionResult != InteractionResult.SUCCESS) {
                        // send the move packet like vanilla to ensure the position+view vectors are accurate
                        Proxy.INSTANCE.getClientPacketListener()
                                .send(new ServerboundMovePlayerPacket.PosRot(player.getX(),
                                        player.getY(),
                                        player.getZ(),
                                        player.getYRot(),
                                        player.getXRot(),
                                        player.onGround(), player.horizontalCollision
                                ));
                        // send interaction packet to the server with a new sequentially assigned id
                        ((FabricProxy) Proxy.INSTANCE).startClientPrediction(level,
                                (int id) -> new ServerboundUseItemPacket(hand, id, player.getYRot(), player.getXRot())
                        );
                    }

                    return interactionResult;
                }).orElse(InteractionResult.PASS);
            };
        });
        INSTANCE.register(PlayerInteractEvents.UseEntity.class, UseEntityCallback.EVENT, (PlayerInteractEvents.UseEntity callback) -> {
            return (Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) -> {
                // Fabric handles two possible cases in one event, here we separate them again
                if (hitResult != null) return InteractionResult.PASS;
                EventResultHolder<InteractionResult> result = callback.onUseEntity(player, level, hand, entity);
                return result.getInterrupt().map((InteractionResult interactionResult) -> {
                    if (interactionResult == InteractionResult.PASS) {
                        // this is done for parity with Forge where InteractionResult#PASS can be cancelled, while on Fabric it will mark the event as having done nothing
                        // unfortunately this will prevent the off-hand from being processed (if fired for the main hand), but it's the best we can do
                        interactionResult = InteractionResult.FAIL;
                    }

                    if (level.isClientSide && interactionResult == InteractionResult.FAIL) {
                        // this brings parity with Forge where the server is notified regardless of the returned InteractionResult (the Forge event runs after the server packet is sent)
                        Proxy.INSTANCE.getClientPacketListener().send(ServerboundInteractPacket.createInteractionPacket(entity, player.isShiftKeyDown(), hand));
                    }

                    return interactionResult;
                }).orElse(InteractionResult.PASS);
            };
        });
        INSTANCE.register(PlayerInteractEvents.UseEntityAt.class, UseEntityCallback.EVENT, (PlayerInteractEvents.UseEntityAt callback) -> {
            return (Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) -> {
                // Fabric handles two possible cases in one event, here we separate them again
                if (hitResult == null) return InteractionResult.PASS;
                EventResultHolder<InteractionResult> result = callback.onUseEntityAt(player,
                        level,
                        hand,
                        entity,
                        hitResult.getLocation()
                );
                return result.getInterrupt().map((InteractionResult interactionResult) -> {
                    if (interactionResult == InteractionResult.PASS) {
                        // this is done for parity with Forge where InteractionResult#PASS can be cancelled, while on Fabric it will mark the event as having done nothing
                        // unfortunately this will prevent the off-hand from being processed (if fired for the main hand), but it's the best we can do
                        interactionResult = InteractionResult.FAIL;
                    }

                    if (level.isClientSide && interactionResult == InteractionResult.FAIL) {
                        // this brings parity with Forge where the server is notified regardless of the returned InteractionResult (the Forge event runs after the server packet is sent)
                        Proxy.INSTANCE.getClientPacketListener().send(ServerboundInteractPacket.createInteractionPacket(entity, player.isShiftKeyDown(), hand, hitResult.getLocation()));
                    }

                    return interactionResult;
                }).orElse(InteractionResult.PASS);
            };
        });
        INSTANCE.register(PlayerInteractEvents.AttackEntity.class, AttackEntityCallback.EVENT, (PlayerInteractEvents.AttackEntity callback) -> {
            return (Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) -> {
                EventResult result = callback.onAttackEntity(player, level, hand, entity);
                // this isn't a proper item use callback (seen with the server-side and Forge implementations), so the return looks a little odd
                // we return InteractionResult#SUCCESS so the packet is sent to the server either way so the server may handle this on its own as Forge does
                return result.isInterrupt() ? InteractionResult.SUCCESS : InteractionResult.PASS;
            };
        });
        INSTANCE.register(PickupExperienceCallback.class, FabricPlayerEvents.PICKUP_XP);
        INSTANCE.register(UseBoneMealCallback.class, FabricPlayerEvents.USE_BONEMEAL);
        INSTANCE.register(LivingExperienceDropCallback.class, FabricLivingEvents.EXPERIENCE_DROP);
        INSTANCE.register(BlockEvents.Break.class, PlayerBlockBreakEvents.BEFORE, (BlockEvents.Break callback) -> {
            return (Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) -> {
                EventResult result = callback.onBreakBlock((ServerLevel) level, pos, state, player, player.getMainHandItem());
                return result.isPass();
            };
        });
        INSTANCE.register(BlockEvents.DropExperience.class, FabricLevelEvents.DROP_BLOCK_EXPERIENCE);
        INSTANCE.register(BlockEvents.FarmlandTrample.class, FabricLevelEvents.FARMLAND_TRAMPLE);
        INSTANCE.register(PlayerTickEvents.Start.class, FabricPlayerEvents.PLAYER_TICK_START);
        INSTANCE.register(PlayerTickEvents.End.class, FabricPlayerEvents.PLAYER_TICK_END);
        INSTANCE.register(LivingFallCallback.class, FabricLivingEvents.LIVING_FALL);
        INSTANCE.register(LootTableLoadCallback.class, LootTableEvents.MODIFY, (LootTableLoadCallback callback) -> {
            return (ResourceKey<LootTable> key, LootTable.Builder tableBuilder, LootTableSource source, HolderLookup.Provider registries) -> {
                callback.onLootTableLoad(key.location(), tableBuilder, registries);
            };
        });
        INSTANCE.register(AnvilEvents.Use.class, FabricPlayerEvents.ANVIL_USE);
        INSTANCE.register(ItemEntityEvents.Touch.class, FabricPlayerEvents.ITEM_TOUCH);
        INSTANCE.register(ItemEntityEvents.Pickup.class, FabricPlayerEvents.ITEM_PICKUP);
        INSTANCE.register(ComputeEnchantedLootBonusCallback.class, FabricLivingEvents.COMPUTE_ENCHANTED_LOOT_BONUS);
        INSTANCE.register(AnvilEvents.Update.class, FabricPlayerEvents.ANVIL_UPDATE);
        INSTANCE.register(LivingDropsCallback.class, FabricLivingEvents.LIVING_DROPS);
        INSTANCE.register(EntityTickEvents.Start.class, FabricEntityEvents.ENTITY_TICK_START);
        INSTANCE.register(EntityTickEvents.End.class, FabricEntityEvents.ENTITY_TICK_END);
        INSTANCE.register(ArrowLooseCallback.class, FabricPlayerEvents.ARROW_LOOSE);
        INSTANCE.register(LivingHurtCallback.class, FabricLivingEvents.LIVING_HURT);
        INSTANCE.register(UseItemEvents.Start.class, FabricLivingEvents.USE_ITEM_START);
        INSTANCE.register(UseItemEvents.Tick.class, FabricLivingEvents.USE_ITEM_TICK);
        INSTANCE.register(UseItemEvents.Stop.class, FabricLivingEvents.USE_ITEM_STOP);
        INSTANCE.register(UseItemEvents.Finish.class, FabricLivingEvents.USE_ITEM_FINISH);
        INSTANCE.register(ShieldBlockCallback.class, FabricLivingEvents.SHIELD_BLOCK);
        INSTANCE.register(ExplosionEvents.Start.class, FabricLevelEvents.EXPLOSION_START);
        INSTANCE.register(ExplosionEvents.Detonate.class, FabricLevelEvents.EXPLOSION_DETONATE);
        INSTANCE.register(SyncDataPackContentsCallback.class, ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS, (SyncDataPackContentsCallback callback) -> {
            return callback::onSyncDataPackContents;
        });
        INSTANCE.register(fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents.Started.class, ServerLifecycleEvents.SERVER_STARTED, (fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents.Started callback) -> {
            return callback::onServerStarted;
        });
        INSTANCE.register(fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents.Stopping.class, ServerLifecycleEvents.SERVER_STOPPING, (fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents.Stopping callback) -> {
            return callback::onServerStopping;
        });
        INSTANCE.register(fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents.Stopped.class, ServerLifecycleEvents.SERVER_STOPPED, (fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents.Stopped callback) -> {
            return callback::onServerStopped;
        });
        INSTANCE.register(PlayLevelSoundEvents.AtPosition.class, FabricLevelEvents.PLAY_LEVEL_SOUND_AT_POSITION);
        INSTANCE.register(PlayLevelSoundEvents.AtEntity.class, FabricLevelEvents.PLAY_LEVEL_SOUND_AT_ENTITY);
        INSTANCE.register(ServerEntityLevelEvents.Load.class, FabricEntityEvents.ENTITY_LOAD);
        INSTANCE.register(ServerEntityLevelEvents.Spawn.class, FabricEntityEvents.ENTITY_SPAWN);
        INSTANCE.register(ServerEntityLevelEvents.Unload.class, ServerEntityEvents.ENTITY_UNLOAD, (ServerEntityLevelEvents.Unload callback) -> {
            return callback::onEntityUnload;
        });
        INSTANCE.register(LivingDeathCallback.class, FabricLivingEvents.LIVING_DEATH);
        INSTANCE.register(PlayerTrackingEvents.Start.class, FabricPlayerEvents.START_TRACKING);
        INSTANCE.register(PlayerTrackingEvents.Stop.class, EntityTrackingEvents.STOP_TRACKING, (PlayerTrackingEvents.Stop callback) -> {
            return callback::onStopTracking;
        });
        INSTANCE.register(PlayerNetworkEvents.LoggedIn.class, ServerPlayConnectionEvents.JOIN, (PlayerNetworkEvents.LoggedIn callback) -> {
            return (ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) -> {
                callback.onLoggedIn(handler.getPlayer());
            };
        });
        INSTANCE.register(PlayerNetworkEvents.LoggedOut.class, ServerPlayConnectionEvents.DISCONNECT, (PlayerNetworkEvents.LoggedOut callback) -> {
            return (ServerGamePacketListenerImpl handler, MinecraftServer server) -> {
                callback.onLoggedOut(handler.getPlayer());
            };
        });
        INSTANCE.register(AfterChangeDimensionCallback.class, ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD, (AfterChangeDimensionCallback callback) -> {
            return callback::onAfterChangeDimension;
        });
        INSTANCE.register(BabyEntitySpawnCallback.class, FabricLivingEvents.BABY_ENTITY_SPAWN);
        INSTANCE.register(AnimalTameCallback.class, FabricLivingEvents.ANIMAL_TAME);
        INSTANCE.register(LivingAttackCallback.class, FabricLivingEvents.LIVING_ATTACK);
        INSTANCE.register(PlayerCopyEvents.Copy.class, ServerPlayerEvents.COPY_FROM, (PlayerCopyEvents.Copy callback) -> {
            return callback::onCopy;
        });
        INSTANCE.register(PlayerCopyEvents.Respawn.class, ServerPlayerEvents.AFTER_RESPAWN, (PlayerCopyEvents.Respawn callback) -> {
            return (ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) -> {
                callback.onRespawn(newPlayer, alive);
            };
        });
        INSTANCE.register(ServerTickEvents.Start.class, net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_SERVER_TICK, (ServerTickEvents.Start callback) -> {
            return callback::onStartServerTick;
        });
        INSTANCE.register(ServerTickEvents.End.class, net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK, (ServerTickEvents.End callback) -> {
            return callback::onEndServerTick;
        });
        INSTANCE.register(ServerLevelTickEvents.Start.class, net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_WORLD_TICK, (ServerLevelTickEvents.Start callback) -> {
            return (ServerLevel level) -> {
                callback.onStartLevelTick(level.getServer(), level);
            };
        });
        INSTANCE.register(ServerLevelTickEvents.End.class, net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK, (ServerLevelTickEvents.End callback) -> {
            return (ServerLevel level) -> {
                callback.onEndLevelTick(level.getServer(), level);
            };
        });
        INSTANCE.register(ServerLevelEvents.Load.class, ServerWorldEvents.LOAD, (ServerLevelEvents.Load callback) -> {
            return callback::onLevelLoad;
        });
        INSTANCE.register(ServerLevelEvents.Unload.class, ServerWorldEvents.UNLOAD, (ServerLevelEvents.Unload callback) -> {
            return callback::onLevelUnload;
        });
        INSTANCE.register(ServerChunkEvents.Load.class, net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_LOAD, (ServerChunkEvents.Load callback) -> {
            return callback::onChunkLoad;
        });
        INSTANCE.register(ServerChunkEvents.Unload.class, net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_UNLOAD, (ServerChunkEvents.Unload callback) -> {
            return callback::onChunkUnload;
        });
        INSTANCE.register(ItemEntityEvents.Toss.class, FabricPlayerEvents.ITEM_TOSS);
        INSTANCE.register(LivingKnockBackCallback.class, FabricLivingEvents.LIVING_KNOCK_BACK);
        INSTANCE.register(ProjectileImpactCallback.class, FabricEntityEvents.PROJECTILE_IMPACT);
        INSTANCE.register(BreakSpeedCallback.class, FabricPlayerEvents.BREAK_SPEED);
        INSTANCE.register(MobEffectEvents.Affects.class, FabricLivingEvents.MOB_EFFECT_AFFECTS);
        INSTANCE.register(MobEffectEvents.Apply.class, FabricLivingEvents.MOB_EFFECT_APPLY);
        INSTANCE.register(MobEffectEvents.Remove.class, FabricLivingEvents.MOB_EFFECT_REMOVE);
        INSTANCE.register(MobEffectEvents.Expire.class, FabricLivingEvents.MOB_EFFECT_EXPIRE);
        INSTANCE.register(LivingJumpCallback.class, FabricLivingEvents.LIVING_JUMP);
        INSTANCE.register(LivingVisibilityCallback.class, FabricLivingEvents.LIVING_VISIBILITY);
        INSTANCE.register(LivingChangeTargetCallback.class, FabricLivingEvents.LIVING_CHANGE_TARGET);
        INSTANCE.register(CheckMobDespawnCallback.class, FabricLivingEvents.CHECK_MOB_DESPAWN);
        INSTANCE.register(GatherPotentialSpawnsCallback.class, FabricLevelEvents.GATHER_POTENTIAL_SPAWNS);
        INSTANCE.register(EntityRidingEvents.Start.class, FabricEntityEvents.ENTITY_START_RIDING);
        INSTANCE.register(EntityRidingEvents.Stop.class, FabricEntityEvents.ENTITY_STOP_RIDING);
        INSTANCE.register(GrindstoneEvents.Update.class, FabricPlayerEvents.GRINDSTONE_UPDATE);
        INSTANCE.register(GrindstoneEvents.Use.class, FabricPlayerEvents.GRINDSTONE_USE);
        INSTANCE.register(ServerChunkEvents.Watch.class, FabricLevelEvents.WATCH_CHUNK);
        INSTANCE.register(ServerChunkEvents.Unwatch.class, FabricLevelEvents.UNWATCH_CHUNK);
        INSTANCE.register(LivingEquipmentChangeCallback.class, ServerEntityEvents.EQUIPMENT_CHANGE, (LivingEquipmentChangeCallback callback) -> {
            return callback::onLivingEquipmentChange;
        });
        INSTANCE.register(LivingConversionCallback.class, ServerLivingEntityEvents.MOB_CONVERSION, (LivingConversionCallback callback) -> {
            return (Mob previous, Mob converted, ConversionParams conversionContext) -> {
                callback.onLivingConversion(previous, converted);
            };
        });
        INSTANCE.register(ContainerEvents.Open.class, FabricPlayerEvents.CONTAINER_OPEN);
        INSTANCE.register(ContainerEvents.Close.class, FabricPlayerEvents.CONTAINER_CLOSE);
        INSTANCE.register(LookingAtEndermanCallback.class, FabricLivingEvents.LOOKING_AT_ENDERMAN);
        INSTANCE.register(RegisterPotionBrewingMixesCallback.class, FabricBrewingRecipeRegistryBuilder.BUILD, (RegisterPotionBrewingMixesCallback callback) -> {
            return (PotionBrewing.Builder builder) -> {
                callback.onRegisterPotionBrewingMixes(new FabricPotionBrewingBuilder(builder));
            };
        });
        INSTANCE.register(RegisterFuelValuesCallback.class, FuelRegistryEvents.BUILD, (RegisterFuelValuesCallback callback) -> {
            return (FuelValues.Builder builder, FuelRegistryEvents.Context context) -> {
                callback.onRegisterFuelValues(builder, context.baseSmeltTime());
            };
        });
        INSTANCE.register(ChangeEntitySizeCallback.class, FabricEntityEvents.CHANGE_ENTITY_SIZE);
        INSTANCE.register(PickProjectileCallback.class, FabricLivingEvents.PICK_PROJECTILE);
        INSTANCE.register(EnderPearlTeleportCallback.class, FabricEntityEvents.ENDER_PEARL_TELEPORT);
        INSTANCE.register(BuildCreativeModeTabContentsCallback.class,
                ItemGroupEvents.ModifyEntries.class, (BuildCreativeModeTabContentsCallback callback, @Nullable Object context) -> {
            return (FabricItemGroupEntries entries) -> {
                Objects.requireNonNull(context, "context is null");
                ResourceKey<CreativeModeTab> resourceKey = (ResourceKey<CreativeModeTab>) context;
                CreativeModeTab creativeModeTab = entries.getContext()
                        .holders()
                        .lookupOrThrow(Registries.CREATIVE_MODE_TAB)
                        .getOrThrow(resourceKey)
                        .value();
                callback.onBuildCreativeModeTabContents(creativeModeTab, entries.getContext(), entries);
            };
                }, (Object context, Consumer<Event<ItemGroupEvents.ModifyEntries>> applyToInvoker, Consumer<Event<ItemGroupEvents.ModifyEntries>> removeInvoker) -> {
            Objects.requireNonNull(context, "context is null");
            ResourceKey<CreativeModeTab> resourceKey = (ResourceKey<CreativeModeTab>) context;
            applyToInvoker.accept(ItemGroupEvents.modifyEntriesEvent(resourceKey));
        }, UnaryOperator.identity(), false);
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            FabricClientEventInvokers.registerEventHandlers();
        }
    }

    @Override
    public <T, E> void register(Class<T> clazz, Event<E> event, FabricEventContextConverter<T, E> converter, UnaryOperator<EventPhase> eventPhaseConverter, boolean joinInvokers) {
        Objects.requireNonNull(clazz, "type is null");
        Objects.requireNonNull(event, "event is null");
        Objects.requireNonNull(converter, "converter is null");
        EventInvokerImpl.register(clazz, new FabricEventInvoker<>(event, converter, eventPhaseConverter), joinInvokers);
    }

    @Override
    public <T, E> void register(Class<T> clazz, Class<E> eventType, FabricEventContextConverter<T, E> converter, FabricEventContextConsumer<E> consumer, UnaryOperator<EventPhase> eventPhaseConverter, boolean joinInvokers) {
        Objects.requireNonNull(clazz, "type is null");
        Objects.requireNonNull(eventType, "event type is null");
        Objects.requireNonNull(converter, "converter is null");
        Objects.requireNonNull(consumer, "consumer is null");
        EventInvokerImpl.register(clazz, new FabricForwardingEventInvoker<>(converter, consumer, eventPhaseConverter), joinInvokers);
    }

    private record FabricEventInvoker<T, E>(Event<E> event, FabricEventContextConverter<T, E> converter, UnaryOperator<EventPhase> eventPhaseConverter, Set<EventPhase> knownEventPhases) implements EventInvoker<T>, EventInvokerImpl.EventInvokerLike<T> {

        public FabricEventInvoker(Event<E> event, FabricEventContextConverter<T, E> converter, UnaryOperator<EventPhase> eventPhaseConverter) {
            this(event, converter, eventPhaseConverter, Collections.synchronizedSet(Sets.newIdentityHashSet()));
        }

        @Override
        public EventInvoker<T> asEventInvoker(@Nullable Object context) {
            return context != null ? (EventPhase phase, T callback) -> {
                this.register(phase, callback, context);
            } : this;
        }

        @Override
        public void register(EventPhase phase, T callback) {
            this.register(phase, callback, null);
        }

        private void register(EventPhase phase, T callback, @Nullable Object context) {
            Objects.requireNonNull(phase, "phase is null");
            Objects.requireNonNull(callback, "callback is null");
            phase = this.eventPhaseConverter.apply(phase);
            // this is the default phase
            if (phase.parent() == null) {
                this.event.register(this.converter.apply(callback, context));
            } else {
                // make sure phase has consumer phase ordering, we keep track of phases we have already added an ordering for in this event in #knownEventPhases
                this.testEventPhase(phase);
                this.event.register(phase.resourceLocation(), this.converter.apply(callback, context));
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

    private record FabricForwardingEventInvoker<T, E>(Function<Event<E>, EventInvokerImpl.EventInvokerLike<T>> factory, FabricEventContextConsumer<E> consumer, Map<Event<E>, EventInvokerImpl.EventInvokerLike<T>> events) implements EventInvokerImpl.EventInvokerLike<T> {

        public FabricForwardingEventInvoker(FabricEventContextConverter<T, E> converter, FabricEventContextConsumer<E> consumer, UnaryOperator<EventPhase> eventPhaseConverter) {
            this((Event<E> event) -> new FabricEventInvoker<>(event, converter, eventPhaseConverter), consumer, new MapMaker().weakKeys().concurrencyLevel(1).makeMap());
        }

        @Override
        public EventInvoker<T> asEventInvoker(Object context) {
            Objects.requireNonNull(context, "context is null");
            // keeping track of events and corresponding invoker is not so important since there is only ever one event per context anyway which is guaranteed by the underlying implementation
            // but for managing event phases it becomes necessary to use our FabricEventInvoker to keep track
            return (EventPhase phase, T callback) -> {
                this.consumer.accept(context, (Event<E> event) -> {
                    this.events.computeIfAbsent(event, this.factory).asEventInvoker(context).register(phase, callback);
                }, this.events::remove);
            };
        }
    }
}
