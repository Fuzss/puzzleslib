package fuzs.puzzleslib.impl.event;

import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.api.event.v1.core.*;
import fuzs.puzzleslib.api.event.v1.data.*;
import fuzs.puzzleslib.api.event.v1.entity.EntityRidingEvents;
import fuzs.puzzleslib.api.event.v1.entity.ProjectileImpactCallback;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityLevelEvents;
import fuzs.puzzleslib.api.event.v1.entity.living.*;
import fuzs.puzzleslib.api.event.v1.entity.player.*;
import fuzs.puzzleslib.api.event.v1.level.*;
import fuzs.puzzleslib.api.event.v1.server.*;
import fuzs.puzzleslib.impl.client.event.ForgeClientEventInvokers;
import fuzs.puzzleslib.impl.event.core.EventInvokerImpl;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ForgeEventInvokerRegistryImpl implements ForgeEventInvokerRegistry {
    public static final ForgeEventInvokerRegistryImpl INSTANCE = new ForgeEventInvokerRegistryImpl();

    public static void register() {
        INSTANCE.register(PlayerInteractEvents.UseBlock.class, PlayerInteractEvent.RightClickBlock.class, (PlayerInteractEvents.UseBlock callback, PlayerInteractEvent.RightClickBlock evt) -> {
            EventResultHolder<InteractionResult> result = callback.onUseBlock(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getHitVec());
            // this is done for parity with Fabric where InteractionResult#PASS cannot be cancelled
            Optional<InteractionResult> optional = result.getInterrupt().filter(t -> t != InteractionResult.PASS);
            if (optional.isPresent()) {
                evt.setCancellationResult(optional.get());
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerInteractEvents.AttackBlock.class, PlayerInteractEvent.LeftClickBlock.class, (PlayerInteractEvents.AttackBlock callback, PlayerInteractEvent.LeftClickBlock evt) -> {
            EventResult result = callback.onAttackBlock(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getPos(), evt.getFace());
            if (result.isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerInteractEvents.UseItem.class, PlayerInteractEvent.RightClickItem.class, (PlayerInteractEvents.UseItem callback, PlayerInteractEvent.RightClickItem evt) -> {
            EventResultHolder<InteractionResultHolder<ItemStack>> result = callback.onUseItem(evt.getEntity(), evt.getLevel(), evt.getHand());
            // this is done for parity with Fabric where InteractionResult#PASS cannot be cancelled
            Optional<InteractionResult> optional = result.getInterrupt().map(InteractionResultHolder::getResult).filter(t -> t != InteractionResult.PASS);
            if (optional.isPresent()) {
                evt.setCancellationResult(optional.get());
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerInteractEvents.UseEntity.class, PlayerInteractEvent.EntityInteract.class, (PlayerInteractEvents.UseEntity callback, PlayerInteractEvent.EntityInteract evt) -> {
            EventResultHolder<InteractionResult> result = callback.onUseEntity(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getTarget());
            // this is done for parity with Fabric where InteractionResult#PASS cannot be cancelled
            Optional<InteractionResult> optional = result.getInterrupt().filter(t -> t != InteractionResult.PASS);
            if (optional.isPresent()) {
                evt.setCancellationResult(optional.get());
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerInteractEvents.UseEntityAt.class, PlayerInteractEvent.EntityInteractSpecific.class, (PlayerInteractEvents.UseEntityAt callback, PlayerInteractEvent.EntityInteractSpecific evt) -> {
            EventResultHolder<InteractionResult> result = callback.onUseEntityAt(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getTarget(), evt.getLocalPos());
            // this is done for parity with Fabric where InteractionResult#PASS cannot be cancelled
            Optional<InteractionResult> optional = result.getInterrupt().filter(t -> t != InteractionResult.PASS);
            if (optional.isPresent()) {
                evt.setCancellationResult(optional.get());
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerInteractEvents.AttackEntity.class, AttackEntityEvent.class, (PlayerInteractEvents.AttackEntity callback, AttackEntityEvent evt) -> {
            if (callback.onAttackEntity(evt.getEntity(), evt.getEntity().level(), InteractionHand.MAIN_HAND, evt.getTarget()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerXpEvents.PickupXp.class, PlayerXpEvent.PickupXp.class, (PlayerXpEvents.PickupXp callback, PlayerXpEvent.PickupXp evt) -> {
            if (callback.onPickupXp(evt.getEntity(), evt.getOrb()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(BonemealCallback.class, BonemealEvent.class, (BonemealCallback callback, BonemealEvent evt) -> {
            EventResult result = callback.onBonemeal(evt.getLevel(), evt.getPos(), evt.getBlock(), evt.getStack());
            // cancelling bone meal event is kinda weird...
            if (result.isInterrupt()) {
                if (result.getAsBoolean()) {
                    evt.setResult(Event.Result.ALLOW);
                } else {
                    evt.setCanceled(true);
                }
            }
        });
        INSTANCE.register(LivingExperienceDropCallback.class, LivingExperienceDropEvent.class, (LivingExperienceDropCallback callback, LivingExperienceDropEvent evt) -> {
            DefaultedInt droppedExperience = DefaultedInt.fromEvent(evt::setDroppedExperience, evt::getDroppedExperience, evt::getOriginalExperience);
            if (callback.onLivingExperienceDrop(evt.getEntity(), evt.getAttackingPlayer(), droppedExperience).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(BlockEvents.FarmlandTrample.class, BlockEvent.FarmlandTrampleEvent.class, (BlockEvents.FarmlandTrample callback, BlockEvent.FarmlandTrampleEvent evt) -> {
            if (callback.onFarmlandTrample((Level) evt.getLevel(), evt.getPos(), evt.getState(), evt.getFallDistance(), evt.getEntity()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerTickEvents.Start.class, TickEvent.PlayerTickEvent.class, (PlayerTickEvents.Start callback, TickEvent.PlayerTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.START) return;
            callback.onStartPlayerTick(evt.player);
        });
        INSTANCE.register(PlayerTickEvents.End.class, TickEvent.PlayerTickEvent.class, (PlayerTickEvents.End callback, TickEvent.PlayerTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.END) return;
            callback.onEndPlayerTick(evt.player);
        });
        INSTANCE.register(LivingFallCallback.class, LivingFallEvent.class, (LivingFallCallback callback, LivingFallEvent evt) -> {
            MutableFloat fallDistance = MutableFloat.fromEvent(evt::setDistance, evt::getDistance);
            MutableFloat damageMultiplier = MutableFloat.fromEvent(evt::setDamageMultiplier, evt::getDamageMultiplier);
            if (callback.onLivingFall(evt.getEntity(), fallDistance, damageMultiplier).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(RegisterCommandsCallback.class, RegisterCommandsEvent.class, (RegisterCommandsCallback callback, RegisterCommandsEvent evt) -> {
            callback.onRegisterCommands(evt.getDispatcher(), evt.getBuildContext(), evt.getCommandSelection());
        });
        INSTANCE.register(LootTableLoadEvents.Replace.class, LootTableLoadEvent.class, (LootTableLoadEvents.Replace callback, LootTableLoadEvent evt) -> {
            MutableValue<LootTable> table = MutableValue.fromEvent(evt::setTable, evt::getTable);
            callback.onReplaceLootTable(evt.getName(), table);
        });
        INSTANCE.register(LootTableLoadEvents.Modify.class, LootTableModifyEvent.class, (LootTableLoadEvents.Modify callback, LootTableModifyEvent evt) -> {
            callback.onModifyLootTable(evt.getLootDataManager(), evt.getIdentifier(), evt::addPool, evt::removePool);
        });
        INSTANCE.register(AnvilRepairCallback.class, AnvilRepairEvent.class, (AnvilRepairCallback callback, AnvilRepairEvent evt) -> {
            MutableFloat breakChance = MutableFloat.fromEvent(evt::setBreakChance, evt::getBreakChance);
            callback.onAnvilRepair(evt.getEntity(), evt.getLeft(), evt.getRight(), evt.getOutput(), breakChance);
        });
        INSTANCE.register(ItemTouchCallback.class, EntityItemPickupEvent.class, (ItemTouchCallback callback, EntityItemPickupEvent evt) -> {
            EventResult result = callback.onItemTouch(evt.getEntity(), evt.getItem());
            if (result.isInterrupt()) {
                if (result.getAsBoolean()) {
                    evt.setResult(Event.Result.ALLOW);
                } else {
                    evt.setCanceled(true);
                }
            }
        });
        INSTANCE.register(PlayerEvents.ItemPickup.class, PlayerEvent.ItemPickupEvent.class, (PlayerEvents.ItemPickup callback, PlayerEvent.ItemPickupEvent evt) -> {
            callback.onItemPickup(evt.getEntity(), evt.getOriginalEntity(), evt.getStack());
        });
        INSTANCE.register(LootingLevelCallback.class, LootingLevelEvent.class, (LootingLevelCallback callback, LootingLevelEvent evt) -> {
            MutableInt lootingLevel = MutableInt.fromEvent(evt::setLootingLevel, evt::getLootingLevel);
            callback.onLootingLevel(evt.getEntity(), evt.getDamageSource(), lootingLevel);
        });
        INSTANCE.register(AnvilUpdateCallback.class, AnvilUpdateEvent.class, (AnvilUpdateCallback callback, AnvilUpdateEvent evt) -> {
            MutableValue<ItemStack> output = MutableValue.fromEvent(evt::setOutput, evt::getOutput);
            MutableInt enchantmentCost = MutableInt.fromEvent(evt::setCost, evt::getCost);
            MutableInt materialCost = MutableInt.fromEvent(evt::setMaterialCost, evt::getMaterialCost);
            EventResult result = callback.onAnvilUpdate(evt.getLeft(), evt.getRight(), output, evt.getName(), enchantmentCost, materialCost, evt.getPlayer());
            if (result.isInterrupt()) {
                // interruption for allow will run properly as long as output is changed from an empty stack
                if (!result.getAsBoolean()) {
                    evt.setCanceled(true);
                }
            } else {
                // revert to an empty stack to allow vanilla behavior to execute
                evt.setOutput(ItemStack.EMPTY);
            }
        });
        INSTANCE.register(LivingDropsCallback.class, LivingDropsEvent.class, (LivingDropsCallback callback, LivingDropsEvent evt) -> {
            if (callback.onLivingDrops(evt.getEntity(), evt.getSource(), evt.getDrops(), evt.getLootingLevel(), evt.isRecentlyHit()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(LivingEvents.Tick.class, LivingEvent.LivingTickEvent.class, (LivingEvents.Tick callback, LivingEvent.LivingTickEvent evt) -> {
            if (callback.onLivingTick(evt.getEntity()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(ArrowLooseCallback.class, ArrowLooseEvent.class, (ArrowLooseCallback callback, ArrowLooseEvent evt) -> {
            MutableInt charge = MutableInt.fromEvent(evt::setCharge, evt::getCharge);
            if (callback.onArrowLoose(evt.getEntity(), evt.getBow(), evt.getLevel(), charge, evt.hasAmmo()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(LivingHurtCallback.class, LivingHurtEvent.class, (LivingHurtCallback callback, LivingHurtEvent evt) -> {
            MutableFloat amount = MutableFloat.fromEvent(evt::setAmount, evt::getAmount);
            if (callback.onLivingHurt(evt.getEntity(), evt.getSource(), amount).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(UseItemEvents.Start.class, LivingEntityUseItemEvent.Start.class, (UseItemEvents.Start callback, LivingEntityUseItemEvent.Start evt) -> {
            MutableInt useDuration = MutableInt.fromEvent(evt::setDuration, evt::getDuration);
            if (callback.onUseItemStart(evt.getEntity(), evt.getItem(), useDuration).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(UseItemEvents.Tick.class, LivingEntityUseItemEvent.Tick.class, (UseItemEvents.Tick callback, LivingEntityUseItemEvent.Tick evt) -> {
            MutableInt useItemRemaining = MutableInt.fromEvent(evt::setDuration, evt::getDuration);
            if (callback.onUseItemTick(evt.getEntity(), evt.getItem(), useItemRemaining).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(UseItemEvents.Stop.class, LivingEntityUseItemEvent.Stop.class, (UseItemEvents.Stop callback, LivingEntityUseItemEvent.Stop evt) -> {
            // Forge event also supports changing duration, but it remains unused
            if (callback.onUseItemStop(evt.getEntity(), evt.getItem(), evt.getDuration()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(UseItemEvents.Finish.class, LivingEntityUseItemEvent.Finish.class, (UseItemEvents.Finish callback, LivingEntityUseItemEvent.Finish evt) -> {
            MutableValue<ItemStack> stack = MutableValue.fromEvent(evt::setResultStack, evt::getResultStack);
            callback.onUseItemFinish(evt.getEntity(), stack, evt.getDuration(), evt.getItem());
        });
        INSTANCE.register(ShieldBlockCallback.class, ShieldBlockEvent.class, (ShieldBlockCallback callback, ShieldBlockEvent evt) -> {
            DefaultedFloat blockedDamage = DefaultedFloat.fromEvent(evt::setBlockedDamage, evt::getBlockedDamage, evt::getOriginalBlockedDamage);
            // Forge event can also prevent the shield taking durability damage, but that's hard to implement...
            if (callback.onShieldBlock(evt.getEntity(), evt.getDamageSource(), blockedDamage).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(TagsUpdatedCallback.class, TagsUpdatedEvent.class, (TagsUpdatedCallback callback, TagsUpdatedEvent evt) -> {
            callback.onTagsUpdated(evt.getRegistryAccess(), evt.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED);
        });
        INSTANCE.register(ExplosionEvents.Start.class, ExplosionEvent.Start.class, (ExplosionEvents.Start callback, ExplosionEvent.Start evt) -> {
            if (callback.onExplosionStart(evt.getLevel(), evt.getExplosion()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(ExplosionEvents.Detonate.class, ExplosionEvent.Detonate.class, (ExplosionEvents.Detonate callback, ExplosionEvent.Detonate evt) -> {
            callback.onExplosionDetonate(evt.getLevel(), evt.getExplosion(), evt.getAffectedBlocks(), evt.getAffectedEntities());
        });
        INSTANCE.register(SyncDataPackContentsCallback.class, OnDatapackSyncEvent.class, (SyncDataPackContentsCallback callback, OnDatapackSyncEvent evt) -> {
            if (evt.getPlayer() != null) {
                callback.onSyncDataPackContents(evt.getPlayer(), true);
                return;
            }
            for (ServerPlayer player : evt.getPlayerList().getPlayers()) {
                callback.onSyncDataPackContents(player, false);
            }
        });
        INSTANCE.register(ServerLifecycleEvents.ServerStarting.class, ServerStartingEvent.class, (ServerLifecycleEvents.ServerStarting callback, ServerStartingEvent evt) -> {
            callback.onServerStarting(evt.getServer());
        });
        INSTANCE.register(ServerLifecycleEvents.ServerStarted.class, ServerStartedEvent.class, (ServerLifecycleEvents.ServerStarted callback, ServerStartedEvent evt) -> {
            callback.onServerStarted(evt.getServer());
        });
        INSTANCE.register(ServerLifecycleEvents.ServerStopping.class, ServerStoppingEvent.class, (ServerLifecycleEvents.ServerStopping callback, ServerStoppingEvent evt) -> {
            callback.onServerStopping(evt.getServer());
        });
        INSTANCE.register(ServerLifecycleEvents.ServerStopped.class, ServerStoppedEvent.class, (ServerLifecycleEvents.ServerStopped callback, ServerStoppedEvent evt) -> {
            callback.onServerStopped(evt.getServer());
        });
        INSTANCE.register(PlayLevelSoundEvents.AtPosition.class, PlayLevelSoundEvent.AtPosition.class, (PlayLevelSoundEvents.AtPosition callback, PlayLevelSoundEvent.AtPosition evt) -> {
            MutableValue<Holder<SoundEvent>> sound = MutableValue.fromEvent(evt::setSound, evt::getSound);
            MutableValue<SoundSource> source = MutableValue.fromEvent(evt::setSource, evt::getSource);
            DefaultedFloat volume = DefaultedFloat.fromEvent(evt::setNewVolume, evt::getNewVolume, evt::getOriginalVolume);
            DefaultedFloat pitch = DefaultedFloat.fromEvent(evt::setNewPitch, evt::getNewPitch, evt::getOriginalPitch);
            if (callback.onPlaySoundAtPosition(evt.getLevel(), evt.getPosition(), sound, source, volume, pitch).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayLevelSoundEvents.AtEntity.class, PlayLevelSoundEvent.AtEntity.class, (PlayLevelSoundEvents.AtEntity callback, PlayLevelSoundEvent.AtEntity evt) -> {
            MutableValue<Holder<SoundEvent>> sound = MutableValue.fromEvent(evt::setSound, evt::getSound);
            MutableValue<SoundSource> source = MutableValue.fromEvent(evt::setSource, evt::getSource);
            DefaultedFloat volume = DefaultedFloat.fromEvent(evt::setNewVolume, evt::getNewVolume, evt::getOriginalVolume);
            DefaultedFloat pitch = DefaultedFloat.fromEvent(evt::setNewPitch, evt::getNewPitch, evt::getOriginalPitch);
            if (callback.onPlaySoundAtEntity(evt.getLevel(), evt.getEntity(), sound, source, volume, pitch).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(ServerEntityLevelEvents.Load.class, EntityJoinLevelEvent.class, (ServerEntityLevelEvents.Load callback, EntityJoinLevelEvent evt) -> {
            if (evt.getLevel().isClientSide) return;
            if (callback.onEntityLoad(evt.getEntity(), (ServerLevel) evt.getLevel(), !evt.loadedFromDisk() && evt.getEntity() instanceof Mob mob ? mob.getSpawnType() : null).isInterrupt()) {
                if (evt.getEntity() instanceof Player) {
                    // we do not support players as it isn't as straight-forward to implement for the server event on Fabric
                    throw new UnsupportedOperationException("Cannot prevent player from spawning in!");
                } else {
                    evt.setCanceled(true);
                }
            }
        });
        INSTANCE.register(ServerEntityLevelEvents.LoadV2.class, EntityJoinLevelEvent.class, (ServerEntityLevelEvents.LoadV2 callback, EntityJoinLevelEvent evt) -> {
            if (evt.getLevel().isClientSide || !evt.loadedFromDisk()) return;
            if (callback.onEntityLoad(evt.getEntity(), (ServerLevel) evt.getLevel()).isInterrupt()) {
                if (evt.getEntity() instanceof Player) {
                    // we do not support players as it isn't as straight-forward to implement for the server event on Fabric
                    throw new UnsupportedOperationException("Cannot prevent player from loading in!");
                } else {
                    evt.setCanceled(true);
                }
            }
        });
        INSTANCE.register(ServerEntityLevelEvents.Spawn.class, EntityJoinLevelEvent.class, (ServerEntityLevelEvents.Spawn callback, EntityJoinLevelEvent evt) -> {
            if (evt.getLevel().isClientSide || evt.loadedFromDisk()) return;
            if (callback.onEntitySpawn(evt.getEntity(), (ServerLevel) evt.getLevel(), evt.getEntity() instanceof Mob mob ? mob.getSpawnType() : null).isInterrupt()) {
                if (evt.getEntity() instanceof Player) {
                    // we do not support players as it isn't as straight-forward to implement for the server event on Fabric
                    throw new UnsupportedOperationException("Cannot prevent player from spawning in!");
                } else {
                    evt.setCanceled(true);
                }
            }
        });
        INSTANCE.register(ServerEntityLevelEvents.Remove.class, EntityLeaveLevelEvent.class, (ServerEntityLevelEvents.Remove callback, EntityLeaveLevelEvent evt) -> {
            if (evt.getLevel().isClientSide) return;
            callback.onEntityRemove(evt.getEntity(), (ServerLevel) evt.getLevel());
        });
        INSTANCE.register(LivingDeathCallback.class, LivingDeathEvent.class, (LivingDeathCallback callback, LivingDeathEvent evt) -> {
            EventResult result = callback.onLivingDeath(evt.getEntity(), evt.getSource());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(PlayerEvents.StartTracking.class, PlayerEvent.StartTracking.class, (PlayerEvents.StartTracking callback, PlayerEvent.StartTracking evt) -> {
            callback.onStartTracking(evt.getTarget(), (ServerPlayer) evt.getEntity());
        });
        INSTANCE.register(PlayerEvents.StopTracking.class, PlayerEvent.StopTracking.class, (PlayerEvents.StopTracking callback, PlayerEvent.StopTracking evt) -> {
            callback.onStopTracking(evt.getTarget(), (ServerPlayer) evt.getEntity());
        });
        INSTANCE.register(PlayerEvents.LoggedIn.class, PlayerEvent.PlayerLoggedInEvent.class, (PlayerEvents.LoggedIn callback, PlayerEvent.PlayerLoggedInEvent evt) -> {
            callback.onLoggedIn((ServerPlayer) evt.getEntity());
        });
        INSTANCE.register(PlayerEvents.LoggedOut.class, PlayerEvent.PlayerLoggedOutEvent.class, (PlayerEvents.LoggedOut callback, PlayerEvent.PlayerLoggedOutEvent evt) -> {
            callback.onLoggedOut((ServerPlayer) evt.getEntity());
        });
        INSTANCE.register(PlayerEvents.AfterChangeDimension.class, PlayerEvent.PlayerChangedDimensionEvent.class, (PlayerEvents.AfterChangeDimension callback, PlayerEvent.PlayerChangedDimensionEvent evt) -> {
            MinecraftServer server = Proxy.INSTANCE.getGameServer();
            ServerLevel from = server.getLevel(evt.getFrom());
            ServerLevel to = server.getLevel(evt.getTo());
            Objects.requireNonNull(from, "level origin is null");
            Objects.requireNonNull(to, "level destination is null");
            callback.onAfterChangeDimension((ServerPlayer) evt.getEntity(), from, to);
        });
        INSTANCE.register(BabyEntitySpawnCallback.class, BabyEntitySpawnEvent.class, (BabyEntitySpawnCallback callback, BabyEntitySpawnEvent evt) -> {
            MutableValue<AgeableMob> child = MutableValue.fromEvent(evt::setChild, evt::getChild);
            if (callback.onBabyEntitySpawn(evt.getParentA(), evt.getParentB(), child).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(AnimalTameCallback.class, AnimalTameEvent.class, (AnimalTameCallback callback, AnimalTameEvent evt) -> {
            if (callback.onAnimalTame(evt.getAnimal(), evt.getTamer()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(LivingAttackCallback.class, LivingAttackEvent.class, (LivingAttackCallback callback, LivingAttackEvent evt) -> {
            if (callback.onLivingAttack(evt.getEntity(), evt.getSource(), evt.getAmount()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerEvents.Copy.class, PlayerEvent.Clone.class, (PlayerEvents.Copy callback, PlayerEvent.Clone evt) -> {
            evt.getOriginal().reviveCaps();
            callback.onCopy((ServerPlayer) evt.getOriginal(), (ServerPlayer) evt.getEntity(), !evt.isWasDeath());
            evt.getOriginal().invalidateCaps();
        });
        INSTANCE.register(PlayerEvents.Respawn.class, PlayerEvent.PlayerRespawnEvent.class, (PlayerEvents.Respawn callback, PlayerEvent.PlayerRespawnEvent evt) -> {
            callback.onRespawn((ServerPlayer) evt.getEntity(), evt.isEndConquered());
        });
        INSTANCE.register(ServerTickEvents.Start.class, TickEvent.ServerTickEvent.class, (ServerTickEvents.Start callback, TickEvent.ServerTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.START) return;
            callback.onStartServerTick(evt.getServer());
        });
        INSTANCE.register(ServerTickEvents.End.class, TickEvent.ServerTickEvent.class, (ServerTickEvents.End callback, TickEvent.ServerTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.END) return;
            callback.onEndServerTick(evt.getServer());
        });
        INSTANCE.register(ServerLevelTickEvents.Start.class, TickEvent.LevelTickEvent.class, (ServerLevelTickEvents.Start callback, TickEvent.LevelTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.START || !(evt.level instanceof ServerLevel level)) return;
            callback.onStartLevelTick(level.getServer(), level);
        });
        INSTANCE.register(ServerLevelTickEvents.End.class, TickEvent.LevelTickEvent.class, (ServerLevelTickEvents.End callback, TickEvent.LevelTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.END || !(evt.level instanceof ServerLevel level)) return;
            callback.onEndLevelTick(level.getServer(), level);
        });
        INSTANCE.register(ServerLevelEvents.Load.class, LevelEvent.Load.class, (ServerLevelEvents.Load callback, LevelEvent.Load evt) -> {
            if (!(evt.getLevel() instanceof ServerLevel level)) return;
            callback.onLevelLoad(level.getServer(), level);
        });
        INSTANCE.register(ServerLevelEvents.Unload.class, LevelEvent.Unload.class, (ServerLevelEvents.Unload callback, LevelEvent.Unload evt) -> {
            if (!(evt.getLevel() instanceof ServerLevel level)) return;
            callback.onLevelUnload(level.getServer(), level);
        });
        INSTANCE.register(ServerChunkEvents.Load.class, ChunkEvent.Load.class, (ServerChunkEvents.Load callback, ChunkEvent.Load evt) -> {
            if (!(evt.getLevel() instanceof ServerLevel level)) return;
            callback.onChunkLoad(level, evt.getChunk());
        });
        INSTANCE.register(ServerChunkEvents.Unload.class, ChunkEvent.Unload.class, (ServerChunkEvents.Unload callback, ChunkEvent.Unload evt) -> {
            if (!(evt.getLevel() instanceof ServerLevel level)) return;
            callback.onChunkUnload(level, evt.getChunk());
        });
        INSTANCE.register(ItemTossCallback.class, ItemTossEvent.class, (ItemTossCallback callback, ItemTossEvent evt) -> {
            if (callback.onItemToss(evt.getEntity(), evt.getPlayer()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(LivingKnockBackCallback.class, LivingKnockBackEvent.class, (LivingKnockBackCallback callback, LivingKnockBackEvent evt) -> {
            DefaultedDouble strength = DefaultedDouble.fromEvent(v -> evt.setStrength((float) v), evt::getStrength, evt::getOriginalStrength);
            DefaultedDouble ratioX = DefaultedDouble.fromEvent(evt::setRatioX, evt::getRatioX, evt::getOriginalRatioX);
            DefaultedDouble ratioZ = DefaultedDouble.fromEvent(evt::setRatioZ, evt::getRatioZ, evt::getOriginalRatioZ);
            if (callback.onLivingKnockBack(evt.getEntity(), strength, ratioX, ratioZ).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(ItemAttributeModifiersCallback.class, ItemAttributeModifierEvent.class, (ItemAttributeModifiersCallback callback, ItemAttributeModifierEvent evt) -> {
            Multimap<Attribute, AttributeModifier> attributeModifiers = new AttributeModifiersMultimap(evt::getModifiers, evt::addModifier, evt::removeModifier, evt::removeAttribute, evt::clearModifiers);
            callback.onItemAttributeModifiers(evt.getItemStack(), evt.getSlotType(), attributeModifiers, evt.getOriginalModifiers());
        });
        INSTANCE.register(ProjectileImpactCallback.class, ProjectileImpactEvent.class, (ProjectileImpactCallback callback, ProjectileImpactEvent evt) -> {
            if (callback.onProjectileImpact(evt.getProjectile(), evt.getRayTraceResult()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerEvents.BreakSpeed.class, PlayerEvent.BreakSpeed.class, (PlayerEvents.BreakSpeed callback, PlayerEvent.BreakSpeed evt) -> {
            DefaultedFloat breakSpeed = DefaultedFloat.fromEvent(evt::setNewSpeed, evt::getNewSpeed, evt::getOriginalSpeed);
            if (callback.onBreakSpeed(evt.getEntity(), evt.getState(), breakSpeed).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(MobEffectEvents.Affects.class, MobEffectEvent.Applicable.class, (MobEffectEvents.Affects callback, MobEffectEvent.Applicable evt) -> {
            EventResult result = callback.onMobEffectAffects(evt.getEntity(), evt.getEffectInstance());
            if (result.isInterrupt()) {
                evt.setResult(result.getAsBoolean() ? Event.Result.ALLOW : Event.Result.DENY);
            }
        });
        INSTANCE.register(MobEffectEvents.Apply.class, MobEffectEvent.Added.class, (MobEffectEvents.Apply callback, MobEffectEvent.Added evt) -> {
            callback.onMobEffectApply(evt.getEntity(), evt.getEffectInstance(), evt.getOldEffectInstance(), evt.getEffectSource());
        });
        INSTANCE.register(MobEffectEvents.Remove.class, MobEffectEvent.Remove.class, (MobEffectEvents.Remove callback, MobEffectEvent.Remove evt) -> {
            if (callback.onMobEffectRemove(evt.getEntity(), evt.getEffectInstance()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(MobEffectEvents.Expire.class, MobEffectEvent.Expired.class, (MobEffectEvents.Expire callback, MobEffectEvent.Expired evt) -> {
            callback.onMobEffectExpire(evt.getEntity(), evt.getEffectInstance());
        });
        INSTANCE.register(LivingEvents.Jump.class, LivingEvent.LivingJumpEvent.class, (LivingEvents.Jump callback, LivingEvent.LivingJumpEvent evt) -> {
            LivingJumpHelper.onLivingJump(callback, evt.getEntity());
        });
        INSTANCE.register(LivingEvents.Visibility.class, LivingEvent.LivingVisibilityEvent.class, (LivingEvents.Visibility callback, LivingEvent.LivingVisibilityEvent evt) -> {
            callback.onLivingVisibility(evt.getEntity(), evt.getLookingEntity(), MutableDouble.fromEvent(visibilityModifier -> {
                evt.modifyVisibility(visibilityModifier / evt.getVisibilityModifier());
            }, evt::getVisibilityModifier));
        });
        INSTANCE.register(LivingChangeTargetCallback.class, LivingChangeTargetEvent.class, (LivingChangeTargetCallback callback, LivingChangeTargetEvent evt) -> {
            DefaultedValue<LivingEntity> target = DefaultedValue.fromEvent(evt::setNewTarget, evt::getNewTarget, evt::getOriginalTarget);
            if (callback.onLivingChangeTarget(evt.getEntity(), target).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(LoadCompleteCallback.class, FMLLoadCompleteEvent.class, (LoadCompleteCallback callback, FMLLoadCompleteEvent evt) -> {
            callback.onLoadComplete();
        });
        INSTANCE.register(CheckMobDespawnCallback.class, MobSpawnEvent.AllowDespawn.class, (CheckMobDespawnCallback callback, MobSpawnEvent.AllowDespawn evt) -> {
            EventResult result = callback.onCheckMobDespawn(evt.getEntity(), (ServerLevel) evt.getLevel());
            if (result.isInterrupt()) {
                evt.setResult(result.getAsBoolean() ? Event.Result.ALLOW : Event.Result.DENY);
            }
        });
        INSTANCE.register(GatherPotentialSpawnsCallback.class, LevelEvent.PotentialSpawns.class, (GatherPotentialSpawnsCallback callback, LevelEvent.PotentialSpawns evt) -> {
            ServerLevel level = (ServerLevel) evt.getLevel();
            List<MobSpawnSettings.SpawnerData> mobsAt = new PotentialSpawnsList<>(evt.getSpawnerDataList(), spawnerData -> {
                evt.addSpawnerData(spawnerData);
                return true;
            }, evt::removeSpawnerData);
            callback.onGatherPotentialSpawns(level, level.structureManager(), level.getChunkSource().getGenerator(), evt.getMobCategory(), evt.getPos(), mobsAt);
        });
        INSTANCE.register(EntityRidingEvents.Start.class, EntityMountEvent.class, (EntityRidingEvents.Start callback, EntityMountEvent evt) -> {
            if (evt.isDismounting()) return;
            if (callback.onStartRiding(evt.getLevel(), evt.getEntity(), evt.getEntityBeingMounted()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(EntityRidingEvents.Stop.class, EntityMountEvent.class, (EntityRidingEvents.Stop callback, EntityMountEvent evt) -> {
            if (evt.isMounting()) return;
            if (callback.onStopRiding(evt.getLevel(), evt.getEntity(), evt.getEntityBeingMounted()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            ForgeClientEventInvokers.register();
        }
    }

    @Override
    public <T, E extends Event> void register(Class<T> clazz, Class<E> event, ForgeEventContextConsumer<T, E> converter, boolean joinInvokers) {
        Objects.requireNonNull(clazz, "type is null");
        Objects.requireNonNull(converter, "converter is null");
        IEventBus eventBus;
        if (IModBusEvent.class.isAssignableFrom(event)) {
            // this will be null when an event is registered after the initial mod loading
            FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
            eventBus = context != null ? context.getModEventBus() : null;
        } else {
            eventBus = MinecraftForge.EVENT_BUS;
        }
        EventInvokerImpl.register(clazz, new ForgeEventInvoker<>(eventBus, event, converter), joinInvokers);
    }

    private record ForgeEventInvoker<T, E extends Event>(@Nullable IEventBus eventBus, Class<E> event, ForgeEventContextConsumer<T, E> converter) implements EventInvokerImpl.EventInvokerLike<T> {
        private static final Map<EventPhase, EventPriority> PHASE_TO_PRIORITY = Map.of(EventPhase.FIRST, EventPriority.HIGHEST, EventPhase.BEFORE, EventPriority.HIGH, EventPhase.DEFAULT, EventPriority.NORMAL, EventPhase.AFTER, EventPriority.LOW, EventPhase.LAST, EventPriority.LOWEST);

        @Override
        public EventInvoker<T> asEventInvoker(@Nullable Object context) {
            return (EventPhase phase, T callback) -> {
                this.register(phase, callback, context);
            };
        }

        private void register(EventPhase phase, T callback, @Nullable Object context) {
            Objects.requireNonNull(phase, "phase is null");
            Objects.requireNonNull(callback, "callback is null");
            EventPriority eventPriority = PHASE_TO_PRIORITY.getOrDefault(phase, EventPriority.NORMAL);
            IEventBus eventBus = this.eventBus;
            if (eventBus == null) {
                Objects.requireNonNull(context, "mod id context is null");
                eventBus = ModContainerHelper.findModEventBus((String) context).orElseThrow();
            }
            if (eventBus == MinecraftForge.EVENT_BUS || eventPriority == EventPriority.NORMAL) {
                // we don't support receiving cancelled events since the event api on Fabric is not designed for it
                eventBus.addListener(eventPriority, false, this.event, (E evt) -> this.converter.accept(callback, evt, context));
            } else {
                throw new IllegalStateException("mod event bus does not support event phases");
            }
        }
    }
}
