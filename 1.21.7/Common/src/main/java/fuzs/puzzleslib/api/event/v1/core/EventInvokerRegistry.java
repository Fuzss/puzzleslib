package fuzs.puzzleslib.api.event.v1.core;

import fuzs.puzzleslib.impl.event.core.EventInvokerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A registry for linking common events implemented as functional interfaces to the corresponding mod loader specific event instances.
 */
public interface EventInvokerRegistry {

    /**
     * Registers an event.
     *
     * @param clazz        common event functional interface class
     * @param converter    custom implementation of common event instance independent of an actual corresponding Forge event
     * @param <T>          common event type
     */
    default <T> void register(Class<T> clazz, BiConsumer<T, @Nullable Object> converter) {
        this.register(clazz, converter, false);
    }

    /**
     * Registers an event.
     *
     * @param clazz        common event functional interface class
     * @param converter    custom implementation of common event instance independent of an actual corresponding Forge event
     * @param joinInvokers join this new event invoker with a possibly already existing one, otherwise an exception will be thrown when registering duplicates
     * @param <T>          common event type
     */
    default <T> void register(Class<T> clazz, BiConsumer<T, @Nullable Object> converter, boolean joinInvokers) {
        Objects.requireNonNull(clazz, "type is null");
        Objects.requireNonNull(converter, "converter is null");
        EventInvokerImpl.register(clazz, context -> {
            return (EventPhase phase, T callback) -> {
                if (phase != EventPhase.DEFAULT) {
                    throw new IllegalStateException("implementation does not support event phases");
                }
                converter.accept(callback, context);
            };
        }, joinInvokers);
    }
}
