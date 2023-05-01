package fuzs.puzzleslib.api.event.v1.core;

import fuzs.puzzleslib.impl.event.FabricEventInvokerRegistryImpl;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A registry for linking common events implemented as functional interfaces to the corresponding Fabric {@link Event} instances.
 * <p>See {@link FabricEventFactory} for easily creating dedicated Fabric events from common event implementations.
 */
public interface FabricEventInvokerRegistry {
    /**
     * the instance
     */
    FabricEventInvokerRegistry INSTANCE = FabricEventInvokerRegistryImpl.INSTANCE;

    /**
     * Registers an event that uses the same type in both common and Fabric subprojects.
     * <p>See {@link FabricEventFactory} for easily creating dedicated Fabric events from common event implementations to use here.
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
     * <p>Useful for linking an event already implemented on Fabric (most likely from Fabric Api) to the common implementation.
     *
     * @param clazz     common event functional interface class
     * @param event     Fabric event implementation
     * @param converter returns an implementation of the Fabric event that runs the passed in common event
     * @param <T>       common event type
     * @param <E>       Fabric event type
     */
    <T, E> void register(Class<T> clazz, Event<E> event, Function<T, E> converter);

    // TODO check this all works correctly

    /**
     * Registers an event that uses the same type in both common and Fabric subprojects and depends on a certain context.
     * <p>See {@link FabricEventFactory} for easily creating dedicated Fabric events from common event implementations to use here.
     *
     * @param clazz    common event functional interface class
     * @param consumer a consumer that runs immediately upon event invoker creation, responsible for registering the actual event to run whenever it is triggered,
     *                 like registering a screen event in {@link net.fabricmc.fabric.api.client.screen.v1.ScreenEvents#BEFORE_INIT},
     *                 where registering to {@link net.fabricmc.fabric.api.client.screen.v1.ScreenEvents#BEFORE_INIT} happens immediately
     * @param <T>      common event type
     */
    default <T> void register(Class<T> clazz, FabricEventContextConsumer<T> consumer) {
        this.register(clazz, clazz, Function.identity(), consumer);
    }

    /**
     * Registers an event that depends on a certain context, such as screen events depending on a screen instance (they are not global like most events).
     * <p>Useful for linking an event already implemented on Fabric (most likely from Fabric Api) to the common implementation.
     *
     * @param clazz     common event functional interface class
     * @param eventType Fabric event implementation type, since the actual instance changes and is therefore not available in this global context
     * @param converter returns an implementation of the Fabric event that runs the passed in common event
     * @param consumer  a consumer that runs immediately upon event invoker creation, responsible for registering the actual event to run whenever it is triggered,
     *                  like registering a screen event in {@link net.fabricmc.fabric.api.client.screen.v1.ScreenEvents#BEFORE_INIT},
     *                  where registering to {@link net.fabricmc.fabric.api.client.screen.v1.ScreenEvents#BEFORE_INIT} happens immediately
     * @param <T>       common event type
     * @param <E>       Fabric event type
     */
    <T, E> void register(Class<T> clazz, Class<E> eventType, Function<T, E> converter, FabricEventContextConsumer<E> consumer);

    /**
     * A helper context for dealing with context based {@link EventInvoker} implementations.
     *
     * @param <E> Fabric event type
     */
    @FunctionalInterface
    interface FabricEventContextConsumer<E> {

        /**
         * @param context        the context object, can be anything, but ideally an identifier such as a {@link Class} or {@link net.minecraft.resources.ResourceLocation}
         * @param applyToInvoker a consumer that runs immediately upon event invoker creation, responsible for registering the actual event to run whenever it is triggered
         * @param removeInvoker  an action for internally discarding the {@link EventInvoker} created in <code>applyToInvoker</code> that can be scheduled,
         *                       useful to run like for screens when {@link Screen#removed()} is called
         */
        void accept(Object context, Consumer<Event<E>> applyToInvoker, Consumer<Event<E>> removeInvoker);
    }
}
