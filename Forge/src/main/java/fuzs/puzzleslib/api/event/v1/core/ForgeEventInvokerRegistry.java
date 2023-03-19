package fuzs.puzzleslib.api.event.v1.core;

import fuzs.puzzleslib.impl.event.ForgeEventInvokerRegistryImpl;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.BiConsumer;

/**
 * A registry for linking common events implemented as functional interfaces to the corresponding Forge {@link Event} instances.
 */
public interface ForgeEventInvokerRegistry {
    /**
     * the instance
     */
    ForgeEventInvokerRegistry INSTANCE = ForgeEventInvokerRegistryImpl.INSTANCE;

    /**
     * Registers an event.
     *
     * @param clazz common event functional interface class
     * @param event Forge event implementation
     * @param converter passes parameters from the Forge event to the common event instance
     * @param <T> common event type
     * @param <E> Forge event type
     */
    <T, E extends Event> void register(Class<T> clazz, Class<E> event, BiConsumer<T, E> converter);
}
