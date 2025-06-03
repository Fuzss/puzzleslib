package fuzs.puzzleslib.api.event.v1.data;

import fuzs.puzzleslib.impl.event.data.EventMutableValue;
import fuzs.puzzleslib.impl.event.data.ValueMutableValue;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * A mutable value implementation useful for events.
 */
public interface MutableValue<T> extends Consumer<T>, Supplier<T> {

    /**
     * Creates a new instance from an initial value.
     *
     * @param value initial value
     * @return new instance
     */
    static <T> MutableValue<T> fromValue(T value) {
        return new ValueMutableValue<>(value);
    }

    /**
     * Creates a new instance backed by a consumer and supplier.
     *
     * @param consumer value consumer
     * @param supplier value supplier
     * @return new instance
     */
    static <T> MutableValue<T> fromEvent(Consumer<T> consumer, Supplier<T> supplier) {
        return new EventMutableValue<>(consumer, supplier);
    }

    /**
     * Maps the contained value to something new.
     *
     * @param operator operator to apply to contained value
     */
    default void map(UnaryOperator<T> operator) {
        this.accept(operator.apply(this.get()));
    }
}
