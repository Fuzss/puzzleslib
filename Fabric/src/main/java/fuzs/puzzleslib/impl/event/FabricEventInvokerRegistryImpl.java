package fuzs.puzzleslib.impl.event;

import com.google.common.collect.MapMaker;
import fuzs.puzzleslib.api.event.v1.*;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.FabricEventInvokerRegistry;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingExperienceDropCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingFallCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.BonemealCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerXpEvents;
import fuzs.puzzleslib.api.event.v1.world.BlockEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Function;

public class FabricEventInvokerRegistryImpl implements FabricEventInvokerRegistry {
    public static final FabricEventInvokerRegistryImpl INSTANCE = new FabricEventInvokerRegistryImpl();
    private static final Map<Class<?>, EventInvoker<?>> EVENT_INVOKER_LOOKUP = new MapMaker().weakKeys().makeMap();

    static {
        INSTANCE.register(PlayerInteractEvents.UseItem.class, UseItemCallback.EVENT, callback -> {
            return (player, level, hand) -> callback.onRightClickItem(player, level, hand).getInterrupt().orElse(InteractionResultHolder.pass(ItemStack.EMPTY));
        });
        INSTANCE.register(PlayerXpEvents.PickupXp.class, FabricPlayerEvents.PICKUP_XP);
        INSTANCE.register(BonemealCallback.class, FabricPlayerEvents.BONEMEAL);
        INSTANCE.register(LivingExperienceDropCallback.class, FabricLivingEvents.EXPERIENCE_DROP);
        INSTANCE.register(BlockEvents.FarmlandTrample.class, FabricWorldEvents.FARMLAND_TRAMPLE);
        INSTANCE.register(PlayerTickEvents.Start.class, FabricEvents.PLAYER_TICK_START);
        INSTANCE.register(PlayerTickEvents.End.class, FabricEvents.PLAYER_TICK_END);
        INSTANCE.register(LivingFallCallback.class, FabricLivingEvents.LIVING_FALL);
    }

    @SuppressWarnings("unchecked")
    public <T> EventInvoker<T> lookup(Class<T> clazz) {
        Objects.requireNonNull(clazz, "type is null");
        EventInvoker<T> invoker = (EventInvoker<T>) EVENT_INVOKER_LOOKUP.get(clazz);
        Objects.requireNonNull(invoker, "invoker for type %s is null".formatted(clazz));
        return invoker;
    }

    @Override
    public <T, E> void register(Class<T> clazz, Event<E> event, Function<T, E> converter) {
        Objects.requireNonNull(clazz, "type is null");
        Objects.requireNonNull(event, "event is null");
        Objects.requireNonNull(converter, "converter is null");
        EventInvoker<T> invoker = new FabricEventInvoker<>(event, converter);
        if (EVENT_INVOKER_LOOKUP.put(clazz, invoker) != null) {
            throw new IllegalArgumentException("duplicate event invoker for type %s".formatted(clazz));
        }
    }

    private record FabricEventInvoker<T, E>(Event<E> event, Function<T, E> converter, Set<EventPhase> knownEventPhases) implements EventInvoker<T> {

        public FabricEventInvoker(Event<E> event, Function<T, E> converter) {
            this(event, converter, Collections.newSetFromMap(new MapMaker().weakKeys().makeMap()));
        }

        @Override
        public void register(EventPhase phase, T callback) {
            Objects.requireNonNull(phase, "phase is null");
            Objects.requireNonNull(callback, "callback is null");
            // this is the default phase
            if (phase.parent() == null) {
                this.event.register(this.converter.apply(callback));
            } else {
                // make sure phase has a phase ordering, we keep track of phases we have already added an ordering for in this event in #knownEventPhases
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
            // add a phase ordering for all parents in reverse order until we reach the phase we want to add
            while (!stack.isEmpty()) {
                phase = stack.pop();
                phase.applyOrdering(this.event::addPhaseOrdering);
                this.knownEventPhases.add(phase);
            }
        }
    }
}
