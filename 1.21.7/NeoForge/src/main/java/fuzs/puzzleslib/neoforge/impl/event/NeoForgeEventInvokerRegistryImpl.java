package fuzs.puzzleslib.neoforge.impl.event;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.api.event.v1.*;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
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
import fuzs.puzzleslib.impl.event.data.DefaultedDouble;
import fuzs.puzzleslib.impl.event.data.DefaultedFloat;
import fuzs.puzzleslib.impl.event.data.DefaultedInt;
import fuzs.puzzleslib.impl.event.data.DefaultedValue;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.api.event.v1.core.NeoForgeEventInvokerRegistry;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgePotionBrewingBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.TriState;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.GameMasterBlock;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.*;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.*;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.registries.ModifyRegistriesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.registries.callback.BakeCallback;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.*;

public final class NeoForgeEventInvokerRegistryImpl implements NeoForgeEventInvokerRegistry {
    private static boolean frozenModBusEvents;

    public static void registerLoadingHandlers() {
        INSTANCE.register(CommonSetupCallback.class,
                FMLCommonSetupEvent.class,
                (CommonSetupCallback callback, FMLCommonSetupEvent event) -> {
                    event.enqueueWork(callback::onCommonSetup);
                });
        INSTANCE.register(LoadCompleteCallback.class,
                FMLLoadCompleteEvent.class,
                (LoadCompleteCallback callback, FMLLoadCompleteEvent event) -> {
                    event.enqueueWork(callback::onLoadComplete);
                });
        INSTANCE.register(RegistryEntryAddedCallback.class,
                ModifyRegistriesEvent.class,
                NeoForgeEventInvokerRegistryImpl::onRegistryEntryAdded);
        INSTANCE.register(FinalizeItemComponentsCallback.class,
                ModifyDefaultComponentsEvent.class,
                (FinalizeItemComponentsCallback callback, ModifyDefaultComponentsEvent event) -> {
                    event.getAllItems().forEach((Item item) -> {
                        callback.onFinalizeItemComponents(item,
                                (Function<DataComponentMap, DataComponentPatch> function) -> {
                                    event.modify(item, (DataComponentPatch.Builder builder) -> {
                                        DataComponentPatch.SplitResult splitResult = function.apply(item.components())
                                                .split();
                                        splitResult.added().stream().forEach(builder::set);
                                        splitResult.removed().forEach(builder::remove);
                                    });
                                });
                    });
                });
        INSTANCE.register(ComputeItemAttributeModifiersCallback.class,
                ModifyDefaultComponentsEvent.class,
                (ComputeItemAttributeModifiersCallback callback, ModifyDefaultComponentsEvent event) -> {
                    event.getAllItems().forEach((Item item) -> {
                        ItemAttributeModifiers itemAttributeModifiers = item.components()
                                .getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
                        CopyOnWriteForwardingList<ItemAttributeModifiers.Entry> entries = new CopyOnWriteForwardingList<>(
                                itemAttributeModifiers.modifiers());
                        callback.onComputeItemAttributeModifiers(item, entries);
                        if (entries.delegate() != itemAttributeModifiers.modifiers()) {
                            event.modify(item, (DataComponentPatch.Builder builder) -> {
                                builder.set(DataComponents.ATTRIBUTE_MODIFIERS,
                                        new ItemAttributeModifiers(ImmutableList.copyOf(entries)));
                            });
                        }
                    });
                });
        INSTANCE.register(AddBlockEntityTypeBlocksCallback.class,
                BlockEntityTypeAddBlocksEvent.class,
                (AddBlockEntityTypeBlocksCallback callback, BlockEntityTypeAddBlocksEvent event) -> {
                    callback.onAddBlockEntityTypeBlocks(event::modify);
                });
        INSTANCE.register(BuildCreativeModeTabContentsCallback.class,
                BuildCreativeModeTabContentsEvent.class,
                (BuildCreativeModeTabContentsCallback callback, BuildCreativeModeTabContentsEvent event, @Nullable Object context) -> {
                    Objects.requireNonNull(context, "context is null");
                    ResourceKey<CreativeModeTab> resourceKey = (ResourceKey<CreativeModeTab>) context;
                    if (resourceKey == event.getTabKey()) {
                        callback.onBuildCreativeModeTabContents(event.getTab(), event.getParameters(), event);
                    }
                });
        INSTANCE.register(RegisterConfigurationTasksCallback.class,
                RegisterConfigurationTasksEvent.class,
                (RegisterConfigurationTasksCallback callback, RegisterConfigurationTasksEvent event) -> {
                    callback.onRegisterConfigurationTasks((MinecraftServer) event.getListener()
                                    .getMainThreadEventLoop(),
                            (ServerConfigurationPacketListenerImpl) event.getListener(),
                            event::register);
                });
    }

