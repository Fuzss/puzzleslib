package fuzs.puzzleslib.impl.event;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.*;
import fuzs.puzzleslib.api.event.v1.core.*;
import fuzs.puzzleslib.api.event.v1.data.*;
import fuzs.puzzleslib.api.event.v1.entity.living.*;
import fuzs.puzzleslib.api.event.v1.entity.player.*;
import fuzs.puzzleslib.api.event.v1.world.BlockEvents;
import fuzs.puzzleslib.impl.client.event.ForgeClientEventInvokers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class ForgeEventInvokerRegistryImpl implements ForgeEventInvokerRegistry {
    public static final ForgeEventInvokerRegistryImpl INSTANCE = new ForgeEventInvokerRegistryImpl();
    private static final Map<Class<?>, EventInvoker<?>> EVENT_INVOKER_LOOKUP = new MapMaker().weakKeys().makeMap();

    static {
        INSTANCE.register(PlayerInteractEvents.UseBlock.class, PlayerInteractEvent.RightClickBlock.class, (PlayerInteractEvents.UseBlock callback, PlayerInteractEvent.RightClickBlock evt) -> {
            EventResultHolder<InteractionResult> result = callback.onUseBlock(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getHitVec());
            if (result.isInterrupt()) {
                evt.setCancellationResult(result.getInterrupt().orElseThrow());
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerInteractEvents.UseItem.class, PlayerInteractEvent.RightClickItem.class, (PlayerInteractEvents.UseItem callback, PlayerInteractEvent.RightClickItem evt) -> {
            EventResultHolder<InteractionResultHolder<ItemStack>> result = callback.onUseItem(evt.getEntity(), evt.getLevel(), evt.getHand());
            if (result.isInterrupt()) {
                evt.setCancellationResult(result.getInterrupt().orElseThrow().getResult());
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerInteractEvents.UseEntity.class, PlayerInteractEvent.EntityInteract.class, (PlayerInteractEvents.UseEntity callback, PlayerInteractEvent.EntityInteract evt) -> {
            EventResultHolder<InteractionResult> result = callback.onUseEntity(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getTarget());
            if (result.isInterrupt()) {
                evt.setCancellationResult(result.getInterrupt().orElseThrow());
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(PlayerInteractEvents.UseEntityAt.class, PlayerInteractEvent.EntityInteractSpecific.class, (PlayerInteractEvents.UseEntityAt callback, PlayerInteractEvent.EntityInteractSpecific evt) -> {
            EventResultHolder<InteractionResult> result = callback.onUseEntityAt(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getTarget(), evt.getLocalPos());
            if (result.isInterrupt()) {
                evt.setCancellationResult(result.getInterrupt().orElseThrow());
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
            callback.onStartTick(evt.player);
        });
        INSTANCE.register(PlayerTickEvents.End.class, TickEvent.PlayerTickEvent.class, (PlayerTickEvents.End callback, TickEvent.PlayerTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.END) return;
            callback.onEndTick(evt.player);
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
            callback.onReplaceLootTable(evt.getLootTableManager(), evt.getName(), table);
        });
        INSTANCE.register(LootTableLoadEvents.Modify.class, LootTableLoadEvent.class, (LootTableLoadEvents.Modify callback, LootTableLoadEvent evt) -> {
            callback.onModifyLootTable(evt.getLootTableManager(), evt.getName(), evt.getTable()::addPool, index -> {
                if (index == 0 && evt.getTable().removePool("main") != null) {
                    return true;
                }
                return evt.getTable().removePool("pool" + index) != null;
            });
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
            MutableValue<ItemStack> useItemResult = MutableValue.fromEvent(evt::setResultStack, evt::getResultStack);
            callback.onUseItemFinish(evt.getEntity(), evt.getItem(), evt.getDuration(), useItemResult);
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
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            ForgeClientEventInvokers.register();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> EventInvoker<T> lookup(Class<T> clazz) {
        Objects.requireNonNull(clazz, "type is null");
        EventInvoker<T> invoker = (EventInvoker<T>) EVENT_INVOKER_LOOKUP.get(clazz);
        Objects.requireNonNull(invoker, "invoker for type %s is null".formatted(clazz));
        return invoker;
    }

    @Override
    public <T, E extends Event> void register(Class<T> clazz, Class<E> event, BiConsumer<T, E> converter) {
        Objects.requireNonNull(clazz, "type is null");
        Objects.requireNonNull(converter, "converter is null");
        IEventBus eventBus;
        if (IModBusEvent.class.isAssignableFrom(event)) {
            if (ModLoadingContext.get().getActiveNamespace().equals("minecraft")) {
                throw new IllegalStateException("invalid active mod container");
            }
            eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        } else {
            eventBus = MinecraftForge.EVENT_BUS;
        }
        EventInvoker<T> invoker = new ForgeEventInvoker<>(eventBus, event, converter);
        if (EVENT_INVOKER_LOOKUP.put(clazz, invoker) != null) {
            throw new IllegalArgumentException("duplicate event invoker for type %s".formatted(clazz));
        }
    }

    private record ForgeEventInvoker<T, E extends Event>(IEventBus eventBus, Class<E> event, BiConsumer<T, E> converter) implements EventInvoker<T> {
        private static final Map<EventPhase, EventPriority> PHASE_TO_PRIORITY = ImmutableMap.<EventPhase, EventPriority>builder().put(EventPhase.FIRST, EventPriority.HIGHEST).put(EventPhase.BEFORE, EventPriority.HIGH).put(EventPhase.DEFAULT, EventPriority.NORMAL).put(EventPhase.AFTER, EventPriority.LOW).put(EventPhase.LAST, EventPriority.LOWEST).build();

        @Override
        public void register(EventPhase phase, T callback) {
            Objects.requireNonNull(phase, "phase is null");
            Objects.requireNonNull(callback, "callback is null");
            EventPriority eventPriority = PHASE_TO_PRIORITY.getOrDefault(phase, EventPriority.NORMAL);
            this.eventBus.addListener(eventPriority, false, this.event, (E evt) -> this.converter.accept(callback, evt));
        }
    }
}
