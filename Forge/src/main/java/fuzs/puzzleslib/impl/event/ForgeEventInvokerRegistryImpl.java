package fuzs.puzzleslib.impl.event;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import fuzs.puzzleslib.api.event.v1.core.*;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingExperienceDropCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.BonemealCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerXpEvents;
import fuzs.puzzleslib.api.event.v1.world.FarmlandTrampleCallback;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
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

public class ForgeEventInvokerRegistryImpl implements ForgeEventInvokerRegistry {
    public static final ForgeEventInvokerRegistryImpl INSTANCE = new ForgeEventInvokerRegistryImpl();
    private static final Map<Class<?>, EventInvoker<?>> EVENT_INVOKER_LOOKUP = new MapMaker().weakKeys().makeMap();

    static {
        INSTANCE.register(PlayerInteractEvents.UseItem.class, PlayerInteractEvent.RightClickItem.class, (PlayerInteractEvents.UseItem callback, PlayerInteractEvent.RightClickItem evt) -> {
            EventResultHolder<InteractionResultHolder<ItemStack>> result = callback.onRightClickItem(evt.getEntity(), evt.getLevel(), evt.getHand());
            if (result.isInterrupt()) {
                evt.setCancellationResult(result.getInterrupt().orElseThrow().getResult());
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
        INSTANCE.register(FarmlandTrampleCallback.class, BlockEvent.FarmlandTrampleEvent.class, (FarmlandTrampleCallback callback, BlockEvent.FarmlandTrampleEvent evt) -> {
            if (callback.onFarmlandTrample((Level) evt.getLevel(), evt.getPos(), evt.getState(), evt.getFallDistance(), evt.getEntity()).isInterrupt()) {
                evt.setCanceled(true);
            }
        });
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
