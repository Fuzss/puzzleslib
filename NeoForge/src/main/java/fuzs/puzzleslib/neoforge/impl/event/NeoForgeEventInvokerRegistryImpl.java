package fuzs.puzzleslib.neoforge.impl.event;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.api.event.v1.*;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.*;
import fuzs.puzzleslib.api.event.v1.entity.*;
import fuzs.puzzleslib.api.event.v1.entity.living.*;
import fuzs.puzzleslib.api.event.v1.entity.player.*;
import fuzs.puzzleslib.api.event.v1.level.*;
import fuzs.puzzleslib.api.event.v1.server.*;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.event.CopyOnWriteForwardingList;
import fuzs.puzzleslib.impl.event.EventImplHelper;
import fuzs.puzzleslib.impl.event.PotentialSpawnsList;
import fuzs.puzzleslib.impl.event.core.EventInvokerImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.api.event.v1.core.NeoForgeEventInvokerRegistry;
import fuzs.puzzleslib.neoforge.api.event.v1.entity.living.ComputeEnchantedLootBonusEvent;
import fuzs.puzzleslib.neoforge.impl.client.event.NeoForgeClientEventInvokers;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgePotionBrewingBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.*;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.*;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.ModifyRegistriesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.registries.callback.BakeCallback;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class NeoForgeEventInvokerRegistryImpl implements NeoForgeEventInvokerRegistry {
    private static boolean frozenModBusEvents;

    public static void registerLoadingHandlers() {
        INSTANCE.register(LoadCompleteCallback.class, FMLLoadCompleteEvent.class, (LoadCompleteCallback callback, FMLLoadCompleteEvent evt) -> {
            evt.enqueueWork(callback::onLoadComplete);
        });
        INSTANCE.register(RegistryEntryAddedCallback.class, ModifyRegistriesEvent.class, NeoForgeEventInvokerRegistryImpl::onRegistryEntryAdded);
        INSTANCE.register(FinalizeItemComponentsCallback.class, ModifyDefaultComponentsEvent.class, (FinalizeItemComponentsCallback callback, ModifyDefaultComponentsEvent evt) -> {
            evt.getAllItems().forEach((Item item) -> {
                callback.onFinalizeItemComponents(item, (Function<DataComponentMap, DataComponentPatch> function) -> {
                    evt.modify(item, (DataComponentPatch.Builder builder) -> {
                        DataComponentPatch.SplitResult splitResult = function.apply(item.components()).split();
                        splitResult.added().stream().forEach(builder::set);
                        splitResult.removed().forEach(builder::remove);
                    });
                });
            });
        });
        INSTANCE.register(ComputeItemAttributeModifiersCallback.class, ModifyDefaultComponentsEvent.class, (ComputeItemAttributeModifiersCallback callback, ModifyDefaultComponentsEvent evt) -> {
            evt.getAllItems().forEach((Item item) -> {
                ItemAttributeModifiers itemAttributeModifiers = item.components()
                        .getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
                CopyOnWriteForwardingList<ItemAttributeModifiers.Entry> entries = new CopyOnWriteForwardingList<>(
                        itemAttributeModifiers.modifiers());
                callback.onComputeItemAttributeModifiers(item, entries);
                if (entries.delegate() != itemAttributeModifiers.modifiers()) {
                    evt.modify(item, (DataComponentPatch.Builder builder) -> {
                        builder.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(ImmutableList.copyOf(entries), itemAttributeModifiers.showInTooltip()));
                    });
                }
            });
        });
        INSTANCE.register(AddBlockEntityTypeBlocksCallback.class, BlockEntityTypeAddBlocksEvent.class, (AddBlockEntityTypeBlocksCallback callback, BlockEntityTypeAddBlocksEvent evt) -> {
            callback.onAddBlockEntityTypeBlocks(evt::modify);
        });
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            NeoForgeClientEventInvokers.registerLoadingHandlers();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void onRegistryEntryAdded(RegistryEntryAddedCallback<T> callback, ModifyRegistriesEvent evt, @Nullable Object context) {
        Objects.requireNonNull(context, "context is null");
        ResourceKey<? extends Registry<T>> resourceKey = (ResourceKey<? extends Registry<T>>) context;
        Registry<T> registry = evt.getRegistry(resourceKey);
        boolean[] loadComplete = new boolean[1];
        registry.addCallback((AddCallback<T>) (Registry<T> callbackRegistry, int id, ResourceKey<T> key, T value) -> {
            if (!loadComplete[0]) {
                try {
                    callback.onRegistryEntryAdded(callbackRegistry, key.location(), value, onRegistryEntryAdded(registry));
                } catch (Exception exception) {
                    PuzzlesLib.LOGGER.error("Failed to run registry entry added callback", exception);
                }
            }
        });
        registry.addCallback((BakeCallback<T>) (Registry<T> registryx) -> {
            // prevent add callback from running after loading has completed, Forge still fires the callback when syncing registries,
            // but that doesn't allow for adding content
            loadComplete[0] = true;
        });
        // store all event invocations for vanilla game content already registered before the registration event runs
        // the add callback above won't trigger for those as they are already registered when mods are constructed
        // we cannot run those directly as registries are frozen when this fires
        Queue<Consumer<BiConsumer<ResourceLocation, Supplier<T>>>> callbacks = new LinkedList<>();
        for (Map.Entry<ResourceKey<T>, T> entry : registry.entrySet()) {
            callbacks.offer((BiConsumer<ResourceLocation, Supplier<T>> consumer) -> {
                try {
                    callback.onRegistryEntryAdded(registry, entry.getKey().location(), entry.getValue(), consumer);
                } catch (Exception exception) {
                    PuzzlesLib.LOGGER.error("Failed to run registry entry added callback", exception);
                }
            });
        }
        // active mod event bus is no longer available when ModifyRegistriesEvent fires,
        // so just use the Puzzles Lib event bus for everything
        IEventBus eventBus = NeoForgeModContainerHelper.getModEventBus(PuzzlesLib.MOD_ID);
        eventBus.addListener((final RegisterEvent evtx) -> {
            if (evtx.getRegistryKey() != resourceKey) return;
            Consumer<BiConsumer<ResourceLocation, Supplier<T>>> consumer;
            while ((consumer = callbacks.poll()) != null) {
                consumer.accept(onRegistryEntryAdded((Registry<T>) evtx.getRegistry()));
            }
        });
    }

    private static <T> BiConsumer<ResourceLocation, Supplier<T>> onRegistryEntryAdded(Registry<T> registry) {
        return (ResourceLocation resourceLocation, Supplier<T> supplier) -> {
            try {
                T t = supplier.get();
                Objects.requireNonNull(t, "entry is null");
                Registry.register(registry, resourceLocation, t);
            } catch (Exception exception) {
                PuzzlesLib.LOGGER.error("Failed to register new entry", exception);
            }
        };
    }

    public static void freezeModBusEvents() {
        frozenModBusEvents = true;
    }

    public static void registerEventHandlers() {
        INSTANCE.register(PlayerInteractEvents.UseBlock.class, PlayerInteractEvent.RightClickBlock.class, (PlayerInteractEvents.UseBlock callback, PlayerInteractEvent.RightClickBlock evt) -> {
            callback.onUseBlock(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getHitVec())
                    .ifInterrupt(interactionResult -> {
                evt.setCancellationResult(interactionResult);
                evt.setCanceled(true);
            });
        });
        INSTANCE.register(PlayerInteractEvents.AttackBlock.class, PlayerInteractEvent.LeftClickBlock.class, (PlayerInteractEvents.AttackBlock callback, PlayerInteractEvent.LeftClickBlock evt) -> {
            if (callback.onAttackBlock(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getPos(), evt.getFace()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerInteractEvents.UseItem.class, PlayerInteractEvent.RightClickItem.class, (PlayerInteractEvents.UseItem callback, PlayerInteractEvent.RightClickItem evt) -> {
            callback.onUseItem(evt.getEntity(), evt.getLevel(), evt.getHand()).ifInterrupt(interactionResult -> {
                evt.setCancellationResult(interactionResult);
                evt.setCanceled(true);
            });
        });
        INSTANCE.register(PlayerInteractEvents.UseEntity.class, PlayerInteractEvent.EntityInteract.class, (PlayerInteractEvents.UseEntity callback, PlayerInteractEvent.EntityInteract evt) -> {
            callback.onUseEntity(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getTarget())
                    .ifInterrupt(interactionResult -> {
                evt.setCancellationResult(interactionResult);
                evt.setCanceled(true);
            });
        });
        INSTANCE.register(PlayerInteractEvents.UseEntityAt.class, PlayerInteractEvent.EntityInteractSpecific.class, (PlayerInteractEvents.UseEntityAt callback, PlayerInteractEvent.EntityInteractSpecific evt) -> {
            callback.onUseEntityAt(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getTarget(), evt.getLocalPos())
                    .ifInterrupt(interactionResult -> {
                evt.setCancellationResult(interactionResult);
                evt.setCanceled(true);
            });
        });
        INSTANCE.register(PlayerInteractEvents.AttackEntity.class, AttackEntityEvent.class, (PlayerInteractEvents.AttackEntity callback, AttackEntityEvent evt) -> {
            if (callback.onAttackEntity(evt.getEntity(), evt.getEntity().level(), InteractionHand.MAIN_HAND, evt.getTarget()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PickupExperienceCallback.class, PlayerXpEvent.PickupXp.class, (PickupExperienceCallback callback, PlayerXpEvent.PickupXp evt) -> {
            if (callback.onPickupExperience(evt.getEntity(), evt.getOrb()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(UseBoneMealCallback.class, BonemealEvent.class, (UseBoneMealCallback callback, BonemealEvent evt) -> {
            EventResult result = callback.onUseBoneMeal(evt.getLevel(), evt.getPos(), evt.getState(), evt.getStack());
            if (result.isInterrupt()) {
                evt.setSuccessful(result.getAsBoolean());
            }
        });
        INSTANCE.register(LivingExperienceDropCallback.class, LivingExperienceDropEvent.class, (LivingExperienceDropCallback callback, LivingExperienceDropEvent evt) -> {
            DefaultedInt droppedExperience = DefaultedInt.fromEvent(evt::setDroppedExperience, evt::getDroppedExperience, evt::getOriginalExperience);
            if (callback.onLivingExperienceDrop(evt.getEntity(), evt.getAttackingPlayer(), droppedExperience).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(BlockEvents.Break.class, BlockEvent.BreakEvent.class, (BlockEvents.Break callback, BlockEvent.BreakEvent evt) -> {
            // match Fabric implementation
            if (evt.getState().getBlock() instanceof GameMasterBlock && !evt.getPlayer().canUseGameMasterBlocks()) {
                return;
            }
            GameType gameType = ((ServerPlayer) evt.getPlayer()).gameMode.getGameModeForPlayer();
            if (evt.getPlayer().blockActionRestricted((Level) evt.getLevel(), evt.getPos(), gameType)) {
                return;
            }
            EventResult result = callback.onBreakBlock((ServerLevel) evt.getLevel(), evt.getPos(), evt.getState(), evt.getPlayer(), evt.getPlayer().getMainHandItem());
            if (result.isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(BlockEvents.DropExperience.class, BlockDropsEvent.class, (BlockEvents.DropExperience callback, BlockDropsEvent evt) -> {
            if (!(evt.getBreaker() instanceof Player player)) return;
            MutableInt experienceAmount = MutableInt.fromEvent(evt::setDroppedExperience, evt::getDroppedExperience);
            callback.onDropExperience(evt.getLevel(), evt.getPos(), evt.getState(), player, evt.getTool(), experienceAmount);
        });
        INSTANCE.register(BlockEvents.FarmlandTrample.class, BlockEvent.FarmlandTrampleEvent.class, (BlockEvents.FarmlandTrample callback, BlockEvent.FarmlandTrampleEvent evt) -> {
            if (callback.onFarmlandTrample((Level) evt.getLevel(), evt.getPos(), evt.getState(), evt.getFallDistance(), evt.getEntity()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerTickEvents.Start.class, PlayerTickEvent.Pre.class, (PlayerTickEvents.Start callback, PlayerTickEvent.Pre evt) -> {
            callback.onStartPlayerTick(evt.getEntity());
        });
        INSTANCE.register(PlayerTickEvents.End.class, PlayerTickEvent.Post.class, (PlayerTickEvents.End callback, PlayerTickEvent.Post evt) -> {
            callback.onEndPlayerTick(evt.getEntity());
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
        INSTANCE.register(LootTableLoadEvents.Modify.class, LootTableLoadEvent.class, (LootTableLoadEvents.Modify callback, LootTableLoadEvent evt) -> {
            callback.onModifyLootTable(evt.getName(), evt.getTable()::addPool, (int index) -> {
                if (index == 0 && evt.getTable().removePool("main") != null) {
                    return true;
                } else {
                    return evt.getTable().removePool("pool" + index) != null;
                }
            });
        });
        INSTANCE.register(AnvilEvents.Use.class, AnvilRepairEvent.class, (AnvilEvents.Use callback, AnvilRepairEvent evt) -> {
            if (evt.getEntity().level().isClientSide) return;
            MutableFloat breakChance = MutableFloat.fromEvent(evt::setBreakChance, evt::getBreakChance);
            callback.onAnvilUse(evt.getEntity(), evt.getLeft(), evt.getRight(), evt.getOutput(), breakChance);
        });
        INSTANCE.register(ItemEntityEvents.Touch.class, ItemEntityPickupEvent.Pre.class, (ItemEntityEvents.Touch callback, ItemEntityPickupEvent.Pre evt) -> {
            EventResult result = callback.onItemTouch(evt.getPlayer(), evt.getItemEntity());
            if (result.isInterrupt()) {
                evt.setCanPickup(result.getAsBoolean() ? TriState.TRUE : TriState.FALSE);
            }
        });
        INSTANCE.register(ItemEntityEvents.Pickup.class, ItemEntityPickupEvent.Post.class, (ItemEntityEvents.Pickup callback, ItemEntityPickupEvent.Post evt) -> {
            callback.onItemPickup(evt.getPlayer(), evt.getItemEntity(), evt.getOriginalStack());
        });
        INSTANCE.register(ComputeEnchantedLootBonusCallback.class, ComputeEnchantedLootBonusEvent.class, (ComputeEnchantedLootBonusCallback callback, ComputeEnchantedLootBonusEvent evt) -> {
            MutableInt enchantmentLevel = MutableInt.fromEvent(evt::setEnchantmentLevel, evt::getEnchantmentLevel);
            callback.onComputeEnchantedLootBonus(evt.getEntity(), evt.getDamageSource(), evt.getEnchantment(),
                    enchantmentLevel
            );
        });
        INSTANCE.register(AnvilEvents.Update.class, AnvilUpdateEvent.class, (AnvilEvents.Update callback, AnvilUpdateEvent evt) -> {
            DefaultedValue<ItemStack> output = DefaultedValue.fromEventWithValue(evt::setOutput, evt::getOutput, evt.getOutput());
            DefaultedInt enchantmentCost = DefaultedInt.fromEventWithValue(evt::setCost, () -> (int) evt.getCost(), (int) evt.getCost());
            DefaultedInt materialCost = DefaultedInt.fromEventWithValue(evt::setMaterialCost, evt::getMaterialCost, evt.getMaterialCost());
            EventResult result = callback.onAnvilUpdate(evt.getLeft(), evt.getRight(), output, evt.getName(), enchantmentCost, materialCost, evt.getPlayer());
            if (result.isInterrupt()) {
                // interruption for allow will run properly as long as output is changed from an empty stack
                if (!result.getAsBoolean()) {
                    evt.setCanceled(true);
                }
            } else {
                // revert any changes made by us if the callback has not been cancelled
                evt.setOutput(output.getAsDefault());
                evt.setCost(enchantmentCost.getAsDefaultInt());
                evt.setMaterialCost(materialCost.getAsDefaultInt());
            }
        });
        INSTANCE.register(LivingDropsCallback.class, LivingDropsEvent.class, (LivingDropsCallback callback, LivingDropsEvent evt) -> {
            if (callback.onLivingDrops(evt.getEntity(), evt.getSource(), evt.getDrops(), evt.isRecentlyHit()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(EntityTickEvents.Start.class, EntityTickEvent.Pre.class, (EntityTickEvents.Start callback, EntityTickEvent.Pre evt) -> {
            if (callback.onStartEntityTick(evt.getEntity()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(EntityTickEvents.End.class, EntityTickEvent.Post.class, (EntityTickEvents.End callback, EntityTickEvent.Post evt) -> {
            callback.onEndEntityTick(evt.getEntity());
        });
        INSTANCE.register(ArrowLooseCallback.class, ArrowLooseEvent.class, (ArrowLooseCallback callback, ArrowLooseEvent evt) -> {
            MutableInt charge = MutableInt.fromEvent(evt::setCharge, evt::getCharge);
            if (callback.onArrowLoose(evt.getEntity(), evt.getBow(), evt.getLevel(), charge, evt.hasAmmo()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(LivingHurtCallback.class, LivingDamageEvent.Pre.class, (LivingHurtCallback callback, LivingDamageEvent.Pre evt) -> {
            MutableFloat damageAmount = MutableFloat.fromEvent(evt.getContainer()::setNewDamage, evt.getContainer()::getNewDamage);
            if (callback.onLivingHurt(evt.getEntity(), evt.getContainer().getSource(), damageAmount).isInterrupt()) {
                // this effectively cancels the event
                evt.getContainer().setNewDamage(0.0F);
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
        INSTANCE.register(ShieldBlockCallback.class, LivingShieldBlockEvent.class, (ShieldBlockCallback callback, LivingShieldBlockEvent evt) -> {
            DefaultedFloat blockedDamage = DefaultedFloat.fromEvent(evt::setBlockedDamage, evt::getBlockedDamage, evt::getOriginalBlockedDamage);
            // Forge event can also prevent the shield taking durability damage, but that's hard to implement...
            if (callback.onShieldBlock(evt.getEntity(), evt.getDamageSource(), blockedDamage).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(TagsUpdatedCallback.class, TagsUpdatedEvent.class, (TagsUpdatedCallback callback, TagsUpdatedEvent evt) -> {
            callback.onTagsUpdated(evt.getLookupProvider(), evt.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED);
        });
        INSTANCE.register(ExplosionEvents.Start.class, ExplosionEvent.Start.class, (ExplosionEvents.Start callback, ExplosionEvent.Start evt) -> {
            if (callback.onExplosionStart((ServerLevel) evt.getLevel(), evt.getExplosion()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(ExplosionEvents.Detonate.class, ExplosionEvent.Detonate.class, (ExplosionEvents.Detonate callback, ExplosionEvent.Detonate evt) -> {
            // TODO fix empty list when parameter is back in NeoForge
            callback.onExplosionDetonate((ServerLevel) evt.getLevel(), evt.getExplosion(), Collections.emptyList(), evt.getAffectedEntities());
        });
        INSTANCE.register(SyncDataPackContentsCallback.class, OnDatapackSyncEvent.class, (SyncDataPackContentsCallback callback, OnDatapackSyncEvent evt) -> {
            evt.getRelevantPlayers().forEach((ServerPlayer player) -> {
                callback.onSyncDataPackContents(player, evt.getPlayer() != null);
            });
        });
        INSTANCE.register(ServerLifecycleEvents.Starting.class, ServerAboutToStartEvent.class, (ServerLifecycleEvents.Starting callback, ServerAboutToStartEvent evt) -> {
            callback.onServerStarting(evt.getServer());
        });
        INSTANCE.register(ServerLifecycleEvents.Started.class, ServerStartedEvent.class, (ServerLifecycleEvents.Started callback, ServerStartedEvent evt) -> {
            callback.onServerStarted(evt.getServer());
        });
        INSTANCE.register(ServerLifecycleEvents.Stopping.class, ServerStoppingEvent.class, (ServerLifecycleEvents.Stopping callback, ServerStoppingEvent evt) -> {
            callback.onServerStopping(evt.getServer());
        });
        INSTANCE.register(ServerLifecycleEvents.Stopped.class, ServerStoppedEvent.class, (ServerLifecycleEvents.Stopped callback, ServerStoppedEvent evt) -> {
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
            if (callback.onEntityLoad(evt.getEntity(), (ServerLevel) evt.getLevel()).isInterrupt()) {
                if (evt.getEntity() instanceof Player) {
                    // we do not support players as it isn't as straight-forward to implement for the server player on Fabric
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
                    // we do not support players as it isn't as straight-forward to implement for the server player on Fabric
                    throw new UnsupportedOperationException("Cannot prevent player from spawning in!");
                } else {
                    evt.setCanceled(true);
                }
            }
        });
        INSTANCE.register(ServerEntityLevelEvents.Unload.class, EntityLeaveLevelEvent.class, (ServerEntityLevelEvents.Unload callback, EntityLeaveLevelEvent evt) -> {
            if (evt.getLevel().isClientSide) return;
            callback.onEntityUnload(evt.getEntity(), (ServerLevel) evt.getLevel());
        });
        INSTANCE.register(LivingDeathCallback.class, LivingDeathEvent.class, (LivingDeathCallback callback, LivingDeathEvent evt) -> {
            if (callback.onLivingDeath(evt.getEntity(), evt.getSource()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerTrackingEvents.Start.class, PlayerEvent.StartTracking.class, (PlayerTrackingEvents.Start callback, PlayerEvent.StartTracking evt) -> {
            callback.onStartTracking(evt.getTarget(), (ServerPlayer) evt.getEntity());
        });
        INSTANCE.register(PlayerTrackingEvents.Stop.class, PlayerEvent.StopTracking.class, (PlayerTrackingEvents.Stop callback, PlayerEvent.StopTracking evt) -> {
            callback.onStopTracking(evt.getTarget(), (ServerPlayer) evt.getEntity());
        });
        INSTANCE.register(PlayerNetworkEvents.LoggedIn.class, PlayerEvent.PlayerLoggedInEvent.class, (PlayerNetworkEvents.LoggedIn callback, PlayerEvent.PlayerLoggedInEvent evt) -> {
            callback.onLoggedIn((ServerPlayer) evt.getEntity());
        });
        INSTANCE.register(PlayerNetworkEvents.LoggedOut.class, PlayerEvent.PlayerLoggedOutEvent.class, (PlayerNetworkEvents.LoggedOut callback, PlayerEvent.PlayerLoggedOutEvent evt) -> {
            callback.onLoggedOut((ServerPlayer) evt.getEntity());
        });
        INSTANCE.register(AfterChangeDimensionCallback.class, PlayerEvent.PlayerChangedDimensionEvent.class, (AfterChangeDimensionCallback callback, PlayerEvent.PlayerChangedDimensionEvent evt) -> {
            MinecraftServer server = CommonAbstractions.INSTANCE.getMinecraftServer();
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
        INSTANCE.register(LivingAttackCallback.class, LivingIncomingDamageEvent.class, (LivingAttackCallback callback, LivingIncomingDamageEvent evt) -> {
            if (callback.onLivingAttack(evt.getEntity(), evt.getSource(), evt.getAmount()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerCopyEvents.Copy.class, PlayerEvent.Clone.class, (PlayerCopyEvents.Copy callback, PlayerEvent.Clone evt) -> {
            callback.onCopy((ServerPlayer) evt.getOriginal(), (ServerPlayer) evt.getEntity(), !evt.isWasDeath());
        });
        INSTANCE.register(PlayerCopyEvents.Respawn.class, PlayerEvent.PlayerRespawnEvent.class, (PlayerCopyEvents.Respawn callback, PlayerEvent.PlayerRespawnEvent evt) -> {
            callback.onRespawn((ServerPlayer) evt.getEntity(), evt.isEndConquered());
        });
        INSTANCE.register(ServerTickEvents.Start.class, ServerTickEvent.Pre.class, (ServerTickEvents.Start callback, ServerTickEvent.Pre evt) -> {
            callback.onStartServerTick(evt.getServer());
        });
        INSTANCE.register(ServerTickEvents.End.class, ServerTickEvent.Post.class, (ServerTickEvents.End callback, ServerTickEvent.Post evt) -> {
            callback.onEndServerTick(evt.getServer());
        });
        INSTANCE.register(ServerLevelTickEvents.Start.class, LevelTickEvent.Pre.class, (ServerLevelTickEvents.Start callback, LevelTickEvent.Pre evt) -> {
            if (!(evt.getLevel() instanceof ServerLevel level)) return;
            callback.onStartLevelTick(level.getServer(), level);
        });
        INSTANCE.register(ServerLevelTickEvents.End.class, LevelTickEvent.Post.class, (ServerLevelTickEvents.End callback, LevelTickEvent.Post evt) -> {
            if (!(evt.getLevel() instanceof ServerLevel level)) return;
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
            callback.onChunkLoad(level, (LevelChunk) evt.getChunk());
        });
        INSTANCE.register(ServerChunkEvents.Unload.class, ChunkEvent.Unload.class, (ServerChunkEvents.Unload callback, ChunkEvent.Unload evt) -> {
            if (!(evt.getLevel() instanceof ServerLevel level)) return;
            callback.onChunkUnload(level, (LevelChunk) evt.getChunk());
        });
        INSTANCE.register(ItemEntityEvents.Toss.class, ItemTossEvent.class, (ItemEntityEvents.Toss callback, ItemTossEvent evt) -> {
            if (callback.onItemToss(evt.getPlayer(), evt.getEntity()).isInterrupt()) {
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
        INSTANCE.register(ProjectileImpactCallback.class, ProjectileImpactEvent.class, (ProjectileImpactCallback callback, ProjectileImpactEvent evt) -> {
            if (callback.onProjectileImpact(evt.getProjectile(), evt.getRayTraceResult()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(BreakSpeedCallback.class, PlayerEvent.BreakSpeed.class, (BreakSpeedCallback callback, PlayerEvent.BreakSpeed evt) -> {
            DefaultedFloat breakSpeed = DefaultedFloat.fromEvent(evt::setNewSpeed, evt::getNewSpeed, evt::getOriginalSpeed);
            if (callback.onBreakSpeed(evt.getEntity(), evt.getState(), breakSpeed).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(MobEffectEvents.Affects.class, MobEffectEvent.Applicable.class, (MobEffectEvents.Affects callback, MobEffectEvent.Applicable evt) -> {
            EventResult result = callback.onMobEffectAffects(evt.getEntity(), evt.getEffectInstance());
            if (result.isInterrupt()) {
                evt.setResult(result.getAsBoolean() ? MobEffectEvent.Applicable.Result.APPLY : MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
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
        INSTANCE.register(LivingJumpCallback.class, LivingEvent.LivingJumpEvent.class, (LivingJumpCallback callback, LivingEvent.LivingJumpEvent evt) -> {
            EventImplHelper.onLivingJump(callback, evt.getEntity());
        });
        INSTANCE.register(LivingVisibilityCallback.class, LivingEvent.LivingVisibilityEvent.class, (LivingVisibilityCallback callback, LivingEvent.LivingVisibilityEvent evt) -> {
            callback.onLivingVisibility(evt.getEntity(), evt.getLookingEntity(), MutableDouble.fromEvent(visibilityModifier -> {
                evt.modifyVisibility(visibilityModifier / evt.getVisibilityModifier());
            }, evt::getVisibilityModifier));
        });
        INSTANCE.register(LivingChangeTargetCallback.class, LivingChangeTargetEvent.class, (LivingChangeTargetCallback callback, LivingChangeTargetEvent evt) -> {
            DefaultedValue<LivingEntity> target = DefaultedValue.fromEvent(evt::setNewAboutToBeSetTarget, evt::getNewAboutToBeSetTarget, evt::getOriginalAboutToBeSetTarget);
            if (callback.onLivingChangeTarget(evt.getEntity(), target).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(CheckMobDespawnCallback.class, MobDespawnEvent.class, (CheckMobDespawnCallback callback, MobDespawnEvent evt) -> {
            EventResult result = callback.onCheckMobDespawn(evt.getEntity(), (ServerLevel) evt.getLevel());
            if (result.isInterrupt()) {
                evt.setResult(result.getAsBoolean() ? MobDespawnEvent.Result.ALLOW : MobDespawnEvent.Result.DENY);
            }
        });
        INSTANCE.register(GatherPotentialSpawnsCallback.class, LevelEvent.PotentialSpawns.class, (GatherPotentialSpawnsCallback callback, LevelEvent.PotentialSpawns evt) -> {
            ServerLevel level = (ServerLevel) evt.getLevel();
            List<MobSpawnSettings.SpawnerData> mobs = new PotentialSpawnsList<>(evt::getSpawnerDataList, spawnerData -> {
                evt.addSpawnerData(spawnerData);
                return true;
            }, evt::removeSpawnerData);
            callback.onGatherPotentialSpawns(level, level.structureManager(), level.getChunkSource().getGenerator(), evt.getMobCategory(), evt.getPos(), mobs);
        });
        INSTANCE.register(EntityRidingEvents.Start.class, EntityMountEvent.class, (EntityRidingEvents.Start callback, EntityMountEvent evt) -> {
            if (evt.isDismounting()) return;
            // same implementation as Fabric
            if (!evt.getEntityMounting().canRide(evt.getEntityBeingMounted())) return;
            if (!evt.getEntityBeingMounted().canAddPassenger(evt.getEntityMounting())) return;
            if (callback.onStartRiding(evt.getLevel(), evt.getEntityMounting(), evt.getEntityBeingMounted()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(EntityRidingEvents.Stop.class, EntityMountEvent.class, (EntityRidingEvents.Stop callback, EntityMountEvent evt) -> {
            if (evt.isMounting()) return;
            if (callback.onStopRiding(evt.getLevel(), evt.getEntity(), evt.getEntityBeingMounted()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(GrindstoneEvents.Update.class, GrindstoneEvent.OnPlaceItem.class, (GrindstoneEvents.Update callback, GrindstoneEvent.OnPlaceItem evt) -> {
            ItemStack originalOutput = evt.getOutput();
            int originalExperienceReward = evt.getXp();
            MutableValue<ItemStack> output = MutableValue.fromEvent(evt::setOutput, evt::getOutput);
            MutableInt experienceReward = MutableInt.fromEvent(evt::setXp, evt::getXp);
            Player player = EventImplHelper.getGrindstoneUsingPlayer(evt.getTopItem(), evt.getBottomItem()).orElseThrow(NullPointerException::new);
            EventResult result = callback.onGrindstoneUpdate(evt.getTopItem(), evt.getBottomItem(), output, experienceReward, player);
            if (result.isInterrupt()) {
                // interruption for allow will run properly as long as output is changed from an empty stack
                if (!result.getAsBoolean()) {
                    evt.setCanceled(true);
                }
            } else {
                // revert any changes made by us if the callback has not been cancelled
                evt.setOutput(originalOutput);
                evt.setXp(originalExperienceReward);
            }
        });
        INSTANCE.register(GrindstoneEvents.Use.class, GrindstoneEvent.OnTakeItem.class, (GrindstoneEvents.Use callback, GrindstoneEvent.OnTakeItem evt) -> {
            DefaultedValue<ItemStack> topInput = DefaultedValue.fromValue(evt.getTopItem());
            // set new item set by other event listeners
            if (!evt.getNewTopItem().isEmpty()) topInput.accept(evt.getNewTopItem());
            DefaultedValue<ItemStack> bottomInput = DefaultedValue.fromValue(evt.getBottomItem());
            // set new item set by other event listeners
            if (!evt.getNewBottomItem().isEmpty()) bottomInput.accept(evt.getNewBottomItem());
            Player player = EventImplHelper.getGrindstoneUsingPlayer(evt.getTopItem(), evt.getBottomItem()).orElseThrow(NullPointerException::new);
            callback.onGrindstoneUse(topInput, bottomInput, player);
            topInput.getAsOptional().ifPresent(evt::setNewTopItem);
            bottomInput.getAsOptional().ifPresent(evt::setNewBottomItem);
        });
        INSTANCE.register(LivingBreathEvents.Breathe.class, LivingBreatheEvent.class, (LivingBreathEvents.Breathe callback, LivingBreatheEvent evt) -> {
            final int airAmountValue;
            if (evt.canBreathe()) {
                airAmountValue = evt.getRefillAirAmount();
            } else {
                airAmountValue = -evt.getConsumeAirAmount();
            }
            DefaultedInt airAmount = DefaultedInt.fromValue(airAmountValue);
            LivingEntity entity = evt.getEntity();
            // do not use LivingBreatheEvent::canBreathe, it is merged with LivingBreatheEvent::canRefillAir, so recalculate the value
            boolean canLoseAir = !entity.canDrownInFluidType(entity.getEyeInFluidType()) && !MobEffectUtil.hasWaterBreathing(entity) && (!(entity instanceof Player) || !((Player) entity).getAbilities().invulnerable);
            EventResult result = callback.onLivingBreathe(entity, airAmount, true, canLoseAir);
            if (result.isInterrupt()) {
                // just some trickery so the event does nothing
                evt.setConsumeAirAmount(0);
                evt.setRefillAirAmount(0);
            } else {
                OptionalInt optional = airAmount.getAsOptionalInt();
                if (optional.isPresent()) {
                    if (optional.getAsInt() < 0) {
                        evt.setCanBreathe(false);
                        evt.setConsumeAirAmount(Math.abs(optional.getAsInt()));
                        evt.setRefillAirAmount(0);
                    } else {
                        evt.setCanBreathe(true);
                        evt.setConsumeAirAmount(0);
                        evt.setRefillAirAmount(Math.abs(optional.getAsInt()));
                    }
                }
            }
        });
        INSTANCE.register(LivingBreathEvents.Drown.class, LivingDrownEvent.class, (LivingBreathEvents.Drown callback, LivingDrownEvent evt) -> {
            EventResult result = callback.onLivingDrown(evt.getEntity(), evt.getEntity().getAirSupply(), evt.isDrowning());
            if (result.isInterrupt()) {
                if (result.getAsBoolean()) {
                    evt.setDrowning(true);
                } else {
                    evt.setCanceled(true);
                }
            }
        });
        INSTANCE.register(ServerChunkEvents.Watch.class, ChunkWatchEvent.Watch.class, (ServerChunkEvents.Watch callback, ChunkWatchEvent.Watch evt) -> {
            callback.onChunkWatch(evt.getPlayer(), evt.getChunk(), evt.getLevel());
        });
        INSTANCE.register(ServerChunkEvents.Unwatch.class, ChunkWatchEvent.UnWatch.class, (ServerChunkEvents.Unwatch callback, ChunkWatchEvent.UnWatch evt) -> {
            callback.onChunkUnwatch(evt.getPlayer(), evt.getPos(), evt.getLevel());
        });
        INSTANCE.register(LivingEquipmentChangeCallback.class, LivingEquipmentChangeEvent.class, (LivingEquipmentChangeCallback callback, LivingEquipmentChangeEvent evt) -> {
            callback.onLivingEquipmentChange(evt.getEntity(), evt.getSlot(), evt.getFrom(), evt.getTo());
        });
        INSTANCE.register(LivingConversionCallback.class, LivingConversionEvent.Post.class, (LivingConversionCallback callback, LivingConversionEvent.Post evt) -> {
            callback.onLivingConversion(evt.getEntity(), evt.getOutcome());
        });
        INSTANCE.register(ContainerEvents.Open.class, PlayerContainerEvent.Open.class, (ContainerEvents.Open callback, PlayerContainerEvent.Open evt) -> {
            callback.onContainerOpen((ServerPlayer) evt.getEntity(), evt.getContainer());
        });
        INSTANCE.register(ContainerEvents.Close.class, PlayerContainerEvent.Close.class, (ContainerEvents.Close callback, PlayerContainerEvent.Close evt) -> {
            callback.onContainerClose((ServerPlayer) evt.getEntity(), evt.getContainer());
        });
        INSTANCE.register(LookingAtEndermanCallback.class, EnderManAngerEvent.class, (LookingAtEndermanCallback callback, EnderManAngerEvent evt) -> {
            if (callback.onLookingAtEnderManCallback(evt.getEntity(), evt.getPlayer()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(RegisterPotionBrewingMixesCallback.class, RegisterBrewingRecipesEvent.class, (RegisterPotionBrewingMixesCallback callback, RegisterBrewingRecipesEvent evt) -> {
            callback.onRegisterPotionBrewingMixes(new NeoForgePotionBrewingBuilder(evt.getBuilder()));
        });
        FuelValues[] fuelValues = new FuelValues[1];
        INSTANCE.register(RegisterFuelValuesCallback.class, AddReloadListenerEvent.class, (RegisterFuelValuesCallback callback, AddReloadListenerEvent evt) -> {
            FuelValues.Builder builder = new FuelValues.Builder(evt.getRegistryAccess(),
                    FeatureFlags.REGISTRY.allFlags()
            );
            callback.onRegisterFuelValues(builder, 200);
            fuelValues[0] = builder.build();
        }, true);
        INSTANCE.register(RegisterFuelValuesCallback.class, FurnaceFuelBurnTimeEvent.class, (RegisterFuelValuesCallback callback, FurnaceFuelBurnTimeEvent evt) -> {
            Objects.requireNonNull(fuelValues[0], "fuel values is null");
            if (fuelValues[0].isFuel(evt.getItemStack())) {
                int burnTime = evt.getItemStack().getBurnTime(evt.getRecipeType(), fuelValues[0]);
                evt.setBurnTime(burnTime);
            }
        }, true);
        INSTANCE.register(AddDataPackReloadListenersCallback.class, AddReloadListenerEvent.class, (AddDataPackReloadListenersCallback callback, AddReloadListenerEvent evt) -> {
            callback.onAddDataPackReloadListeners((ResourceLocation resourceLocation, Function<HolderLookup.Provider, PreparableReloadListener> factory) -> {
                evt.addListener(ForwardingReloadListenerHelper.fromReloadListener(resourceLocation, factory.apply(
                        evt.getServerResources().getRegistryLookup())));
            });
        });
        INSTANCE.register(
                ChangeEntitySizeCallback.class, EntityEvent.Size.class, (ChangeEntitySizeCallback callback, EntityEvent.Size evt) -> {
            EventResultHolder<EntityDimensions> result = callback.onChangeEntitySize(
                    evt.getEntity(), evt.getPose(), evt.getOldSize());
            result.ifInterrupt(evt::setNewSize);
        });
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            NeoForgeClientEventInvokers.registerEventHandlers();
        }
    }

    @Override
    public <T, E extends Event> void register(Class<T> clazz, Class<E> event, NeoForgeEventContextConsumer<T, E> converter, boolean joinInvokers) {
        Objects.requireNonNull(clazz, "type is null");
        Objects.requireNonNull(event, "event type is null");
        Objects.requireNonNull(converter, "converter is null");
        Preconditions.checkArgument(!Modifier.isAbstract(event.getModifiers()), event + " is abstract");
        IEventBus eventBus;
        if (IModBusEvent.class.isAssignableFrom(event)) {
            // Most events are registered during the load complete phase, where most mod bus events have already run,
            // so they will be missed silently. Check this lock to avoid that.
            Preconditions.checkState(!frozenModBusEvents, "Mod bus events already frozen");
            // this will be null when an event is registered after the initial mod loading
            eventBus = NeoForgeModContainerHelper.getOptionalActiveModEventBus().orElse(null);
        } else {
            eventBus = NeoForge.EVENT_BUS;
        }
        EventInvokerImpl.register(clazz, new ForgeEventInvoker<>(eventBus, event, converter), joinInvokers);
    }

    private record ForgeEventInvoker<T, E extends Event>(@Nullable IEventBus eventBus, Class<E> event, NeoForgeEventContextConsumer<T, E> converter) implements EventInvoker<T>, EventInvokerImpl.EventInvokerLike<T> {
        private static final Map<EventPhase, EventPriority> PHASE_TO_PRIORITY = Map.of(EventPhase.FIRST, EventPriority.HIGHEST, EventPhase.BEFORE, EventPriority.HIGH, EventPhase.DEFAULT, EventPriority.NORMAL, EventPhase.AFTER, EventPriority.LOW, EventPhase.LAST, EventPriority.LOWEST);

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
            IEventBus eventBus = this.getEventBus(context);
            EventPriority eventPriority = PHASE_TO_PRIORITY.getOrDefault(phase, EventPriority.NORMAL);
            // filter out mod id which has been used to retrieve a missing mod event bus
            Object eventContext = this.eventBus != eventBus ? null : context;
            // we don't support receiving cancelled events since the event api on Fabric is not designed for it
            eventBus.addListener(eventPriority, false, this.event, (E evt) -> this.converter.accept(callback, evt, eventContext));
        }

        private IEventBus getEventBus(@Nullable Object context) {
            if (this.eventBus == null) {
                Objects.requireNonNull(context, "mod id context is null");
                return NeoForgeModContainerHelper.getModEventBus((String) context);
            } else {
                return this.eventBus;
            }
        }
    }
}