    @SuppressWarnings("unchecked")
    private static <T> void onRegistryEntryAdded(RegistryEntryAddedCallback<T> callback, ModifyRegistriesEvent event, @Nullable Object context) {
        Objects.requireNonNull(context, "context is null");
        ResourceKey<? extends Registry<T>> resourceKey = (ResourceKey<? extends Registry<T>>) context;
        Registry<T> registry = event.getRegistry(resourceKey);
        boolean[] loadComplete = new boolean[1];
        registry.addCallback((AddCallback<T>) (Registry<T> callbackRegistry, int id, ResourceKey<T> key, T value) -> {
            if (!loadComplete[0]) {
                try {
                    callback.onRegistryEntryAdded(callbackRegistry,
                            key.location(),
                            value,
                            onRegistryEntryAdded(registry));
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
        eventBus.addListener((final RegisterEvent eventX) -> {
            if (eventX.getRegistryKey() != resourceKey) return;
            Consumer<BiConsumer<ResourceLocation, Supplier<T>>> consumer;
            while ((consumer = callbacks.poll()) != null) {
                consumer.accept(onRegistryEntryAdded((Registry<T>) eventX.getRegistry()));
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
        INSTANCE.register(PlayerInteractEvents.UseBlock.class,
                PlayerInteractEvent.RightClickBlock.class,
                (PlayerInteractEvents.UseBlock callback, PlayerInteractEvent.RightClickBlock event) -> {
                    callback.onUseBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec())
                            .ifInterrupt(interactionResult -> {
                                event.setCancellationResult(interactionResult);
                                event.setCanceled(true);
                            });
                });
        INSTANCE.register(PlayerInteractEvents.AttackBlock.class,
                PlayerInteractEvent.LeftClickBlock.class,
                (PlayerInteractEvents.AttackBlock callback, PlayerInteractEvent.LeftClickBlock event) -> {
                    if (callback.onAttackBlock(event.getEntity(),
                            event.getLevel(),
                            event.getHand(),
                            event.getPos(),
                            event.getFace()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(PlayerInteractEvents.UseItem.class,
                PlayerInteractEvent.RightClickItem.class,
                (PlayerInteractEvents.UseItem callback, PlayerInteractEvent.RightClickItem event) -> {
                    callback.onUseItem(event.getEntity(), event.getLevel(), event.getHand())
                            .ifInterrupt(interactionResult -> {
                                event.setCancellationResult(interactionResult);
                                event.setCanceled(true);
                            });
                });
        INSTANCE.register(PlayerInteractEvents.UseEntity.class,
                PlayerInteractEvent.EntityInteract.class,
                (PlayerInteractEvents.UseEntity callback, PlayerInteractEvent.EntityInteract event) -> {
                    callback.onUseEntity(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget())
                            .ifInterrupt(interactionResult -> {
                                event.setCancellationResult(interactionResult);
                                event.setCanceled(true);
                            });
                });
        INSTANCE.register(PlayerInteractEvents.UseEntityAt.class,
                PlayerInteractEvent.EntityInteractSpecific.class,
                (PlayerInteractEvents.UseEntityAt callback, PlayerInteractEvent.EntityInteractSpecific event) -> {
                    callback.onUseEntityAt(event.getEntity(),
                            event.getLevel(),
                            event.getHand(),
                            event.getTarget(),
                            event.getLocalPos()).ifInterrupt(interactionResult -> {
                        event.setCancellationResult(interactionResult);
                        event.setCanceled(true);
                    });
                });
        INSTANCE.register(PlayerInteractEvents.AttackEntity.class,
                AttackEntityEvent.class,
                (PlayerInteractEvents.AttackEntity callback, AttackEntityEvent event) -> {
                    if (callback.onAttackEntity(event.getEntity(),
                            event.getEntity().level(),
                            InteractionHand.MAIN_HAND,
                            event.getTarget()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(PickupExperienceCallback.class,
                PlayerXpEvent.PickupXp.class,
                (PickupExperienceCallback callback, PlayerXpEvent.PickupXp event) -> {
                    if (callback.onPickupExperience(event.getEntity(), event.getOrb()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(UseBoneMealCallback.class,
                BonemealEvent.class,
                (UseBoneMealCallback callback, BonemealEvent event) -> {
                    EventResult eventResult = callback.onUseBoneMeal(event.getLevel(),
                            event.getPos(),
                            event.getState(),
                            event.getStack());
                    if (eventResult.isInterrupt()) {
                        event.setSuccessful(eventResult.getAsBoolean());
                    }
                });
        INSTANCE.register(LivingExperienceDropCallback.class,
                LivingExperienceDropEvent.class,
                (LivingExperienceDropCallback callback, LivingExperienceDropEvent event) -> {
                    DefaultedInt droppedExperience = DefaultedInt.fromEvent(event::setDroppedExperience,
                            event::getDroppedExperience,
                            event::getOriginalExperience);
                    if (callback.onLivingExperienceDrop(event.getEntity(),
                            event.getAttackingPlayer(),
                            droppedExperience).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(BlockEvents.Break.class,
                BlockEvent.BreakEvent.class,
                (BlockEvents.Break callback, BlockEvent.BreakEvent event) -> {
                    if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
                    if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) return;
                    // match Fabric implementation
                    if (event.getState().getBlock() instanceof GameMasterBlock
                            && !serverPlayer.canUseGameMasterBlocks()) {
                        return;
                    }
                    GameType gameType = serverPlayer.gameMode.getGameModeForPlayer();
                    if (serverPlayer.blockActionRestricted((Level) event.getLevel(), event.getPos(), gameType)) {
                        return;
                    }
                    EventResult eventResult = callback.onBreakBlock(serverLevel,
                            event.getPos(),
                            event.getState(),
                            serverPlayer,
                            serverPlayer.getMainHandItem());
                    if (eventResult.isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(BlockEvents.DropExperience.class,
                BlockDropsEvent.class,
                (BlockEvents.DropExperience callback, BlockDropsEvent event) -> {
                    if (!(event.getBreaker() instanceof ServerPlayer serverPlayer)) return;
                    MutableInt experienceAmount = MutableInt.fromEvent(event::setDroppedExperience,
                            event::getDroppedExperience);
                    callback.onDropExperience(event.getLevel(),
                            event.getPos(),
                            event.getState(),
                            serverPlayer,
                            event.getTool(),
                            experienceAmount);
                });
        INSTANCE.register(PlayerTickEvents.Start.class,
                PlayerTickEvent.Pre.class,
                (PlayerTickEvents.Start callback, PlayerTickEvent.Pre event) -> {
                    callback.onStartPlayerTick(event.getEntity());
                });
        INSTANCE.register(PlayerTickEvents.End.class,
                PlayerTickEvent.Post.class,
                (PlayerTickEvents.End callback, PlayerTickEvent.Post event) -> {
                    callback.onEndPlayerTick(event.getEntity());
                });
        INSTANCE.register(LivingFallCallback.class,
                LivingFallEvent.class,
                (LivingFallCallback callback, LivingFallEvent event) -> {
                    MutableDouble fallDistance = MutableDouble.fromEvent(event::setDistance, event::getDistance);
                    MutableFloat damageMultiplier = MutableFloat.fromEvent(event::setDamageMultiplier,
                            event::getDamageMultiplier);
                    if (callback.onLivingFall(event.getEntity(), fallDistance, damageMultiplier).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(RegisterCommandsCallback.class,
                RegisterCommandsEvent.class,
                (RegisterCommandsCallback callback, RegisterCommandsEvent event) -> {
                    callback.onRegisterCommands(event.getDispatcher(),
                            event.getBuildContext(),
                            event.getCommandSelection());
                });
        INSTANCE.register(LootTableLoadCallback.class,
                LootTableLoadEvent.class,
                (LootTableLoadCallback callback, LootTableLoadEvent event) -> {
                    callback.onLootTableLoad(event.getName(),
                            new ForwardingLootTableBuilder(event.getTable()),
                            event.getRegistries());
                });
        INSTANCE.register(ItemEntityEvents.Touch.class,
                ItemEntityPickupEvent.Pre.class,
                (ItemEntityEvents.Touch callback, ItemEntityPickupEvent.Pre event) -> {
                    EventResult eventResult = callback.onItemTouch(event.getPlayer(), event.getItemEntity());
                    if (eventResult.isInterrupt()) {
                        event.setCanPickup(eventResult.getAsBoolean() ? TriState.TRUE : TriState.FALSE);
                    }
                });
        INSTANCE.register(ItemEntityEvents.Pickup.class,
                ItemEntityPickupEvent.Post.class,
                (ItemEntityEvents.Pickup callback, ItemEntityPickupEvent.Post event) -> {
                    callback.onItemPickup(event.getPlayer(), event.getItemEntity(), event.getOriginalStack());
                });
        INSTANCE.register(CreateAnvilResultCallback.class,
                AnvilUpdateEvent.class,
                (CreateAnvilResultCallback callback, AnvilUpdateEvent event) -> {
                    DefaultedValue<ItemStack> outputItemStack = DefaultedValue.fromEvent(event::setOutput,
                            event::getOutput,
                            event.getVanillaResult()::output);
                    DefaultedInt enchantmentLevelCost = DefaultedInt.fromEvent(event::setXpCost,
                            event::getXpCost,
                            event.getVanillaResult()::xpCost);
                    DefaultedInt repairMaterialCost = DefaultedInt.fromEvent(event::setMaterialCost,
                            event::getMaterialCost,
                            event.getVanillaResult()::materialCost);
                    if (callback.onCreateAnvilResult(event.getPlayer(),
                            event.getLeft(),
                            event.getRight(),
                            outputItemStack,
                            event.getName(),
                            enchantmentLevelCost,
                            repairMaterialCost).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(CreateGrindstoneResultCallback.class,
                GrindstoneEvent.OnPlaceItem.class,
                (CreateGrindstoneResultCallback callback, GrindstoneEvent.OnPlaceItem event) -> {
                    Map.Entry<GrindstoneMenu, Player> entry = EventImplHelper.getGrindstoneMenuFromInputs(event.getTopItem(),
                            event.getBottomItem());
                    if (entry != null) {
                        Supplier<ItemStack> outputItemStackSupplier = Suppliers.memoize(() -> entry.getKey()
                                .computeResult(event.getTopItem(), event.getBottomItem()));
                        MutableValue<ItemStack> outputItemStack = MutableValue.fromEvent(event::setOutput, () -> {
                            return !event.getOutput().isEmpty() ? event.getOutput() : outputItemStackSupplier.get();
                        });
                        MutableInt experiencePointReward = MutableInt.fromEvent(event::setXp, event::getXp);
                        EventResult eventResult = callback.onCreateGrindstoneResult(entry.getValue(),
                                event.getTopItem(),
                                event.getBottomItem(),
                                outputItemStack,
                                experiencePointReward);
                        if (eventResult.isInterrupt()) {
                            event.setCanceled(true);
                        }
                    }
                });
        INSTANCE.register(LivingDropsCallback.class,
                LivingDropsEvent.class,
                (LivingDropsCallback callback, LivingDropsEvent event) -> {
                    if (callback.onLivingDrops(event.getEntity(),
                            event.getSource(),
                            event.getDrops(),
                            event.isRecentlyHit()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(EntityTickEvents.Start.class,
                EntityTickEvent.Pre.class,
                (EntityTickEvents.Start callback, EntityTickEvent.Pre event) -> {
                    if (callback.onStartEntityTick(event.getEntity()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(EntityTickEvents.End.class,
                EntityTickEvent.Post.class,
                (EntityTickEvents.End callback, EntityTickEvent.Post event) -> {
                    callback.onEndEntityTick(event.getEntity());
                });
        INSTANCE.register(ArrowLooseCallback.class,
                ArrowLooseEvent.class,
                (ArrowLooseCallback callback, ArrowLooseEvent event) -> {
                    MutableInt charge = MutableInt.fromEvent(event::setCharge, event::getCharge);
                    if (callback.onArrowLoose(event.getEntity(),
                            event.getBow(),
                            event.getLevel(),
                            charge,
                            event.hasAmmo()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(LivingHurtCallback.class,
                LivingDamageEvent.Pre.class,
                (LivingHurtCallback callback, LivingDamageEvent.Pre event) -> {
                    MutableFloat damageAmount = MutableFloat.fromEvent(event.getContainer()::setNewDamage,
                            event.getContainer()::getNewDamage);
                    if (callback.onLivingHurt(event.getEntity(), event.getContainer().getSource(), damageAmount)
                            .isInterrupt()) {
                        // this effectively cancels the event
                        event.getContainer().setNewDamage(0.0F);
                    }
                });
        INSTANCE.register(UseItemEvents.Start.class,
                LivingEntityUseItemEvent.Start.class,
                (UseItemEvents.Start callback, LivingEntityUseItemEvent.Start event) -> {
                    MutableInt useDuration = MutableInt.fromEvent(event::setDuration, event::getDuration);
                    if (callback.onUseItemStart(event.getEntity(), event.getItem(), useDuration).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(UseItemEvents.Tick.class,
                LivingEntityUseItemEvent.Tick.class,
                (UseItemEvents.Tick callback, LivingEntityUseItemEvent.Tick event) -> {
                    MutableInt useItemRemaining = MutableInt.fromEvent(event::setDuration, event::getDuration);
                    if (callback.onUseItemTick(event.getEntity(), event.getItem(), useItemRemaining).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(UseItemEvents.Stop.class,
                LivingEntityUseItemEvent.Stop.class,
                (UseItemEvents.Stop callback, LivingEntityUseItemEvent.Stop event) -> {
                    // Forge event also supports changing duration, but it remains unused
                    if (callback.onUseItemStop(event.getEntity(), event.getItem(), event.getDuration()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(UseItemEvents.Finish.class,
                LivingEntityUseItemEvent.Finish.class,
                (UseItemEvents.Finish callback, LivingEntityUseItemEvent.Finish event) -> {
                    MutableValue<ItemStack> itemStack = MutableValue.fromEvent(event::setResultStack,
                            event::getResultStack);
                    callback.onUseItemFinish(event.getEntity(), itemStack, event.getItem());
                });
        INSTANCE.register(ShieldBlockCallback.class,
                LivingShieldBlockEvent.class,
                (ShieldBlockCallback callback, LivingShieldBlockEvent event) -> {
                    DefaultedFloat blockedDamage = DefaultedFloat.fromEvent(event::setBlockedDamage,
                            event::getBlockedDamage,
                            event::getOriginalBlockedDamage);
                    if (event.getBlocked() && callback.onShieldBlock(event.getEntity(),
                            event.getDamageSource(),
                            blockedDamage).isInterrupt()) {
                        event.setBlocked(true);
                    }
                });
        INSTANCE.register(TagsUpdatedCallback.class,
                TagsUpdatedEvent.class,
                (TagsUpdatedCallback callback, TagsUpdatedEvent event) -> {
                    callback.onTagsUpdated(event.getLookupProvider(),
                            event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED);
                });
        INSTANCE.register(ExplosionEvents.Start.class,
                ExplosionEvent.Start.class,
                (ExplosionEvents.Start callback, ExplosionEvent.Start event) -> {
                    if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
                    if (callback.onExplosionStart(serverLevel, event.getExplosion()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(ExplosionEvents.Detonate.class,
                ExplosionEvent.Detonate.class,
                (ExplosionEvents.Detonate callback, ExplosionEvent.Detonate event) -> {
                    if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
                    callback.onExplosionDetonate(serverLevel,
                            event.getExplosion(),
                            event.getAffectedBlocks(),
                            event.getAffectedEntities());
                });
        INSTANCE.register(SyncDataPackContentsCallback.class,
                OnDatapackSyncEvent.class,
                (SyncDataPackContentsCallback callback, OnDatapackSyncEvent event) -> {
                    event.getRelevantPlayers().forEach((ServerPlayer player) -> {
                        callback.onSyncDataPackContents(player, event.getPlayer() != null);
                    });
                });
        INSTANCE.register(ServerLifecycleEvents.Starting.class,
                ServerAboutToStartEvent.class,
                (ServerLifecycleEvents.Starting callback, ServerAboutToStartEvent event) -> {
                    callback.onServerStarting(event.getServer());
                });
        INSTANCE.register(ServerLifecycleEvents.Started.class,
                ServerStartedEvent.class,
                (ServerLifecycleEvents.Started callback, ServerStartedEvent event) -> {
                    callback.onServerStarted(event.getServer());
                });
        INSTANCE.register(ServerLifecycleEvents.Stopping.class,
                ServerStoppingEvent.class,
                (ServerLifecycleEvents.Stopping callback, ServerStoppingEvent event) -> {
                    callback.onServerStopping(event.getServer());
                });
        INSTANCE.register(ServerLifecycleEvents.Stopped.class,
                ServerStoppedEvent.class,
                (ServerLifecycleEvents.Stopped callback, ServerStoppedEvent event) -> {
                    callback.onServerStopped(event.getServer());
                });
        INSTANCE.register(PlaySoundEvents.AtPosition.class,
                PlayLevelSoundEvent.AtPosition.class,
                (PlaySoundEvents.AtPosition callback, PlayLevelSoundEvent.AtPosition event) -> {
                    MutableValue<Holder<SoundEvent>> soundEvent = MutableValue.fromEvent(event::setSound,
                            event::getSound);
                    MutableValue<SoundSource> soundSource = MutableValue.fromEvent(event::setSource, event::getSource);
                    MutableFloat soundVolume = MutableFloat.fromEvent(event::setNewVolume, event::getNewVolume);
                    MutableFloat soundPitch = MutableFloat.fromEvent(event::setNewPitch, event::getNewPitch);
                    if (callback.onPlaySoundAtPosition(event.getLevel(),
                            event.getPosition(),
                            soundEvent,
                            soundSource,
                            soundVolume,
                            soundPitch).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(PlaySoundEvents.AtEntity.class,
                PlayLevelSoundEvent.AtEntity.class,
                (PlaySoundEvents.AtEntity callback, PlayLevelSoundEvent.AtEntity event) -> {
                    MutableValue<Holder<SoundEvent>> soundEvent = MutableValue.fromEvent(event::setSound,
                            event::getSound);
                    MutableValue<SoundSource> soundSource = MutableValue.fromEvent(event::setSource, event::getSource);
                    MutableFloat soundVolume = MutableFloat.fromEvent(event::setNewVolume, event::getNewVolume);
                    MutableFloat soundPitch = MutableFloat.fromEvent(event::setNewPitch, event::getNewPitch);
                    if (callback.onPlaySoundAtEntity(event.getLevel(),
                            event.getEntity(),
                            soundEvent,
                            soundSource,
                            soundVolume,
                            soundPitch).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(ServerEntityLevelEvents.Load.class,
                EntityJoinLevelEvent.class,
                (ServerEntityLevelEvents.Load callback, EntityJoinLevelEvent event) -> {
                    if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
                    if (callback.onEntityLoad(event.getEntity(), serverLevel, !event.loadedFromDisk()).isInterrupt()) {
                        if (event.getEntity() instanceof Player) {
                            // we do not support players as it isn't as straight-forward to implement for the server player on Fabric
                            throw new UnsupportedOperationException("Cannot prevent player from loading in!");
                        } else {
                            event.setCanceled(true);
                        }
                    }
                });
        INSTANCE.register(ServerEntityLevelEvents.Unload.class,
                EntityLeaveLevelEvent.class,
                (ServerEntityLevelEvents.Unload callback, EntityLeaveLevelEvent event) -> {
                    if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
                    callback.onEntityUnload(event.getEntity(), serverLevel);
                });
        INSTANCE.register(LivingDeathCallback.class,
                LivingDeathEvent.class,
                (LivingDeathCallback callback, LivingDeathEvent event) -> {
                    if (callback.onLivingDeath(event.getEntity(), event.getSource()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(PlayerTrackingEvents.Start.class,
                PlayerEvent.StartTracking.class,
                (PlayerTrackingEvents.Start callback, PlayerEvent.StartTracking event) -> {
                    if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
                    callback.onStartTracking(event.getTarget(), serverPlayer);
                });
        INSTANCE.register(PlayerTrackingEvents.Stop.class,
                PlayerEvent.StopTracking.class,
                (PlayerTrackingEvents.Stop callback, PlayerEvent.StopTracking event) -> {
                    if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
                    callback.onStopTracking(event.getTarget(), serverPlayer);
                });
        INSTANCE.register(PlayerNetworkEvents.LoggedIn.class,
                PlayerEvent.PlayerLoggedInEvent.class,
                (PlayerNetworkEvents.LoggedIn callback, PlayerEvent.PlayerLoggedInEvent event) -> {
                    if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
                    callback.onLoggedIn(serverPlayer);
                });
        INSTANCE.register(PlayerNetworkEvents.LoggedOut.class,
                PlayerEvent.PlayerLoggedOutEvent.class,
                (PlayerNetworkEvents.LoggedOut callback, PlayerEvent.PlayerLoggedOutEvent event) -> {
                    if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
                    callback.onLoggedOut(serverPlayer);
                });
        INSTANCE.register(AfterChangeDimensionCallback.class,
                PlayerEvent.PlayerChangedDimensionEvent.class,
                (AfterChangeDimensionCallback callback, PlayerEvent.PlayerChangedDimensionEvent event) -> {
                    if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
                    ServerLevel originalLevel = serverPlayer.getServer().getLevel(event.getFrom());
                    ServerLevel newLevel = serverPlayer.getServer().getLevel(event.getTo());
                    Objects.requireNonNull(originalLevel, "original level is null");
                    Objects.requireNonNull(newLevel, "new level is null");
                    callback.onAfterChangeDimension(serverPlayer, originalLevel, newLevel);
                });
        INSTANCE.register(BabyEntitySpawnCallback.class,
                BabyEntitySpawnEvent.class,
                (BabyEntitySpawnCallback callback, BabyEntitySpawnEvent event) -> {
                    MutableValue<AgeableMob> child = MutableValue.fromEvent(event::setChild, event::getChild);
                    if (callback.onBabyEntitySpawn(event.getParentA(), event.getParentB(), child).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(AnimalTameCallback.class,
                AnimalTameEvent.class,
                (AnimalTameCallback callback, AnimalTameEvent event) -> {
                    if (callback.onAnimalTame(event.getAnimal(), event.getTamer()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(LivingAttackCallback.class,
                LivingIncomingDamageEvent.class,
                (LivingAttackCallback callback, LivingIncomingDamageEvent event) -> {
                    if (callback.onLivingAttack(event.getEntity(), event.getSource(), event.getAmount())
                            .isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(PlayerCopyEvents.Copy.class,
                PlayerEvent.Clone.class,
                (PlayerCopyEvents.Copy callback, PlayerEvent.Clone event) -> {
                    if (!(event.getOriginal() instanceof ServerPlayer originalServerPlayer)) return;
                    if (!(event.getEntity() instanceof ServerPlayer newServerPlayer)) return;
                    callback.onCopy(originalServerPlayer, newServerPlayer, !event.isWasDeath());
                });
        INSTANCE.register(PlayerCopyEvents.Respawn.class,
                PlayerEvent.PlayerRespawnEvent.class,
                (PlayerCopyEvents.Respawn callback, PlayerEvent.PlayerRespawnEvent event) -> {
                    if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
                    callback.onRespawn(serverPlayer, event.isEndConquered());
                });
        INSTANCE.register(ServerTickEvents.Start.class,
                ServerTickEvent.Pre.class,
                (ServerTickEvents.Start callback, ServerTickEvent.Pre event) -> {
                    callback.onStartServerTick(event.getServer());
                });
        INSTANCE.register(ServerTickEvents.End.class,
                ServerTickEvent.Post.class,
                (ServerTickEvents.End callback, ServerTickEvent.Post event) -> {
                    callback.onEndServerTick(event.getServer());
                });
        INSTANCE.register(ServerLevelTickEvents.Start.class,
                LevelTickEvent.Pre.class,
                (ServerLevelTickEvents.Start callback, LevelTickEvent.Pre event) -> {
                    if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
                    callback.onStartLevelTick(serverLevel.getServer(), serverLevel);
                });
        INSTANCE.register(ServerLevelTickEvents.End.class,
                LevelTickEvent.Post.class,
                (ServerLevelTickEvents.End callback, LevelTickEvent.Post event) -> {
                    if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
                    callback.onEndLevelTick(serverLevel.getServer(), serverLevel);
                });
        INSTANCE.register(ServerLevelEvents.Load.class,
                LevelEvent.Load.class,
                (ServerLevelEvents.Load callback, LevelEvent.Load event) -> {
                    if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
                    callback.onLevelLoad(serverLevel.getServer(), serverLevel);
                });
        INSTANCE.register(ServerLevelEvents.Unload.class,
                LevelEvent.Unload.class,
                (ServerLevelEvents.Unload callback, LevelEvent.Unload event) -> {
                    if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
                    callback.onLevelUnload(serverLevel.getServer(), serverLevel);
                });
        INSTANCE.register(ServerChunkEvents.Load.class,
                ChunkEvent.Load.class,
                (ServerChunkEvents.Load callback, ChunkEvent.Load event) -> {
                    if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
                    callback.onChunkLoad(serverLevel, event.getChunk());
                });
        INSTANCE.register(ServerChunkEvents.Unload.class,
                ChunkEvent.Unload.class,
                (ServerChunkEvents.Unload callback, ChunkEvent.Unload event) -> {
                    if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
                    callback.onChunkUnload(serverLevel, event.getChunk());
                });
        INSTANCE.register(ItemEntityEvents.Toss.class,
                ItemTossEvent.class,
                (ItemEntityEvents.Toss callback, ItemTossEvent event) -> {
                    if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) return;
                    if (callback.onItemToss(serverPlayer, event.getEntity().getItem()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(LivingKnockBackCallback.class,
                LivingKnockBackEvent.class,
                (LivingKnockBackCallback callback, LivingKnockBackEvent event) -> {
                    DefaultedDouble strength = DefaultedDouble.fromEvent(v -> event.setStrength((float) v),
                            event::getStrength,
                            event::getOriginalStrength);
                    DefaultedDouble ratioX = DefaultedDouble.fromEvent(event::setRatioX,
                            event::getRatioX,
                            event::getOriginalRatioX);
                    DefaultedDouble ratioZ = DefaultedDouble.fromEvent(event::setRatioZ,
                            event::getRatioZ,
                            event::getOriginalRatioZ);
                    if (callback.onLivingKnockBack(event.getEntity(), strength, ratioX, ratioZ).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(ProjectileImpactCallback.class,
                ProjectileImpactEvent.class,
                (ProjectileImpactCallback callback, ProjectileImpactEvent event) -> {
                    if (callback.onProjectileImpact(event.getProjectile(), event.getRayTraceResult()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(CalculateBlockBreakSpeedCallback.class,
                PlayerEvent.BreakSpeed.class,
                (CalculateBlockBreakSpeedCallback callback, PlayerEvent.BreakSpeed event) -> {
                    DefaultedFloat breakSpeed = DefaultedFloat.fromEvent(event::setNewSpeed,
                            event::getNewSpeed,
                            event::getOriginalSpeed);
                    if (callback.onCalculateBlockBreakSpeed(event.getEntity(), event.getState(), breakSpeed)
                            .isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(MobEffectEvents.Affects.class,
                MobEffectEvent.Applicable.class,
                (MobEffectEvents.Affects callback, MobEffectEvent.Applicable event) -> {
                    EventResult eventResult = callback.onMobEffectAffects(event.getEntity(), event.getEffectInstance());
                    if (eventResult.isInterrupt()) {
                        event.setResult(eventResult.getAsBoolean() ? MobEffectEvent.Applicable.Result.APPLY :
                                MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
                    }
                });
        INSTANCE.register(MobEffectEvents.Apply.class,
                MobEffectEvent.Added.class,
                (MobEffectEvents.Apply callback, MobEffectEvent.Added event) -> {
                    callback.onMobEffectApply(event.getEntity(),
                            event.getEffectInstance(),
                            event.getOldEffectInstance(),
                            event.getEffectSource());
                });
        INSTANCE.register(MobEffectEvents.Remove.class,
                MobEffectEvent.Remove.class,
                (MobEffectEvents.Remove callback, MobEffectEvent.Remove event) -> {
                    if (callback.onMobEffectRemove(event.getEntity(), event.getEffectInstance()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(MobEffectEvents.Expire.class,
                MobEffectEvent.Expired.class,
                (MobEffectEvents.Expire callback, MobEffectEvent.Expired event) -> {
                    callback.onMobEffectExpire(event.getEntity(), event.getEffectInstance());
                });
        INSTANCE.register(LivingJumpCallback.class,
                LivingEvent.LivingJumpEvent.class,
                (LivingJumpCallback callback, LivingEvent.LivingJumpEvent event) -> {
                    EventImplHelper.onLivingJump(callback, event.getEntity());
                });
        INSTANCE.register(LivingVisibilityCallback.class,
                LivingEvent.LivingVisibilityEvent.class,
                (LivingVisibilityCallback callback, LivingEvent.LivingVisibilityEvent event) -> {
                    callback.onLivingVisibility(event.getEntity(),
                            event.getLookingEntity(),
                            MutableDouble.fromEvent(visibilityModifier -> {
                                event.modifyVisibility(visibilityModifier / event.getVisibilityModifier());
                            }, event::getVisibilityModifier));
                });
        INSTANCE.register(LivingChangeTargetCallback.class,
                LivingChangeTargetEvent.class,
                (LivingChangeTargetCallback callback, LivingChangeTargetEvent event) -> {
                    DefaultedValue<LivingEntity> target = DefaultedValue.fromEvent(event::setNewAboutToBeSetTarget,
                            event::getNewAboutToBeSetTarget,
                            event::getOriginalAboutToBeSetTarget);
                    if (callback.onLivingChangeTarget(event.getEntity(), target).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(CheckMobDespawnCallback.class,
                MobDespawnEvent.class,
                (CheckMobDespawnCallback callback, MobDespawnEvent event) -> {
                    if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
                    EventResult eventResult = callback.onCheckMobDespawn(event.getEntity(), serverLevel);
                    if (eventResult.isInterrupt()) {
                        event.setResult(eventResult.getAsBoolean() ? MobDespawnEvent.Result.ALLOW :
                                MobDespawnEvent.Result.DENY);
                    }
                });
        INSTANCE.register(GatherPotentialSpawnsCallback.class,
                LevelEvent.PotentialSpawns.class,
                (GatherPotentialSpawnsCallback callback, LevelEvent.PotentialSpawns event) -> {
                    if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
                    List<Weighted<MobSpawnSettings.SpawnerData>> mobs = new PotentialSpawnsList<>(event::getSpawnerDataList,
                            (Weighted<MobSpawnSettings.SpawnerData> spawnerData) -> {
                                int size = event.getSpawnerDataList().size();
                                event.addSpawnerData(spawnerData);
                                return size != event.getSpawnerDataList().size();
                            },
                            (Weighted<MobSpawnSettings.SpawnerData> spawnerData) -> {
                                int size = event.getSpawnerDataList().size();
                                event.removeSpawnerData(spawnerData);
                                return size != event.getSpawnerDataList().size();
                            });
                    callback.onGatherPotentialSpawns(serverLevel,
                            serverLevel.structureManager(),
                            serverLevel.getChunkSource().getGenerator(),
                            event.getMobCategory(),
                            event.getPos(),
                            mobs);
                });
        INSTANCE.register(EntityRidingEvents.Start.class,
                EntityMountEvent.class,
                (EntityRidingEvents.Start callback, EntityMountEvent event) -> {
                    if (event.isDismounting()) return;
                    // same implementation as Fabric
                    if (!event.getEntityMounting().canRide(event.getEntityBeingMounted())) return;
                    if (!event.getEntityBeingMounted().canAddPassenger(event.getEntityMounting())) return;
                    if (callback.onStartRiding(event.getLevel(),
                            event.getEntityMounting(),
                            event.getEntityBeingMounted()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(EntityRidingEvents.Stop.class,
                EntityMountEvent.class,
                (EntityRidingEvents.Stop callback, EntityMountEvent event) -> {
                    if (event.isMounting()) return;
                    if (callback.onStopRiding(event.getLevel(), event.getEntity(), event.getEntityBeingMounted())
                            .isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(ServerChunkEvents.Watch.class,
                ChunkWatchEvent.Watch.class,
                (ServerChunkEvents.Watch callback, ChunkWatchEvent.Watch event) -> {
                    callback.onChunkWatch(event.getPlayer(), event.getChunk(), event.getLevel());
                });
        INSTANCE.register(ServerChunkEvents.Unwatch.class,
                ChunkWatchEvent.UnWatch.class,
                (ServerChunkEvents.Unwatch callback, ChunkWatchEvent.UnWatch event) -> {
                    callback.onChunkUnwatch(event.getPlayer(), event.getPos(), event.getLevel());
                });
        INSTANCE.register(LivingEquipmentChangeCallback.class,
                LivingEquipmentChangeEvent.class,
                (LivingEquipmentChangeCallback callback, LivingEquipmentChangeEvent event) -> {
                    callback.onLivingEquipmentChange(event.getEntity(),
                            event.getSlot(),
                            event.getFrom(),
                            event.getTo());
                });
        INSTANCE.register(LivingConversionCallback.class,
                LivingConversionEvent.Post.class,
                (LivingConversionCallback callback, LivingConversionEvent.Post event) -> {
                    callback.onLivingConversion(event.getEntity(), event.getOutcome());
                });
        INSTANCE.register(ContainerEvents.Open.class,
                PlayerContainerEvent.Open.class,
                (ContainerEvents.Open callback, PlayerContainerEvent.Open event) -> {
                    if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
                    callback.onContainerOpen(serverPlayer, event.getContainer());
                });
        INSTANCE.register(ContainerEvents.Close.class,
                PlayerContainerEvent.Close.class,
                (ContainerEvents.Close callback, PlayerContainerEvent.Close event) -> {
                    if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
                    callback.onContainerClose(serverPlayer, event.getContainer());
                });
        INSTANCE.register(LookingAtEndermanCallback.class,
                EnderManAngerEvent.class,
                (LookingAtEndermanCallback callback, EnderManAngerEvent event) -> {
                    if (callback.onLookingAtEnderManCallback(event.getEntity(), event.getPlayer()).isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(RegisterPotionBrewingMixesCallback.class,
                RegisterBrewingRecipesEvent.class,
                (RegisterPotionBrewingMixesCallback callback, RegisterBrewingRecipesEvent event) -> {
                    callback.onRegisterPotionBrewingMixes(new NeoForgePotionBrewingBuilder(event.getBuilder()));
                });
        INSTANCE.register(AddDataPackReloadListenersCallback.class,
                AddServerReloadListenersEvent.class,
                (AddDataPackReloadListenersCallback callback, AddServerReloadListenersEvent event) -> {
                    callback.onAddDataPackReloadListeners(event.getRegistryAccess(),
                            event.getServerResources().getRegistryLookup(),
                            (ResourceLocation resourceLocation, PreparableReloadListener reloadListener) -> {
                                event.addListener(resourceLocation,
                                        ForwardingReloadListenerHelper.fromReloadListener(resourceLocation,
                                                reloadListener));
                            });
                });
        INSTANCE.register(ChangeEntitySizeCallback.class,
                EntityEvent.Size.class,
                (ChangeEntitySizeCallback callback, EntityEvent.Size event) -> {
                    EventResultHolder<EntityDimensions> eventResult = callback.onChangeEntitySize(event.getEntity(),
                            event.getPose(),
                            event.getOldSize());
                    eventResult.ifInterrupt(event::setNewSize);
                });
        INSTANCE.register(PickProjectileCallback.class,
                LivingGetProjectileEvent.class,
                (PickProjectileCallback callback, LivingGetProjectileEvent event) -> {
                    MutableValue<ItemStack> ammoItemStack = MutableValue.fromEvent(event::setProjectileItemStack,
                            event::getProjectileItemStack);
                    callback.onPickProjectile(event.getEntity(), event.getProjectileWeaponItemStack(), ammoItemStack);
                });
        INSTANCE.register(EnderPearlTeleportCallback.class,
                EntityTeleportEvent.EnderPearl.class,
                (EnderPearlTeleportCallback callback, EntityTeleportEvent.EnderPearl event) -> {
                    EventResult eventResult = callback.onEnderPearlTeleport(event.getPlayer(),
                            event.getTarget(),
                            event.getPearlEntity(),
                            MutableFloat.fromEvent(event::setAttackDamage, event::getAttackDamage),
                            event.getHitResult());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
    }

    @Override
    public <T, E extends Event> void register(Class<T> clazz, Class<E> event, NeoForgeEventContextConsumer<T, E> converter, UnaryOperator<EventPhase> eventPhaseConverter, boolean joinInvokers) {
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
        EventInvokerImpl.register(clazz,
                new NeoForgeEventInvoker<>(eventBus, event, converter, eventPhaseConverter),
                joinInvokers);
    }

    public static EventPriority getEventPriorityFromPhase(EventPhase eventPhase) {
        return NeoForgeEventInvoker.getEventPriority(eventPhase);
    }

    private record NeoForgeEventInvoker<T, E extends Event>(@Nullable IEventBus eventBus,
                                                            Class<E> event,
                                                            NeoForgeEventContextConsumer<T, E> converter,
                                                            UnaryOperator<EventPhase> eventPhaseConverter) implements EventInvoker<T>, EventInvokerImpl.EventInvokerLike<T> {
        private static final Map<EventPhase, EventPriority> PHASE_TO_PRIORITY = Map.of(EventPhase.FIRST,
                EventPriority.HIGHEST,
                EventPhase.BEFORE,
                EventPriority.HIGH,
                EventPhase.DEFAULT,
                EventPriority.NORMAL,
                EventPhase.AFTER,
                EventPriority.LOW,
                EventPhase.LAST,
                EventPriority.LOWEST);
        private static final IntFunction<EventPriority> PRIORITY_IDS = ByIdMap.continuous(Enum::ordinal,
                EventPriority.values(),
                ByIdMap.OutOfBoundsStrategy.CLAMP);

        @Override
        public EventInvoker<T> asEventInvoker(@Nullable Object context) {
            return context != null ? (EventPhase phase, T callback) -> {
                this.register(phase, callback, context);
            } : this;
        }

        @Override
        public void register(EventPhase eventPhase, T callback) {
            this.register(eventPhase, callback, null);
        }

        private void register(EventPhase eventPhase, T callback, @Nullable Object context) {
            Objects.requireNonNull(eventPhase, "phase is null");
            Objects.requireNonNull(callback, "callback is null");
            eventPhase = this.eventPhaseConverter.apply(eventPhase);
            IEventBus eventBus = this.getEventBus(context);
            EventPriority eventPriority = getEventPriority(eventPhase);
            // filter out mod id which has been used to retrieve a missing mod event bus
            Object eventContext = this.eventBus != eventBus ? null : context;
            // we don't support receiving cancelled events since the event api on Fabric is not designed for it
            eventBus.addListener(eventPriority,
                    false,
                    this.event,
                    (E event) -> this.converter.accept(callback, event, eventContext));
        }

        private IEventBus getEventBus(@Nullable Object context) {
            if (this.eventBus == null) {
                Objects.requireNonNull(context, "mod id context is null");
                return NeoForgeModContainerHelper.getModEventBus((String) context);
            } else {
                return this.eventBus;
            }
        }

        private static EventPriority getEventPriority(EventPhase eventPhase) {
            if (PHASE_TO_PRIORITY.containsKey(eventPhase)) {
                return PHASE_TO_PRIORITY.get(eventPhase);
            } else {
                Objects.requireNonNull(eventPhase.parent(), "parent is null");
                EventPriority eventPriority = PHASE_TO_PRIORITY.getOrDefault(eventPhase.parent(), EventPriority.NORMAL);
                return PRIORITY_IDS.apply(eventPriority.ordinal() + eventPhase.getOrderingValue());
            }
        }
    }
}
