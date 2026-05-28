package fuzs.puzzleslib.fabric.impl.event;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.common.api.event.v1.*;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.common.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.common.api.event.v1.entity.*;
import fuzs.puzzleslib.common.api.event.v1.entity.living.*;
import fuzs.puzzleslib.common.api.event.v1.entity.player.*;
import fuzs.puzzleslib.common.api.event.v1.level.*;
import fuzs.puzzleslib.common.api.event.v1.level.BlockEvents;
import fuzs.puzzleslib.common.api.event.v1.server.*;
import fuzs.puzzleslib.common.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.common.impl.PuzzlesLib;
import fuzs.puzzleslib.fabric.api.event.v1.*;
import fuzs.puzzleslib.fabric.impl.core.FabricProxy;
import fuzs.puzzleslib.fabric.impl.init.FabricPotionBrewingBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventInvokerRegistry.INSTANCE;

public final class FabricEventInvokers {
    public static final ThreadLocal<ReloadableServerResources> SERVER_RESOURCES = new ThreadLocal<>();

    private FabricEventInvokers() {
        // NO-OP
    }

    public static void registerLoadingHandlers() {
        INSTANCE.register(LoadCompleteCallback.class, FabricLifecycleEvents.LOAD_COMPLETE);
        INSTANCE.register(RegistryEntryAddedCallback.class, FabricEventInvokers::onRegistryEntryAdded);
        INSTANCE.register(AddBlockEntityTypeBlocksCallback.class,
                FabricLifecycleEvents.LOAD_COMPLETE,
                (AddBlockEntityTypeBlocksCallback callback) -> {
                    return () -> {
                        callback.onAddBlockEntityTypeBlocks((BlockEntityType<?> blockEntityType, Block block) -> {
                            blockEntityType.addValidBlock(block);
                        });
                    };
                });
        INSTANCE.register(CommonSetupCallback.class, (CommonSetupCallback callback, @Nullable Object context) -> {
            callback.onCommonSetup();
        });
        INSTANCE.register(RegisterConfigurationTasksCallback.class,
                ServerConfigurationConnectionEvents.CONFIGURE,
                (RegisterConfigurationTasksCallback callback) -> {
                    return (ServerConfigurationPacketListenerImpl handler, MinecraftServer server) -> {
                        callback.onRegisterConfigurationTasks(server, handler, handler::addTask);
                    };
                });
    }

    @SuppressWarnings("unchecked")
    private static <T> void onRegistryEntryAdded(RegistryEntryAddedCallback<T> callback, @Nullable Object context) {
        Objects.requireNonNull(context, "context is null");
        ResourceKey<? extends Registry<T>> resourceKey = (ResourceKey<? extends Registry<T>>) context;
        Registry<T> registry = LookupHelper.getRegistry(resourceKey).orElseThrow();
        BiConsumer<Identifier, Supplier<T>> registrar = (Identifier identifier, Supplier<T> supplier) -> {
            Registry.register(registry, identifier, supplier.get());
        };
        net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback.event(registry)
                .register((int rawId, Identifier id, T object) -> {
                    callback.onRegistryEntryAdded(registry, id, object, registrar);
                });
        // do not register directly to prevent ConcurrentModificationException
        Map<Identifier, Supplier<T>> toRegister = Maps.newLinkedHashMap();
        for (Map.Entry<ResourceKey<T>, T> entry : registry.entrySet()) {
            callback.onRegistryEntryAdded(registry, entry.getKey().identifier(), entry.getValue(), toRegister::put);
        }
        toRegister.forEach(registrar);
    }

