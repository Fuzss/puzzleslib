package fuzs.puzzleslib.api.event.v1.core;

import fuzs.puzzleslib.impl.event.ForgeEventInvokerRegistryImpl;
import net.minecraft.client.gui.screens.Screen;
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

    // TODO finish implementation

    /**
     * A helper context for dealing with context based {@link EventInvoker} implementations.
     *
     * @param <E> Fabric event type
     */
    @FunctionalInterface
    interface ForgeEventContextConsumer {

        /**
         * @param context        the context object, can be anything, but ideally an identifier such as a {@link Class} or {@link net.minecraft.resources.ResourceLocation}
         * @param applyToInvoker a consumer that runs immediately upon event invoker creation, responsible for registering the actual event to run whenever it is triggered
         * @param removeInvoker  an action for internally discarding the {@link EventInvoker} created in <code>applyToInvoker</code> that can be scheduled,
         *                       useful to run like for screens when {@link Screen#removed()} is called
         */
        void accept(Object context, Runnable applyToInvoker, Runnable removeInvoker);
    }
}
