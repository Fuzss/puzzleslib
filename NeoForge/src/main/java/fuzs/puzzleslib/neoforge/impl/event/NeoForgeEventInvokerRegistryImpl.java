package fuzs.puzzleslib.neoforge.impl.event;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.common.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.common.impl.event.core.EventInvokerImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.api.event.v1.core.NeoForgeEventInvokerRegistry;
import net.minecraft.util.ByIdMap;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

public final class NeoForgeEventInvokerRegistryImpl implements NeoForgeEventInvokerRegistry {
    private boolean frozenModBusEvents;

    public void freezeModBusEvents() {
        this.frozenModBusEvents = true;
    }

    @Override
    public <T, E extends Event> void register(Class<T> clazz, Class<E> eventClazz, NeoForgeEventContextConsumer<T, E> converter, UnaryOperator<EventPhase> eventPhaseConverter, boolean joinInvokers) {
        Objects.requireNonNull(clazz, "type is null");
        Objects.requireNonNull(eventClazz, "event type is null");
        Objects.requireNonNull(converter, "converter is null");
        Preconditions.checkArgument(!Modifier.isAbstract(eventClazz.getModifiers()), eventClazz + " is abstract");
        IEventBus eventBus;
        if (IModBusEvent.class.isAssignableFrom(eventClazz)) {
            // Most events are registered during the load complete phase, where most mod bus events have already run,
            // so they will be missed silently. Check this lock to avoid that.
            Preconditions.checkState(!this.frozenModBusEvents, "Mod bus events already frozen");
            // this will be null when an event is registered after the initial mod loading
            eventBus = NeoForgeModContainerHelper.getOptionalActiveModEventBus().orElse(null);
        } else {
            eventBus = NeoForge.EVENT_BUS;
        }
        EventInvokerImpl.register(clazz,
                new NeoForgeEventInvoker<>(eventBus, eventClazz, converter, eventPhaseConverter),
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
