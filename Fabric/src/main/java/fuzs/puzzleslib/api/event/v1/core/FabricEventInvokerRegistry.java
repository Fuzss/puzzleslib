package fuzs.puzzleslib.api.event.v1.core;

import fuzs.puzzleslib.impl.event.FabricEventInvokerRegistryImpl;
import net.fabricmc.fabric.api.event.Event;

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
     * @param <T> event type
     */
    default <T> void register(Class<T> clazz, Event<T> event) {
        this.register(clazz, event, Function.identity());
    }

    /**
     * Registers an event.
     * <p>Useful for linking an event already implemented on Fabric (most likely from Fabric Api) to the common implementation.
     *
     * @param clazz common event functional interface class
     * @param event Fabric event implementation
     * @param converter returns an implementation of the Fabric event that runs the passed in common event
     * @param <T> common event type
     * @param <E> Fabric event type
     */
    <T, E> void register(Class<T> clazz, Event<E> event, Function<T, E> converter);
}
