package fuzs.puzzleslib.api.event.v1.data;

import fuzs.puzzleslib.impl.event.data.EventMutableDouble;
import fuzs.puzzleslib.impl.event.data.ValueMutableDouble;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

/**
 * A mutable double implementation useful for events.
 */
public interface MutableDouble extends DoubleConsumer, DoubleSupplier {

    /**
     * Creates a new instance from an initial value.
     *
     * @param value initial value
     * @return new instance
     */
    static MutableDouble fromValue(double value) {
        return new ValueMutableDouble(value);
    }

    /**
     * Creates a new instance backed by a consumer and supplier.
     *
     * @param consumer value consumer
     * @param supplier value supplier
     * @return new instance
     */
    static MutableDouble fromEvent(DoubleConsumer consumer, DoubleSupplier supplier) {
        return new EventMutableDouble(consumer, supplier);
    }

    /**
     * Maps the contained value to something new.
     *
     * @param operator operator to apply to contained value
     */
    default void mapDouble(DoubleUnaryOperator operator) {
        this.accept(operator.applyAsDouble(this.getAsDouble()));
    }
}
