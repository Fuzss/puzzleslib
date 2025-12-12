package fuzs.puzzleslib.fabric.api.event.v1.core;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventInvokerRegistry;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.fabric.impl.event.FabricEventInvokerRegistryImpl;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A registry for linking common events implemented as functional interfaces to the corresponding Fabric {@link Event}
 * instances.
 * <p>See {@link FabricEventFactory} for easily creating dedicated Fabric events from common event implementations.
 */
public interface FabricEventInvokerRegistry extends EventInvokerRegistry {
    /**
     * the instance
     */
    FabricEventInvokerRegistry INSTANCE = new FabricEventInvokerRegistryImpl();

    /**
     * Registers an event that uses the same type in both common and Fabric subprojects.
     * <p>See {@link FabricEventFactory} for easily creating dedicated Fabric events from common event implementations
     * to use here.
     *
     * @param clazz common event functional interface class
     * @param event Fabric event implementation
     * @param <T>   event type
     */
    default <T> void register(Class<T> clazz, Event<T> event) {
        this.register(clazz, event, Function.identity());
    }

    /**
     * Registers an event.
     * <p>Useful for linking an event already implemented on Fabric (most likely from Fabric Api) to the common
     * implementation.
     *
     * @param clazz     common event functional interface class
     * @param event     Fabric event implementation
     * @param converter returns an implementation of the Fabric event that runs the passed in common event
     * @param <T>       common event type
     * @param <E>       Fabric event type
     */
    default <T, E> void register(Class<T> clazz, Event<E> event, Function<T, E> converter) {
        this.register(clazz, event, converter, false);
    }

    /**
     * Registers an event.
     * <p>Useful for linking an event already implemented on Fabric (most likely from Fabric Api) to the common
     * implementation.
     *
     * @param clazz        common event functional interface class
     * @param event        Fabric event implementation
     * @param converter    returns an implementation of the Fabric event that runs the passed in common event
     * @param joinInvokers join this new event invoker with a possibly already existing one, otherwise an exception will
     *                     be thrown when registering duplicates
     * @param <T>          common event type
     * @param <E>          Fabric event type
     */
    default <T, E> void register(Class<T> clazz, Event<E> event, Function<T, E> converter, boolean joinInvokers) {
        this.register(clazz, event, (T callback, @Nullable Object context) -> converter.apply(callback), joinInvokers);
    }

    /**
     * Registers an event.
     * <p>Useful for linking an event already implemented on Fabric (most likely from Fabric Api) to the common
     * implementation.
     *
     * @param clazz     common event functional interface class
     * @param event     Fabric event implementation
     * @param converter returns an implementation of the Fabric event that runs the passed in common event
     * @param <T>       common event type
     * @param <E>       Fabric event type
     */
    default <T, E> void register(Class<T> clazz, Event<E> event, FabricEventContextConverter<T, E> converter) {
        this.register(clazz, event, converter, false);
    }

    /**
     * Registers an event.
     * <p>Useful for linking an event already implemented on Fabric (most likely from Fabric Api) to the common
     * implementation.
     *
     * @param clazz        common event functional interface class
     * @param event        Fabric event implementation
     * @param converter    returns an implementation of the Fabric event that runs the passed in common event
     * @param joinInvokers join this new event invoker with a possibly already existing one, otherwise an exception will
     *                     be thrown when registering duplicates
     * @param <T>          common event type
     * @param <E>          Fabric event type
     */
    default <T, E> void register(Class<T> clazz, Event<E> event, FabricEventContextConverter<T, E> converter, boolean joinInvokers) {
        this.register(clazz, event, converter, UnaryOperator.identity(), joinInvokers);
    }

    /**
     * Registers an event.
     * <p>Useful for linking an event already implemented on Fabric (most likely from Fabric Api) to the common
     * implementation.
     *
     * @param clazz               common event functional interface class
     * @param event               Fabric event implementation
     * @param converter           returns an implementation of the Fabric event that runs the passed in common event
     * @param eventPhaseConverter an operator for adjusting the provided event phase, intended to impose an ordering on
     *                            different events that use the same underlying implementation
     * @param joinInvokers        join this new event invoker with a possibly already existing one, otherwise an
     *                            exception will be thrown when registering duplicates
     * @param <T>                 common event type
     * @param <E>                 Fabric event type
     */
    <T, E> void register(Class<T> clazz, Event<E> event, FabricEventContextConverter<T, E> converter, UnaryOperator<EventPhase> eventPhaseConverter, boolean joinInvokers);

    /**
     * Registers an event that uses the same type in both common and Fabric subprojects and depends on a certain
     * context.
     * <p>See {@link FabricEventFactory} for easily creating dedicated Fabric events from common event implementations
     * to use here.
     *
     * @param clazz    common event functional interface class
     * @param consumer a consumer that runs immediately upon event invoker creation, responsible for registering the
     *                 actual event to run whenever it is triggered, like registering a screen event in
     *                 {@link net.fabricmc.fabric.api.client.screen.v1.ScreenEvents#BEFORE_INIT}, where registering to
     *                 {@link net.fabricmc.fabric.api.client.screen.v1.ScreenEvents#BEFORE_INIT} happens immediately
     * @param <T>      common event type
     */
    default <T> void register(Class<T> clazz, FabricEventContextConsumer<T> consumer) {
        this.register(clazz, clazz, Function.identity(), consumer);
    }

