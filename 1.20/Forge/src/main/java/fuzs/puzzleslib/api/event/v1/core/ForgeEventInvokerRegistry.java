package fuzs.puzzleslib.api.event.v1.core;

import fuzs.puzzleslib.impl.event.ForgeEventInvokerRegistryImpl;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * A registry for linking common events implemented as functional interfaces to the corresponding Forge {@link Event} instances.
 */
public interface ForgeEventInvokerRegistry extends EventInvokerRegistry {
    ForgeEventInvokerRegistry INSTANCE = new ForgeEventInvokerRegistryImpl();

    /**
     * Registers an event.
     *
     * @param clazz     common event functional interface class
     * @param event     Forge event implementation
     * @param converter passes parameters from the Forge event to the common event instance
     * @param <T>       common event type
     * @param <E>       Forge event type
     */
    default <T, E extends Event> void register(Class<T> clazz, Class<E> event, BiConsumer<T, E> converter) {
        this.register(clazz, event, (T callback, E evt, Object context) -> converter.accept(callback, evt), false);
    }

    /**
     * Registers an event.
     *
     * @param clazz        common event functional interface class
     * @param event        Forge event implementation
     * @param converter    passes parameters from the Forge event to the common event instance
     * @param joinInvokers join this new event invoker with a possibly already existing one, otherwise an exception will be thrown when registering duplicates
     * @param <T>          common event type
     * @param <E>          Forge event type
     */
    default <T, E extends Event> void register(Class<T> clazz, Class<E> event, BiConsumer<T, E> converter, boolean joinInvokers) {
        this.register(clazz, event, (T callback, E evt, @Nullable Object context) -> converter.accept(callback, evt), joinInvokers);
    }

    /**
     * Registers an event.
     *
     * @param clazz     common event functional interface class
     * @param event     Forge event implementation
     * @param converter passes parameters from the Forge event to the common event instance, including a context
     * @param <T>       common event type
     * @param <E>       Forge event type
     */
    default <T, E extends Event> void register(Class<T> clazz, Class<E> event, ForgeEventContextConsumer<T, E> converter) {
        this.register(clazz, event, converter, false);
    }

    /**
     * Registers an event.
     *
     * @param clazz        common event functional interface class
     * @param event        Forge event implementation
     * @param converter    passes parameters from the Forge event to the common event instance, including a context
     * @param joinInvokers join this new event invoker with a possibly already existing one, otherwise an exception will be thrown when registering duplicates
     * @param <T>          common event type
     * @param <E>          Forge event type
     */
    <T, E extends Event> void register(Class<T> clazz, Class<E> event, ForgeEventContextConsumer<T, E> converter, boolean joinInvokers);

    /**
     * A helper context for dealing with context based {@link EventInvoker} implementations.
     *
     * @param <T> common event type
     * @param <E> Forge event type
     */
    @FunctionalInterface
    interface ForgeEventContextConsumer<T, E extends Event> {

        /**
         * Runs the consumer.
         *
         * @param callback our callback implementation to pass parameters from <code>event</code> to
         * @param event    the Forge event that is firing at this very moment
         * @param context  the context object, can be anything, but ideally an identifier such as a {@link Class} or {@link net.minecraft.resources.ResourceLocation}
         */
        void accept(T callback, E event, @Nullable Object context);
    }
}
