package fuzs.puzzleslib.impl.event.core;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.impl.core.CommonFactories;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class EventInvokerImpl {
    private static final Map<Class<?>, EventInvokerLike<?>> EVENT_INVOKER_LOOKUP = Collections.synchronizedMap(Maps.newIdentityHashMap());

    static {
        CommonFactories.INSTANCE.registerEventInvokers();
    }

    private EventInvokerImpl() {

    }

    @SuppressWarnings("unchecked")
    public static <T> EventInvoker<T> lookup(Class<T> clazz, @Nullable Object context) {
        Objects.requireNonNull(clazz, "type is null");
        EventInvokerLike<T> invokerLike = (EventInvokerLike<T>) EVENT_INVOKER_LOOKUP.get(clazz);
        Objects.requireNonNull(invokerLike, "invoker for type %s is null".formatted(clazz));
        EventInvoker<T> invoker = invokerLike.asEventInvoker(context);
        Objects.requireNonNull(invoker, "invoker for type %s is null".formatted(clazz));
        return invoker;
    }

    @SuppressWarnings("unchecked")
    public static <T> void register(Class<T> clazz, EventInvokerLike<T> invoker, boolean joinInvokers) {
        EventInvokerLike<T> other = (EventInvokerLike<T>) EVENT_INVOKER_LOOKUP.get(clazz);
        if (other != null) {
            if (joinInvokers) {
                invoker = join(invoker, other);
            } else {
                throw new IllegalArgumentException("duplicate event invoker for type %s".formatted(clazz));
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

    public interface EventInvokerLike<T> {

        EventInvoker<T> asEventInvoker(@Nullable Object context);
    }
}