    /**
     * Registers an event that depends on a certain context, such as screen events depending on a screen instance (they
     * are not global like most events).
     * <p>Useful for linking an event already implemented on Fabric (most likely from Fabric Api) to the common
     * implementation.
     *
     * @param clazz     common event functional interface class
     * @param eventType Fabric event implementation type, since the actual instance changes and is therefore not
     *                  available in this global context
     * @param converter returns an implementation of the Fabric event that runs the passed in common event
     * @param consumer  a consumer that runs immediately upon event invoker creation, responsible for registering the
     *                  actual event to run whenever it is triggered, like registering a screen event in
     *                  {@link net.fabricmc.fabric.api.client.screen.v1.ScreenEvents#BEFORE_INIT}, where registering to
     *                  {@link net.fabricmc.fabric.api.client.screen.v1.ScreenEvents#BEFORE_INIT} happens immediately
     * @param <T>       common event type
     * @param <E>       Fabric event type
     */
    default <T, E> void register(Class<T> clazz, Class<E> eventType, Function<T, E> converter, FabricEventContextConsumer<E> consumer) {
        this.register(clazz, eventType, converter, consumer, UnaryOperator.identity(), false);
    }

    /**
     * Registers an event that depends on a certain context, such as screen events depending on a screen instance (they
     * are not global like most events).
     * <p>Useful for linking an event already implemented on Fabric (most likely from Fabric Api) to the common
     * implementation.
     *
     * @param clazz               common event functional interface class
     * @param eventType           Fabric event implementation type, since the actual instance changes and is therefore
     *                            not available in this global context
     * @param converter           returns an implementation of the Fabric event that runs the passed in common event
     * @param consumer            a consumer that runs immediately upon event invoker creation, responsible for
     *                            registering the actual event to run whenever it is triggered, like registering a
     *                            screen event in
     *                            {@link net.fabricmc.fabric.api.client.screen.v1.ScreenEvents#BEFORE_INIT}, where
     *                            registering to
     *                            {@link net.fabricmc.fabric.api.client.screen.v1.ScreenEvents#BEFORE_INIT} happens
     *                            immediately
     * @param eventPhaseConverter an operator for adjusting the provided event phase, intended to impose an ordering on
     *                            different events that use the same underlying implementation
     * @param joinInvokers        join this new event invoker with a possibly already existing one, otherwise an
     *                            exception will be thrown when registering duplicates
     * @param <T>                 common event type
     * @param <E>                 Fabric event type
     */
    default <T, E> void register(Class<T> clazz, Class<E> eventType, Function<T, E> converter, FabricEventContextConsumer<E> consumer, UnaryOperator<EventPhase> eventPhaseConverter, boolean joinInvokers) {
        this.register(clazz, eventType, (T callback, @Nullable Object context) -> converter.apply(callback), consumer, eventPhaseConverter, joinInvokers);
    }

    /**
     * Registers an event that depends on a certain context, such as screen events depending on a screen instance (they
     * are not global like most events).
     * <p>Useful for linking an event already implemented on Fabric (most likely from Fabric Api) to the common
     * implementation.
     *
     * @param clazz               common event functional interface class
     * @param eventType           Fabric event implementation type, since the actual instance changes and is therefore
     *                            not available in this global context
     * @param converter           returns an implementation of the Fabric event that runs the passed in common event
     * @param consumer            a consumer that runs immediately upon event invoker creation, responsible for
     *                            registering the actual event to run whenever it is triggered, like registering a
     *                            screen event in
     *                            {@link net.fabricmc.fabric.api.client.screen.v1.ScreenEvents#BEFORE_INIT}, where
     *                            registering to
     *                            {@link net.fabricmc.fabric.api.client.screen.v1.ScreenEvents#BEFORE_INIT} happens
     *                            immediately
     * @param eventPhaseConverter an operator for adjusting the provided event phase, intended to impose an ordering on
     *                            different events that use the same underlying implementation
     * @param joinInvokers        join this new event invoker with a possibly already existing one, otherwise an
     *                            exception will be thrown when registering duplicates
     * @param <T>                 common event type
     * @param <E>                 Fabric event type
     */
    <T, E> void register(Class<T> clazz, Class<E> eventType, FabricEventContextConverter<T, E> converter, FabricEventContextConsumer<E> consumer, UnaryOperator<EventPhase> eventPhaseConverter, boolean joinInvokers);

    /**
     * A helper context for dealing with context based {@link EventInvoker} implementations.
     *
     * @param <T> common event type
     * @param <E> Fabric event type
     */
    @FunctionalInterface
    interface FabricEventContextConverter<T, E> {

        /**
         * Runs the converter.
         *
         * @param callback our callback implementation used in the returned Fabric event implementation
         * @param context  the context object, can be anything, but ideally an identifier such as a {@link Class} or
         *                 {@link net.minecraft.resources.Identifier}
         * @return the Fabric event implementation
         */
        E apply(T callback, @Nullable Object context);
    }

    /**
     * A helper context for dealing with context based {@link EventInvoker} implementations.
     *
     * @param <E> Fabric event type
     */
    @FunctionalInterface
    interface FabricEventContextConsumer<E> {

        /**
         * Runs the consumer.
         *
         * @param context        the context object, can be anything, but ideally an identifier such as a {@link Class}
         *                       or {@link net.minecraft.resources.Identifier}
         * @param applyToInvoker a consumer that runs immediately upon event invoker creation, responsible for
         *                       registering the actual event to run whenever it is triggered
         * @param removeInvoker  an action for internally discarding the {@link EventInvoker} created in
         *                       <code>applyToInvoker</code> that can be scheduled, useful to run like for screens when
         *                       {@link Screen#removed()} is called
         */
        void accept(Object context, Consumer<Event<E>> applyToInvoker, Consumer<Event<E>> removeInvoker);
    }
}
