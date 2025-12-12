package fuzs.puzzleslib.fabric.api.event.v1.core;

import com.google.common.reflect.AbstractInvocationHandler;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * A helper class for avoiding boilerplate code when defining new events on Fabric.
 * <p>
 * Especially useful when converting events from common to dedicated {@link Event Fabric Events}.
 * <p>
 * Thanks a lot to <a
 * href="https://github.com/architectury/architectury-api/blob/1.19.3/common/src/main/java/dev/architectury/event/EventFactory.java">EventFactory.java</a>
 * from <a href="https://github.com/architectury/architectury-api">Architectury API</a> for this implementation.
 */
@SuppressWarnings("unchecked")
public final class FabricEventFactory {

    private FabricEventFactory() {
        // NO-OP
    }

    /**
     * Creates a new event from a {@link FunctionalInterface} returning {@link Void}.
     *
     * @param type the event type
     * @param <T>  the event type
     * @return the Fabric event implementation
     */
    public static <T> Event<T> create(Class<? super T> type) {
        return EventFactory.createArrayBacked(type,
                (T[] events) -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(),
                        new Class[]{type},
                        new AbstractInvocationHandler() {
                            @Override
                            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                                for (Object event : events) {
                                    invokeFast(event, method, args);
                                }
                                return null;
                            }
                        }));
    }

    /**
     * Creates a new event from a {@link FunctionalInterface} returning {@link EventResult}.
     * <p>
     * The implementation stops for the first callback that does not return {@link EventResult#PASS}.
     *
     * @param type the event type
     * @param <T>  the event type
     * @return the Fabric event implementation
     */
    public static <T> Event<T> createResult(Class<? super T> type) {
        return EventFactory.createArrayBacked(type,
                (T[] events) -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(),
                        new Class[]{type},
                        new AbstractInvocationHandler() {
                            @Override
                            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                                for (Object event : events) {
                                    Object o = invokeFast(event, method, args);
                                    if (o instanceof EventResult result) {
                                        if (result.isInterrupt()) {
                                            return o;
                                        }
                                    } else {
                                        PuzzlesLib.LOGGER.warn("Result mismatch for event {}", type.getName());
                                    }
                                }
                                return EventResult.PASS;
                            }
                        }));
    }

    /**
     * Creates a new event from a {@link FunctionalInterface} returning {@link EventResultHolder}.
     * <p>
     * The implementation stops for the first callback that does not return {@link EventResultHolder#pass()}.
     *
     * @param type the event type
     * @param <T>  the event type
     * @return the Fabric event implementation
     */
    public static <T> Event<T> createResultHolder(Class<? super T> type) {
        return EventFactory.createArrayBacked(type,
                (T[] events) -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(),
                        new Class[]{type},
                        new AbstractInvocationHandler() {
                            @Override
                            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                                for (Object event : events) {
                                    Object o = invokeFast(event, method, args);
                                    if (o instanceof EventResultHolder<?> holder) {
                                        if (holder.isInterrupt()) {
                                            return o;
                                        }
                                    } else {
                                        PuzzlesLib.LOGGER.warn("Result mismatch for event {}", type.getName());
                                    }
                                }
                                return EventResultHolder.pass();
                            }
                        }));
    }

    /**
     * Creates a new event from a {@link FunctionalInterface} returning an {@link Boolean}.
     * <p>
     * The implementation stops for the first callback that does return a value different from the method parameter.
     *
     * @param type  the event type
     * @param value the boolean value to match for an early return
     * @param <T>   the event type
     * @return the Fabric event implementation
     */
    public static <T> Event<T> createBooleanResult(Class<? super T> type, boolean value) {
        return EventFactory.createArrayBacked(type,
                (T[] events) -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(),
                        new Class[]{type},
                        new AbstractInvocationHandler() {
                            @Override
                            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                                for (Object event : events) {
                                    Object o = invokeFast(event, method, args);
                                    if (o instanceof Boolean bool) {
                                        if (bool == value) {
                                            return o;
                                        }
                                    } else {
                                        PuzzlesLib.LOGGER.warn("Result mismatch for event {}", type.getName());
                                    }
                                }
                                return !value;
                            }
                        }));
    }

    /**
     * Creates a new event from a {@link FunctionalInterface} returning {@link InteractionResult}.
     * <p>
     * The implementation stops for the first callback that does not return {@link InteractionResult#PASS}.
     *
     * @param type the event type
     * @param <T>  the event type
     * @return the Fabric event implementation
     */
    public static <T> Event<T> createInteractionResult(Class<? super T> type) {
        return EventFactory.createArrayBacked(type,
                (T[] events) -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(),
                        new Class[]{type},
                        new AbstractInvocationHandler() {
                            @Override
                            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                                for (Object event : events) {
                                    Object o = invokeFast(event, method, args);
                                    if (o instanceof InteractionResult interactionResult) {
                                        if (interactionResult != InteractionResult.PASS) {
                                            return o;
                                        }
                                    } else {
                                        PuzzlesLib.LOGGER.warn("Result mismatch for event {}", type.getName());
                                    }
                                }
                                return InteractionResult.PASS;
                            }
                        }));
    }

    private static Object invokeFast(Object object, Method method, Object[] args) throws Throwable {
        return MethodHandles.lookup().unreflect(method).bindTo(object).invokeWithArguments(args);
    }
}