    public static void registerEventHandlers() {
        INSTANCE.register(ServerResourcesLoadCallback.class,
                CommonLifecycleEvents.TAGS_LOADED,
                (ServerResourcesLoadCallback callback) -> {
                    return (RegistryAccess registries, boolean client) -> {
                        if (!client) {
                            ReloadableServerResources serverResources = SERVER_RESOURCES.get();
                            if (serverResources != null) {
                                callback.onServerResourcesLoad(serverResources, registries);
                            } else {
                                PuzzlesLib.LOGGER.warn("Missing server resources for {}",
                                        ServerResourcesLoadCallback.class.getSimpleName());
                            }
                        }
                    };
                });
        INSTANCE.register(RegisterCommandsCallback.class,
                CommandRegistrationCallback.EVENT,
                (RegisterCommandsCallback callback) -> {
                    return callback::onRegisterCommands;
                });
        INSTANCE.register(fuzs.puzzleslib.common.api.event.v1.server.ServerLifecycleEvents.Starting.class,
                ServerLifecycleEvents.SERVER_STARTING,
                (fuzs.puzzleslib.common.api.event.v1.server.ServerLifecycleEvents.Starting callback) -> {
                    return callback::onServerStarting;
                });
        INSTANCE.register(PlayerInteractEvents.UseBlock.class,
                UseBlockCallback.EVENT,
                (PlayerInteractEvents.UseBlock callback) -> {
                    return (Player player, Level level, InteractionHand interactionHand, BlockHitResult hitResult) -> {
                        EventResultHolder<InteractionResult> eventResult = callback.onUseBlock(player,
                                level,
                                interactionHand,
                                hitResult);
                        return FabricPlayerInteraction.USE_BLOCK.getHandledInteractionResult(eventResult,
                                player,
                                level,
                                interactionHand,
                                null,
                                hitResult);
                    };
                });
        INSTANCE.register(PlayerInteractEvents.AttackBlock.class,
                AttackBlockCallback.EVENT,
                (PlayerInteractEvents.AttackBlock callback) -> {
                    return (Player player, Level level, InteractionHand interactionHand, BlockPos pos, Direction direction) -> {
                        if (!level.isClientSide() || player.isCreative() || FabricProxy.get()
                                .shouldStartDestroyBlock(pos)) {
                            EventResult eventResult = callback.onAttackBlock(player,
                                    level,
                                    interactionHand,
                                    pos,
                                    direction);
                            // this brings parity with Forge where the server is notified regardless of the returned InteractionResult (achieved by returning InteractionResult#SUCCESS) since the Forge event runs after the server packet is sent
                            // returning InteractionResult#SUCCESS will return true from MultiPlayerGameMode::continueDestroyBlock which will spawn breaking particles and make the player arm swing
                            return eventResult.isInterrupt() ? InteractionResult.SUCCESS : InteractionResult.PASS;
                        } else {
                            return InteractionResult.PASS;
                        }
                    };
                });
        INSTANCE.register(PlayerInteractEvents.UseItem.class,
                UseItemCallback.EVENT,
                (PlayerInteractEvents.UseItem callback) -> {
                    return (Player player, Level level, InteractionHand interactionHand) -> {
                        // parity with Forge, eventResult item stack does not matter for Fabric implementation when eventResult is pass
                        if (player.isSpectator()) {
                            return InteractionResult.PASS;
                        } else if (player.getCooldowns().isOnCooldown(player.getItemInHand(interactionHand))) {
                            return InteractionResult.PASS;
                        }

                        EventResultHolder<InteractionResult> eventResult = callback.onUseItem(player,
                                level,
                                interactionHand);
                        return FabricPlayerInteraction.USE_ITEM.getHandledInteractionResult(eventResult,
                                player,
                                level,
                                interactionHand,
                                null,
                                null);
                    };
                });
        INSTANCE.register(PlayerInteractEvents.UseEntity.class,
                UseEntityCallback.EVENT,
                (PlayerInteractEvents.UseEntity callback) -> {
                    return (Player player, Level level, InteractionHand interactionHand, Entity entity, @Nullable EntityHitResult hitResult) -> {
                        // The hit result should never be able to pass as null.
                        if (hitResult == null) {
                            return InteractionResult.PASS;
                        }

                        EventResultHolder<InteractionResult> eventResult = callback.onUseEntity(player,
                                level,
                                interactionHand,
                                entity,
                                hitResult.getLocation());
                        return FabricPlayerInteraction.USE_ENTITY.getHandledInteractionResult(eventResult,
                                player,
                                level,
                                interactionHand,
                                entity,
                                hitResult);
                    };
                });
        INSTANCE.register(PlayerInteractEvents.AttackEntity.class,
                AttackEntityCallback.EVENT,
                (PlayerInteractEvents.AttackEntity callback) -> {
                    return (Player player, Level level, InteractionHand interactionHand, Entity entity, @Nullable EntityHitResult hitResult) -> {
                        EventResult eventResult = callback.onAttackEntity(player, level, interactionHand, entity);
                        // this isn't a proper item use callback (seen with the server-side and Forge implementations), so the return looks a little odd
                        // we return InteractionResult#SUCCESS so the packet is sent to the server either way so the server may handle this on its own as Forge does
                        return eventResult.isInterrupt() ? InteractionResult.SUCCESS : InteractionResult.PASS;
                    };
                });
        INSTANCE.register(PickupExperienceCallback.class, FabricPlayerEvents.PICKUP_EXPERIENCE);
        INSTANCE.register(UseBoneMealCallback.class, FabricLevelEvents.USE_BONE_MEAL);
        INSTANCE.register(LivingExperienceDropCallback.class, FabricLivingEvents.EXPERIENCE_DROP);
        INSTANCE.register(fuzs.puzzleslib.common.api.event.v1.level.BlockEvents.Break.class,
                PlayerBlockBreakEvents.BEFORE,
                (fuzs.puzzleslib.common.api.event.v1.level.BlockEvents.Break callback) -> {
                    return (Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) -> {
                        if (!(level instanceof ServerLevel serverLevel)) {
                            return true;
                        }

                        if (!(player instanceof ServerPlayer serverPlayer)) {
                            return true;
                        }

                        return callback.onBreakBlock(serverLevel, pos, state, serverPlayer, player.getMainHandItem())
                                .isPass();
                    };
                });
        INSTANCE.register(BlockEvents.DropExperience.class, FabricLevelEvents.DROP_BLOCK_EXPERIENCE);
        INSTANCE.register(PlayerTickEvents.Start.class, FabricPlayerEvents.PLAYER_TICK_START);
        INSTANCE.register(PlayerTickEvents.End.class, FabricPlayerEvents.PLAYER_TICK_END);
        INSTANCE.register(LivingFallCallback.class, FabricLivingEvents.LIVING_FALL);
        INSTANCE.register(LootTableLoadCallback.class, LootTableEvents.MODIFY, (LootTableLoadCallback callback) -> {
            return (ResourceKey<LootTable> key, LootTable.Builder tableBuilder, LootTableSource source, HolderLookup.Provider registries) -> {
                callback.onLootTableLoad(key.identifier(), tableBuilder, registries);
            };
        });
        INSTANCE.register(ItemEntityEvents.Touch.class, FabricPlayerEvents.ITEM_TOUCH);
        INSTANCE.register(ItemEntityEvents.Pickup.class, FabricPlayerEvents.ITEM_PICKUP);
        INSTANCE.register(CreateAnvilResultCallback.class, FabricPlayerEvents.CREATE_ANVIL_RESULT);
        INSTANCE.register(CreateGrindstoneResultCallback.class, FabricPlayerEvents.CREATE_GRINDSTONE_RESULT);
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
        INSTANCE.register(SyncDataPackContentsCallback.class,
                ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS,
                (SyncDataPackContentsCallback callback) -> {
                    return callback::onSyncDataPackContents;
                });
        INSTANCE.register(fuzs.puzzleslib.common.api.event.v1.server.ServerLifecycleEvents.Started.class,
                ServerLifecycleEvents.SERVER_STARTED,
                (fuzs.puzzleslib.common.api.event.v1.server.ServerLifecycleEvents.Started callback) -> {
                    return callback::onServerStarted;
                });
        INSTANCE.register(fuzs.puzzleslib.common.api.event.v1.server.ServerLifecycleEvents.Stopping.class,
                ServerLifecycleEvents.SERVER_STOPPING,
                (fuzs.puzzleslib.common.api.event.v1.server.ServerLifecycleEvents.Stopping callback) -> {
                    return callback::onServerStopping;
                });
        INSTANCE.register(fuzs.puzzleslib.common.api.event.v1.server.ServerLifecycleEvents.Stopped.class,
                ServerLifecycleEvents.SERVER_STOPPED,
                (fuzs.puzzleslib.common.api.event.v1.server.ServerLifecycleEvents.Stopped callback) -> {
                    return callback::onServerStopped;
                });
        INSTANCE.register(PlaySoundEvents.AtPosition.class, FabricLevelEvents.PLAY_SOUND_AT_POSITION);
        INSTANCE.register(PlaySoundEvents.AtEntity.class, FabricLevelEvents.PLAY_SOUND_AT_ENTITY);
        INSTANCE.register(ServerEntityEvents.Join.class,
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ALLOW_LOAD,
                (ServerEntityEvents.Join callback) -> {
                    return (Entity entity, ServerLevel level, @Nullable EntitySpawnReason spawnReason, boolean isLoadedFromDisk) -> {
                        return callback.onEntityJoin(entity, level, isLoadedFromDisk, spawnReason).isPass();
                    };
                });
        INSTANCE.register(ServerEntityEvents.Load.class,
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD,
                (ServerEntityEvents.Load callback) -> {
                    return (Entity entity, ServerLevel level) -> {
                        callback.onEntityLoad(entity, level, entity.isLoadedFromDisk(), entity.spawnReason());
                    };
                });
        INSTANCE.register(ServerEntityEvents.Unload.class,
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_UNLOAD,
                (ServerEntityEvents.Unload callback) -> {
                    return callback::onEntityUnload;
                });
        INSTANCE.register(LivingDeathCallback.class, FabricLivingEvents.LIVING_DEATH);
        INSTANCE.register(PlayerTrackingEvents.Start.class,
                EntityTrackingEvents.START_TRACKING,
                (PlayerTrackingEvents.Start callback) -> {
                    return callback::onStartTracking;
                });
        INSTANCE.register(PlayerTrackingEvents.Stop.class,
                EntityTrackingEvents.STOP_TRACKING,
                (PlayerTrackingEvents.Stop callback) -> {
                    return callback::onStopTracking;
                });
        INSTANCE.register(PlayerNetworkEvents.Join.class,
                ServerPlayerEvents.JOIN,
                (PlayerNetworkEvents.Join callback) -> {
                    return callback::onPlayerJoin;
                });
        INSTANCE.register(PlayerNetworkEvents.Leave.class,
                ServerPlayerEvents.LEAVE,
                (PlayerNetworkEvents.Leave callback) -> {
                    return callback::onPlayerLeave;
                });
        INSTANCE.register(AfterChangeDimensionCallback.class,
                ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL,
                (AfterChangeDimensionCallback callback) -> {
                    return callback::onAfterChangeDimension;
                });
        INSTANCE.register(BabyEntitySpawnCallback.class, FabricLivingEvents.BABY_ENTITY_SPAWN);
        INSTANCE.register(AnimalTameCallback.class, FabricLivingEvents.ANIMAL_TAME);
        INSTANCE.register(LivingAttackCallback.class,
                ServerLivingEntityEvents.ALLOW_DAMAGE,
                (LivingAttackCallback callback) -> {
                    return (LivingEntity entity, DamageSource source, float amount) -> {
                        return callback.onLivingAttack(entity, source, amount).isPass();
                    };
                });
        INSTANCE.register(PlayerCopyEvents.Copy.class,
                ServerPlayerEvents.COPY_FROM,
                (PlayerCopyEvents.Copy callback) -> {
                    return callback::onCopy;
                });
        INSTANCE.register(PlayerCopyEvents.Respawn.class,
                ServerPlayerEvents.AFTER_RESPAWN,
                (PlayerCopyEvents.Respawn callback) -> {
                    return (ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) -> {
                        callback.onRespawn(newPlayer, alive);
                    };
                });
        INSTANCE.register(ServerTickEvents.Start.class,
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_SERVER_TICK,
                (ServerTickEvents.Start callback) -> {
                    return callback::onStartServerTick;
                });
        INSTANCE.register(ServerTickEvents.End.class,
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK,
                (ServerTickEvents.End callback) -> {
                    return callback::onEndServerTick;
                });
        INSTANCE.register(ServerLevelTickEvents.Start.class,
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_LEVEL_TICK,
                (ServerLevelTickEvents.Start callback) -> {
                    return (ServerLevel serverLevel) -> {
                        callback.onStartLevelTick(serverLevel.getServer(), serverLevel);
                    };
                });
        INSTANCE.register(ServerLevelTickEvents.End.class,
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_LEVEL_TICK,
                (ServerLevelTickEvents.End callback) -> {
                    return (ServerLevel serverLevel) -> {
                        callback.onEndLevelTick(serverLevel.getServer(), serverLevel);
                    };
                });
        INSTANCE.register(ServerLevelEvents.Load.class,
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents.LOAD,
                (ServerLevelEvents.Load callback) -> {
                    return callback::onLevelLoad;
                });
        INSTANCE.register(ServerLevelEvents.Unload.class,
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents.UNLOAD,
                (ServerLevelEvents.Unload callback) -> {
                    return callback::onLevelUnload;
                });
        INSTANCE.register(ServerChunkEvents.Load.class,
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_LOAD,
                (ServerChunkEvents.Load callback) -> {
                    return callback::onChunkLoad;
                });
        INSTANCE.register(ServerChunkEvents.Unload.class,
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_UNLOAD,
                (ServerChunkEvents.Unload callback) -> {
                    return callback::onChunkUnload;
                });
        INSTANCE.register(ItemEntityEvents.Toss.class, FabricPlayerEvents.ITEM_TOSS);
        INSTANCE.register(LivingKnockBackCallback.class, FabricLivingEvents.LIVING_KNOCK_BACK);
        INSTANCE.register(ProjectileImpactCallback.class, FabricEntityEvents.PROJECTILE_IMPACT);
        INSTANCE.register(CalculateBlockBreakSpeedCallback.class, FabricPlayerEvents.CALCULATE_BLOCK_BREAK_SPEED);
        INSTANCE.register(MobEffectEvents.Affects.class, FabricLivingEvents.MOB_EFFECT_AFFECTS);
        INSTANCE.register(MobEffectEvents.Apply.class, FabricLivingEvents.MOB_EFFECT_APPLY);
        INSTANCE.register(MobEffectEvents.Remove.class, FabricLivingEvents.MOB_EFFECT_REMOVE);
        INSTANCE.register(MobEffectEvents.Expire.class, FabricLivingEvents.MOB_EFFECT_EXPIRE);
        INSTANCE.register(LivingJumpCallback.class, FabricLivingEvents.LIVING_JUMP);
        INSTANCE.register(CalculateLivingVisibilityCallback.class, FabricLivingEvents.CALCULATE_LIVING_VISIBILITY);
        INSTANCE.register(LivingChangeTargetCallback.class, FabricLivingEvents.LIVING_CHANGE_TARGET);
        INSTANCE.register(CheckMobDespawnCallback.class, FabricLivingEvents.CHECK_MOB_DESPAWN);
        INSTANCE.register(GatherPotentialSpawnsCallback.class, FabricLevelEvents.GATHER_POTENTIAL_SPAWNS);
        INSTANCE.register(EntityRidingEvents.Start.class, FabricEntityEvents.ENTITY_START_RIDING);
        INSTANCE.register(EntityRidingEvents.Stop.class, FabricEntityEvents.ENTITY_STOP_RIDING);
        INSTANCE.register(ServerChunkEvents.Watch.class, FabricLevelEvents.WATCH_CHUNK);
        INSTANCE.register(ServerChunkEvents.Unwatch.class, FabricLevelEvents.UNWATCH_CHUNK);
        INSTANCE.register(LivingEquipmentChangeCallback.class,
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.EQUIPMENT_CHANGE,
                (LivingEquipmentChangeCallback callback) -> {
                    return callback::onLivingEquipmentChange;
                });
        INSTANCE.register(LivingConversionCallback.class,
                ServerLivingEntityEvents.MOB_CONVERSION,
                (LivingConversionCallback callback) -> {
                    return (Mob previous, Mob converted, ConversionParams conversionContext) -> {
                        callback.onLivingConversion(previous, converted);
                    };
                });
        INSTANCE.register(ContainerEvents.Open.class, FabricPlayerEvents.CONTAINER_OPEN);
        INSTANCE.register(ContainerEvents.Close.class, FabricPlayerEvents.CONTAINER_CLOSE);
        INSTANCE.register(LookingAtEndermanCallback.class, FabricLivingEvents.LOOKING_AT_ENDERMAN);
        INSTANCE.register(RegisterPotionBrewingMixesCallback.class,
                net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder.BUILD,
                (RegisterPotionBrewingMixesCallback callback) -> {
                    return (PotionBrewing.Builder builder) -> {
                        callback.onRegisterPotionBrewingMixes(new FabricPotionBrewingBuilder(builder));
                    };
                });
        INSTANCE.register(RefreshEntityDimensionsCallback.class, FabricEntityEvents.REFRESH_ENTITY_DIMENSIONS);
        INSTANCE.register(PickProjectileCallback.class, FabricLivingEvents.PICK_PROJECTILE);
        INSTANCE.register(EnderPearlTeleportCallback.class, FabricEntityEvents.ENDER_PEARL_TELEPORT);
        INSTANCE.register(BuildCreativeModeTabContentsCallback.class,
                CreativeModeTabEvents.ModifyOutput.class,
                (BuildCreativeModeTabContentsCallback callback, @Nullable Object context) -> {
                    return (FabricCreativeModeTabOutput output) -> {
                        Objects.requireNonNull(context, "context is null");
                        ResourceKey<CreativeModeTab> resourceKey = (ResourceKey<CreativeModeTab>) context;
                        CreativeModeTab creativeModeTab = output.getContext()
                                .holders()
                                .lookupOrThrow(Registries.CREATIVE_MODE_TAB)
                                .getOrThrow(resourceKey)
                                .value();
                        callback.onBuildCreativeModeTabContents(creativeModeTab, output.getContext(), output);
                    };
                },
                (Object context, Consumer<Event<CreativeModeTabEvents.ModifyOutput>> applyToInvoker, Consumer<Event<CreativeModeTabEvents.ModifyOutput>> removeInvoker) -> {
                    Objects.requireNonNull(context, "context is null");
                    ResourceKey<CreativeModeTab> resourceKey = (ResourceKey<CreativeModeTab>) context;
                    applyToInvoker.accept(CreativeModeTabEvents.modifyOutputEvent(resourceKey));
                },
                UnaryOperator.identity(),
                false);
        INSTANCE.register(GameRuleUpdatedCallback.class,
                GameRuleEvents.ValueUpdate.class,
                (GameRuleUpdatedCallback callback, @Nullable Object context) -> {
                    return (GameRuleEvents.ValueUpdate<?>) (Object value, MinecraftServer server) -> {
                        Objects.requireNonNull(context, "context is null");
                        GameRule<?> gameRule = (GameRule<?>) context;
                        callback.onGameRuleUpdated(server, gameRule, value);
                    };
                },
                (Object context, Consumer<Event<GameRuleEvents.ValueUpdate>> applyToInvoker, Consumer<Event<GameRuleEvents.ValueUpdate>> removeInvoker) -> {
                    Objects.requireNonNull(context, "context is null");
                    GameRule<?> gameRule = (GameRule<?>) context;
                    applyToInvoker.accept((Event<GameRuleEvents.ValueUpdate>) (Event<?>) GameRuleEvents.changeCallback(
                            gameRule));
                },
                UnaryOperator.identity(),
                false);
        INSTANCE.register(StopSleepInBedCallback.class, FabricPlayerEvents.STOP_SLEEP_IN_BED);
        INSTANCE.register(EntityDamageImmunityCallback.class, FabricEntityEvents.ENTITY_DAMAGE_IMMUNITY);
    }
}
