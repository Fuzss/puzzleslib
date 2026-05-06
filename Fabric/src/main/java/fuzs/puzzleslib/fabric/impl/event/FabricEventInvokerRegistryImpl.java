package fuzs.puzzleslib.fabric.impl.event;

import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.common.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.common.impl.event.core.EventInvokerImpl;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventInvokerRegistry;
import net.fabricmc.fabric.api.event.Event;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class FabricEventInvokerRegistryImpl implements FabricEventInvokerRegistry {

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
        EventInvokerImpl.register(clazz,
                new FabricForwardingEventInvoker<>(converter, consumer, eventPhaseConverter),
                joinInvokers);
    }

    private record FabricEventInvoker<T, E>(Event<E> event,
                                            FabricEventContextConverter<T, E> converter,
                                            UnaryOperator<EventPhase> eventPhaseConverter,
                                            Set<EventPhase> knownEventPhases) implements EventInvoker<T>, EventInvokerImpl.EventInvokerLike<T> {

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
        public void register(EventPhase eventPhase, T callback) {
            this.register(eventPhase, callback, null);
        }

        private void register(EventPhase eventPhase, T callback, @Nullable Object context) {
            Objects.requireNonNull(eventPhase, "phase is null");
            Objects.requireNonNull(callback, "callback is null");
            eventPhase = this.eventPhaseConverter.apply(eventPhase);
            // this is the default phase
            if (eventPhase.parent() == null) {
                this.event.register(this.converter.apply(callback, context));
            } else {
                // to make sure phase has consumer phase ordering, we keep track of phases we have already added an ordering for in this event in #knownEventPhases
                this.registerEventPhaseIfNecessary(eventPhase);
                this.event.register(eventPhase.identifier(), this.converter.apply(callback, context));
            }
        }

        private void registerEventPhaseIfNecessary(EventPhase eventPhase) {
            registerEventPhaseIfNecessary(this.event, eventPhase, this.knownEventPhases);
        }

        private static void registerEventPhaseIfNecessary(Event<?> event, EventPhase eventPhase, Collection<EventPhase> knownEventPhases) {
            Stack<EventPhase> stack = new Stack<>();
            // find the first parent we already know (probably default)
            while (eventPhase.parent() != null && !knownEventPhases.contains(eventPhase)) {
                stack.push(eventPhase);
                eventPhase = eventPhase.parent();
            }
            // add consumer phase ordering for all parents in reverse order until we reach the phase we want to add
            while (!stack.isEmpty()) {
                eventPhase = stack.pop();
                eventPhase.applyOrdering(event::addPhaseOrdering);
                knownEventPhases.add(eventPhase);
                EventPhase parentEventPhase = eventPhase;
                while ((parentEventPhase = parentEventPhase.parent()) != null && parentEventPhase.parent() != null) {
                    if (eventPhase.getOrderingValue() != parentEventPhase.getOrderingValue()) {
                        parentEventPhase.applyOrdering(eventPhase.identifier(), event::addPhaseOrdering);
                        break;
                    }
                }
            }
        }
    }

    private record FabricForwardingEventInvoker<T, E>(Function<Event<E>, EventInvokerImpl.EventInvokerLike<T>> factory,
                                                      FabricEventContextConsumer<E> consumer,
                                                      Map<Event<E>, EventInvokerImpl.EventInvokerLike<T>> events) implements EventInvokerImpl.EventInvokerLike<T> {

        public FabricForwardingEventInvoker(FabricEventContextConverter<T, E> converter, FabricEventContextConsumer<E> consumer, UnaryOperator<EventPhase> eventPhaseConverter) {
            this((Event<E> event) -> new FabricEventInvoker<>(event, converter, eventPhaseConverter),
                    consumer,
                    new MapMaker().weakKeys().concurrencyLevel(1).makeMap());
        }

        @Override
        public EventInvoker<T> asEventInvoker(Object context) {
            Objects.requireNonNull(context, "context is null");
            // keeping track of events and corresponding invoker is not so important,
            // since there is only ever one event per context anyway which is guaranteed by the underlying implementation,
            // but for managing event phases, it becomes necessary to use our FabricEventInvoker to keep track
            return (EventPhase phase, T callback) -> {
                this.consumer.accept(context, (Event<E> event) -> {
                    this.events.computeIfAbsent(event, this.factory).asEventInvoker(context).register(phase, callback);
                }, this.events::remove);
            };
        }
    }
}
