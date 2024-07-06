package fuzs.puzzleslib.impl.event.core;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.impl.core.CommonFactories;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Supplier;

public final class EventInvokerImpl {
    private static final Map<Class<?>, EventInvokerLike<?>> EVENT_INVOKER_LOOKUP = Collections.synchronizedMap(Maps.newIdentityHashMap());
    private static final Queue<Runnable> DEFERRED_INVOKER_REGISTRATIONS = Queues.newConcurrentLinkedQueue();

    private static boolean initialized;

    static {
        // initialize events required during start-up early, all other events are initialized when loading has completed
        CommonFactories.INSTANCE.registerLoadingHandlers();
    }

    private EventInvokerImpl() {
        // NO-OP
    }

    public static void initialize() {
        if (!initialized) {
            // initialize most of the events as late as possible to avoid loading many classes very early,
            // and being blamed for possible class loading errors that follow
            CommonFactories.INSTANCE.registerEventHandlers();
            initialized = true;
            while (!DEFERRED_INVOKER_REGISTRATIONS.isEmpty()) {
                DEFERRED_INVOKER_REGISTRATIONS.poll().run();
            }
        }
    }

    public static <T> EventInvoker<T> softLookup(Class<T> clazz, @Nullable Object context) {
        Objects.requireNonNull(clazz, "type is null");
        Supplier<EventInvoker<T>> invoker = Suppliers.memoize(() -> EventInvokerImpl.lookup(clazz, context));
        return (EventPhase phase, T callback) -> {
            if (!initialized && !EVENT_INVOKER_LOOKUP.containsKey(clazz)) {
                DEFERRED_INVOKER_REGISTRATIONS.offer(() -> {
                    invoker.get().register(phase, callback);
                });
            } else {
                // due to static initializers the invoker might not be present in the lookup just yet
                invoker.get().register(phase, callback);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> EventInvoker<T> lookup(Class<T> clazz, @Nullable Object context) {
        Objects.requireNonNull(clazz, "type is null");
        EventInvokerLike<T> invokerLike = (EventInvokerLike<T>) EVENT_INVOKER_LOOKUP.get(clazz);
        Objects.requireNonNull(invokerLike, "invoker is null for type " + clazz);
        EventInvoker<T> invoker = invokerLike.asEventInvoker(context);
        Objects.requireNonNull(invoker, "invoker is null for type " + clazz);
        return invoker;
    }

    @SuppressWarnings("unchecked")
    public static <T> void register(Class<T> clazz, EventInvokerLike<T> invoker, boolean joinInvokers) {
        EventInvokerLike<T> other = (EventInvokerLike<T>) EVENT_INVOKER_LOOKUP.get(clazz);
        if (other != null) {
            if (joinInvokers) {
                invoker = join(invoker, other);
            } else {
                throw new IllegalArgumentException("duplicate event invoker for type " + clazz);
            }
        }
        EVENT_INVOKER_LOOKUP.put(clazz, invoker);
    }

    private static <T> EventInvokerLike<T> join(EventInvokerLike<T> invoker, EventInvokerLike<T> other) {
        return (@Nullable Object context) -> (EventPhase phase, T callback) -> {
            invoker.asEventInvoker(context).register(phase, callback);
            other.asEventInvoker(context).register(phase, callback);
        };
    }

    @FunctionalInterface
    public interface EventInvokerLike<T> {

        EventInvoker<T> asEventInvoker(@Nullable Object context);
    }
}
