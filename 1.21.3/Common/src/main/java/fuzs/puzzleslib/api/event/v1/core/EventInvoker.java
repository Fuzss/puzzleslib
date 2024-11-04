package fuzs.puzzleslib.api.event.v1.core;

import fuzs.puzzleslib.impl.event.core.EventInvokerImpl;
import org.jetbrains.annotations.Nullable;

/**
 * Main event class for common events.
 * <p>The events are <b>NOT</b> implemented in common, all that is left to Fabric / Forge subprojects.
 * <p>Instances are usually created by performing a {@link #lookup(Class)} which will retrieve the actually implemented
 * event from the mod loader-specific subproject.
 * <p>An invoker can also be implemented as a functional interface for the rare case of using a different invoker to
 * run
 * the event, not requiring a mod loader-specific implementation.
 *
 * @param <T> event type
 */
@FunctionalInterface
public interface EventInvoker<T> {

    /**
     * Retrieves the actually implemented event from the mod loader-specific subproject.
     *
     * @param clazz event type
     * @param <T>   event type
     * @return mod loader-specific invoker, will throw an exception is none is present
     */
    static <T> EventInvoker<T> lookup(Class<T> clazz) {
        return lookup(clazz, null);
    }

    /**
     * Retrieves the actually implemented event from the mod loader-specific subproject.
     *
     * @param clazz   event type
     * @param context additional context for the event invoker to look up, like a screen class for screen events
     *                <p>
     *                can pass a mod id for looking up the mod event bus on NeoForge if it's not accessible from the mod
     *                loading context
     * @param <T>     event type
     * @return mod loader-specific invoker, will throw an exception is none is present
     */
    static <T> EventInvoker<T> lookup(Class<T> clazz, @Nullable Object context) {
        return EventInvokerImpl.softLookup(clazz, context);
    }

    /**
     * Registers an event.
     * <p>On Forge the correct event bus is automatically chosen, the mod event bus is retrieved from the active
     * <code>FMLJavaModLoadingContext</code>.
     * <p>If <code>IModbusEvent</code>s are registered too late an exception is thrown.
     *
     * @param callback the event callback
     */
    default void register(T callback) {
        this.register(EventPhase.DEFAULT, callback);
    }

    /**
     * Registers an event.
     * <p>On Forge the correct event bus is automatically chosen, the mod event bus is retrieved from the active
     * <code>FMLJavaModLoadingContext</code>.
     * <p>If events of type <code>IModbusEvent</code> are registered too late an exception is thrown.
     *
     * @param phase    the order in relation to other events
     * @param callback the event callback
     */
    void register(EventPhase phase, T callback);
}
