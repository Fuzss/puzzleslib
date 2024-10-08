package fuzs.puzzleslib.fabric.api.event.v1.core;

import com.google.common.reflect.AbstractInvocationHandler;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * A helper class for avoiding boilerplate code when defining new events on Fabric.
 * <p>
 * Especially useful when converting events from common to dedicated Fabric {@link Event}s.
 * <p>
 * Thanks a lot to <a
 * href="https://github.com/architectury/architectury-api/blob/1.19.3/common/src/main/java/dev/architectury/event/EventFactory.java">EventFactory.java</a>
 * from Architectury API for this implementation.
 */
@SuppressWarnings("unchecked")
public final class FabricEventFactory {

    /**
     * Creates a new event from a functional interface without a result, meaning the interface method returns
     * {@link Void}.
     *
     * @param type event type
     * @param <T>  event type
     * @return the Fabric event
     */
    public static <T> Event<T> create(Class<? super T> type) {
        return EventFactory.createArrayBacked(type,
                events -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(), new Class[]{type},
                        new AbstractInvocationHandler() {

                            @Override
                            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                                for (Object event : events) {
                                    invokeFast(event, method, args);
                                }
                                return null;
                            }
                        }
                )
        );
    }

    /**
     * Creates a new event from a functional interface with an event result, meaning the interface method returns
     * {@link EventResult}.
     *
     * <p>The implementation stops for the first callback that does not return {@link EventResult#PASS}.
     *
     * @param type event type
     * @param <T>  event type
     * @return the Fabric event
     */
    public static <T> Event<T> createResult(Class<? super T> type) {
        return EventFactory.createArrayBacked(type,
                events -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(), new Class[]{type},
                        new AbstractInvocationHandler() {

                            @Override
                            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                                for (Object event : events) {
                                    EventResult result = invokeFast(event, method, args);
                                    if (result.isInterrupt()) {
                                        return result;
                                    }
                                }
                                return EventResult.PASS;
                            }
                        }
                )
        );
    }

    /**
     * Creates a new event from a functional interface with an event result holder, meaning the interface method returns
     * an instance of {@link EventResultHolder}.
     *
     * <p>The implementation stops for the first callback that does not return {@link EventResultHolder#pass()}.
     *
     * @param type event type
     * @param <T>  event type
     * @return the Fabric event
     */
    public static <T> Event<T> createResultHolder(Class<? super T> type) {
        return EventFactory.createArrayBacked(type,
                events -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(), new Class[]{type},
                        new AbstractInvocationHandler() {

                            @Override
                            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                                for (Object event : events) {
                                    EventResultHolder<?> holder = invokeFast(event, method, args);
                                    if (holder.isInterrupt()) {
                                        return holder;
                                    }
                                }
                                return EventResultHolder.pass();
                            }
                        }
                )
        );
    }

    /**
     * Creates a new event from a functional interface with a boolean result, meaning the interface method returns a
     * primitive boolean.
     *
     * <p>The implementation stops for the first callback that returns the same value as provided by
     * <code>inverted</code>.
     *
     * <p>This helper method exists to aid in creating events similar to implementations in Fabric Api.
     *
     * @param type     event type
     * @param inverted should boolean result be inverted when determining when to interrupt event invocation
     * @param <T>      event type
     * @return the Fabric event
     */
    public static <T> Event<T> createBooleanResult(Class<? super T> type, boolean inverted) {
        return EventFactory.createArrayBacked(type,
                events -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(), new Class[]{type},
                        new AbstractInvocationHandler() {

                            @Override
                            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                                for (Object event : events) {
                                    boolean result = invokeFast(event, method, args);
                                    if (result == inverted) {
                                        return result;
                                    }
                                }
                                return !inverted;
                            }
                        }
                )
        );
    }

    /**
     * Creates a new event from a functional interface with a vanilla result, meaning the interface method returns
     * {@link InteractionResult}.
     *
     * <p>The implementation stops for the first callback that does not return {@link InteractionResult#PASS}.
     *
     * <p>This helper method exists to aid in creating events similar to implementations in Fabric Api.
     *
     * @param type event type
     * @param <T>  event type
     * @return the Fabric event
     */
    public static <T> Event<T> createInteractionResult(Class<? super T> type) {
        return EventFactory.createArrayBacked(type,
                events -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(), new Class[]{type},
                        new AbstractInvocationHandler() {

                            @Override
                            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                                for (Object event : events) {
                                    InteractionResult result = invokeFast(event, method, args);
                                    if (result != InteractionResult.PASS) {
                                        return result;
                                    }
                                }
                                return InteractionResult.PASS;
                            }
                        }
                )
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> T invokeFast(Object object, Method method, Object[] args) throws Throwable {
        return (T) MethodHandles.lookup().unreflect(method).bindTo(object).invokeWithArguments(args);
    }
}
