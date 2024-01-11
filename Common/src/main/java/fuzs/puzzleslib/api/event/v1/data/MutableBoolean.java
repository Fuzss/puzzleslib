package fuzs.puzzleslib.api.event.v1.data;

import fuzs.puzzleslib.impl.event.data.EventMutableBoolean;
import fuzs.puzzleslib.impl.event.data.ValueMutableBoolean;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * A mutable boolean implementation useful for events.
 */
public interface MutableBoolean extends BooleanSupplier {

    /**
     * Creates a new instance from an initial value.
     *
     * @param value initial value
     * @return new instance
     */
    static MutableBoolean fromValue(boolean value) {
        return new ValueMutableBoolean(value);
    }

    /**
     * Creates a new instance backed by a consumer and supplier.
     *
     * @param consumer value consumer
     * @param supplier value supplier
     * @return new instance
     */
    static MutableBoolean fromEvent(Consumer<Boolean> consumer, Supplier<Boolean> supplier) {
        return new EventMutableBoolean(consumer, supplier);
    }

    /**
     * A setter for the contained value.
     *
     * @param value new value to set
     */
    void accept(boolean value);

    /**
     * Maps the contained value to something new.
     *
     * @param operator operator to apply to contained value
     */
    default void mapBoolean(UnaryOperator<Boolean> operator) {
        this.accept(operator.apply(this.getAsBoolean()));
    }
}
