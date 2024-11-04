package fuzs.puzzleslib.api.event.v1.core;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An extension to {@link EventResult} allowing to attach any kind of value to the result, <code>null</code> values are not permitted.
 * <p>The implementation can be used similarly to Java's {@link Optional} class, allowing for functional patterns.
 * <p>Very similar to vanilla's {@link net.minecraft.world.InteractionResultHolder}.
 * <p>Implementation is also remotely similar to <a href="https://github.com/architectury/architectury-api/blob/1.19.3/common/src/main/java/dev/architectury/event/CompoundEventResult.java">CompoundEventResult.java</a> from Architectury API.
 *
 * @param <T> holder value type
 */
public final class EventResultHolder<T> {
    private static final EventResultHolder<?> PASS = new EventResultHolder<>();

    @NotNull
    private final EventResult result;
    @NotNull
    private final T value;

    /**
     * private constructor
     */
    @SuppressWarnings("DataFlowIssue")
    private EventResultHolder() {
        this.result = EventResult.PASS;
        this.value = null;
    }

    /**
     * private constructor
     */
    private EventResultHolder(@NotNull EventResult result, @NotNull T value) {
        Objects.requireNonNull(result, "result is null");
        Objects.requireNonNull(value, "value is null");
        this.result = result;
        this.value = value;
    }

    /**
     * @param <T> holder value type
     * @return default passing value instance
     */
    @SuppressWarnings("unchecked")
    public static <T> EventResultHolder<T> pass() {
        return (EventResultHolder<T>) PASS;
    }

    /**
     * @param value held value instance, <code>null</code> are not permitted
     * @param <T> holder value type
     * @return interrupt instance
     */
    public static <T> EventResultHolder<T> interrupt(@NotNull T value) {
        return new EventResultHolder<>(EventResult.INTERRUPT, value);
    }

    /**
     * @param value held value instance, <code>null</code> are not permitted
     * @param <T> holder value type
     * @return allow instance
     */
    public static <T> EventResultHolder<T> allow(@NotNull T value) {
        return new EventResultHolder<>(EventResult.ALLOW, value);
    }

    /**
     * @param value held value instance, <code>null</code> are not permitted
     * @param <T> holder value type
     * @return deny instance
     */
    public static <T> EventResultHolder<T> deny(@NotNull T value) {
        return new EventResultHolder<>(EventResult.DENY, value);
    }

    /**
     * @return does this result prevent the vanilla behavior from running
     */
    public boolean isInterrupt() {
        return this.result.isInterrupt();
    }

    /**
     * @return does this result allow for events to continue processing and for vanilla behavior to apply
     */
    public boolean isPass() {
        return this.result.isPass();
    }

    /**
     * @param action action to run if {@link #result} is {@link EventResult#INTERRUPT}
     * @return this instance
     */
    public EventResultHolder<T> ifInterrupt(final Consumer<? super T> action) {
        Objects.requireNonNull(action, "action is null");
        if (this.isInterrupt() && this.result.getAsBoolean()) {
            action.accept(this.value);
        }
        return this;
    }

    /**
     * @param action action to run if {@link #result} is {@link EventResult#ALLOW}
     * @return this instance
     */
    public EventResultHolder<T> ifAllow(final Consumer<? super T> action) {
        Objects.requireNonNull(action, "action is null");
        if (this.isInterrupt() && this.result.getAsBoolean()) {
            action.accept(this.value);
        }
        return this;
    }

    /**
     * @param action action to run if {@link #result} is {@link EventResult#DENY}
     * @return this instance
     */
    public EventResultHolder<T> ifDeny(final Consumer<? super T> action) {
        Objects.requireNonNull(action, "action is null");
        if (this.isInterrupt() && !this.result.getAsBoolean()) {
            action.accept(this.value);
        }
        return this;
    }

    /**
     * @param filter tests the contained value
     * @return this instance if <code>filter</code> succeeds, otherwise {@link #pass()}
     */
    public EventResultHolder<T> filter(Predicate<? super T> filter) {
        Objects.requireNonNull(filter, "filter is null");
        if (!this.isInterrupt()) {
            return this;
        } else {
            return filter.test(this.value) ? this : pass();
        }
    }

    /**
     * @param mapper mapping transformer for {@link #value}
     * @param <U> data type for new value
     * @return new holder containing the mapped value, {@link #result} is unchanged
     */
    public <U> EventResultHolder<U> map(final Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        if (!this.isInterrupt()) {
            return pass();
        } else {
            return new EventResultHolder<>(this.result, mapper.apply(this.value));
        }
    }

    /**
     * @param mapper mapping transformer for {@link #value}
     * @param <U> data type for new value
     * @return new holder containing the mapped value, {@link #result} is unchanged
     */
    @SuppressWarnings("unchecked")
    public <U> EventResultHolder<U> flatMap(final Function<? super T, ? extends EventResultHolder<? extends U>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        if (!this.isInterrupt()) {
            return pass();
        } else {
            EventResultHolder<U> holder = (EventResultHolder<U>) mapper.apply(this.value);
            Objects.requireNonNull(holder, "holder is null");
            return holder;
        }
    }

    /**
     * @return optional value if {@link #result} is {@link EventResult#INTERRUPT}
     */
    public Optional<T> getInterrupt() {
        if (!this.isInterrupt() || !this.result.getAsBoolean()) {
            return Optional.empty();
        } else {
            return Optional.of(this.value);
        }
    }

    /**
     * @return optional value if {@link #result} is {@link EventResult#ALLOW}
     */
    public Optional<T> getAllow() {
        if (!this.isInterrupt() || !this.result.getAsBoolean()) {
            return Optional.empty();
        } else {
            return Optional.of(this.value);
        }
    }

    /**
     * @return optional value if {@link #result} is {@link EventResult#DENY}
     */
    public Optional<T> getDeny() {
        if (!this.isInterrupt() || this.result.getAsBoolean()) {
            return Optional.empty();
        } else {
            return Optional.of(this.value);
        }
    }
}
