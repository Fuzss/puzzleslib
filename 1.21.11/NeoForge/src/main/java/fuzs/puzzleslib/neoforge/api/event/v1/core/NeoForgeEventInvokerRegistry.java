package fuzs.puzzleslib.neoforge.api.event.v1.core;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventInvokerRegistry;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.neoforge.impl.event.NeoForgeEventInvokerRegistryImpl;
import net.neoforged.bus.api.Event;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

/**
 * A registry for linking common events implemented as functional interfaces to the corresponding Forge {@link Event}
 * instances.
 */
public interface NeoForgeEventInvokerRegistry extends EventInvokerRegistry {
    /**
     * the instance
     */
    NeoForgeEventInvokerRegistry INSTANCE = new NeoForgeEventInvokerRegistryImpl();

    /**
     * Registers an event.
     *
     * @param clazz      common event functional interface class
     * @param eventClazz NeoForge event implementation
     * @param converter  passes parameters from the NeoForge event to the common event instance
     * @param <T>        common event type
     * @param <E>        NeoForge event type
     */
    default <T, E extends Event> void register(Class<T> clazz, Class<E> eventClazz, BiConsumer<T, E> converter) {
        this.register(clazz,
                eventClazz,
                (T callback, E event, Object context) -> converter.accept(callback, event),
                false);
    }

    /**
     * Registers an event.
     *
     * @param clazz        common event functional interface class
     * @param eventClazz   NeoForge event implementation
     * @param converter    passes parameters from the NeoForge event to the common event instance
     * @param joinInvokers join this new event invoker with a possibly already existing one, otherwise an exception will
     *                     be thrown when registering duplicates
     * @param <T>          common event type
     * @param <E>          NeoForge event type
     */
    default <T, E extends Event> void register(Class<T> clazz, Class<E> eventClazz, BiConsumer<T, E> converter, boolean joinInvokers) {
        this.register(clazz,
                eventClazz,
                (T callback, E event, @Nullable Object context) -> converter.accept(callback, event),
                joinInvokers);
    }

    /**
     * Registers an event.
     *
     * @param clazz      common event functional interface class
     * @param eventClazz NeoForge event implementation
     * @param converter  passes parameters from the NeoForge event to the common event instance, including a context
     * @param <T>        common event type
     * @param <E>        NeoForge event type
     */
    default <T, E extends Event> void register(Class<T> clazz, Class<E> eventClazz, NeoForgeEventContextConsumer<T, E> converter) {
        this.register(clazz, eventClazz, converter, false);
    }

    /**
     * Registers an event.
     *
     * @param clazz        common event functional interface class
     * @param eventClazz   NeoForge event implementation
     * @param converter    passes parameters from the NeoForge event to the common event instance, including a context
     * @param joinInvokers join this new event invoker with a possibly already existing one, otherwise an exception will
     *                     be thrown when registering duplicates
     * @param <T>          common event type
     * @param <E>          NeoForge event type
     */
    default <T, E extends Event> void register(Class<T> clazz, Class<E> eventClazz, NeoForgeEventContextConsumer<T, E> converter, boolean joinInvokers) {
        this.register(clazz, eventClazz, converter, UnaryOperator.identity(), joinInvokers);
    }

    /**
     * Registers an event.
     *
     * @param clazz               common event functional interface class
     * @param eventClazz          NeoForge event implementation
     * @param converter           passes parameters from the NeoForge event to the common event instance, including a
     *                            context
     * @param eventPhaseConverter an operator for adjusting the provided event phase, intended to impose an ordering on
     *                            different events that use the same underlying implementation
     * @param joinInvokers        join this new event invoker with a possibly already existing one, otherwise an
     *                            exception will be thrown when registering duplicates
     * @param <T>                 common event type
     * @param <E>                 NeoForge event type
     */
    <T, E extends Event> void register(Class<T> clazz, Class<E> eventClazz, NeoForgeEventContextConsumer<T, E> converter, UnaryOperator<EventPhase> eventPhaseConverter, boolean joinInvokers);

    /**
     * A helper context for dealing with context based {@link EventInvoker} implementations.
     *
     * @param <T> common event type
     * @param <E> NeoForge event type
     */
    @FunctionalInterface
    interface NeoForgeEventContextConsumer<T, E extends Event> {

        /**
         * Runs the consumer.
         *
         * @param callback our callback implementation to pass parameters from <code>event</code> to
         * @param event    the NeoForge event that is firing at this very moment
         * @param context  the context object, can be anything, but ideally an identifier such as a {@link Class} or
         *                 {@link net.minecraft.resources.Identifier}
         */
        void accept(T callback, E event, @Nullable Object context);
    }
}
