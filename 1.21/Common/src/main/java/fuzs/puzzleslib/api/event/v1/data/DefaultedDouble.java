package fuzs.puzzleslib.api.event.v1.data;

import fuzs.puzzleslib.impl.event.data.EventDefaultedDouble;
import fuzs.puzzleslib.impl.event.data.ValueDefaultedDouble;

import java.util.OptionalDouble;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

/**
 * A mutable double implementation with a default value useful for events.
 */
public interface DefaultedDouble extends MutableDouble {

    /**
     * Creates a new instance from an initial value.
     *
     * @param value initial value
     * @return new instance
     */
    static DefaultedDouble fromValue(double value) {
        return new ValueDefaultedDouble(value);
    }

    /**
     * Creates a new instance backed by a consumer and supplier.
     *
     * @param consumer value consumer
     * @param supplier value supplier
     * @param defaultSupplier default value supplier
     * @return new instance
     */
    static DefaultedDouble fromEvent(DoubleConsumer consumer, DoubleSupplier supplier, DoubleSupplier defaultSupplier) {
        return new EventDefaultedDouble(consumer, supplier, defaultSupplier);
    }

    /**
     * A getter for the default value.
     *
     * @return default value
     */
    double getAsDefaultDouble();

    /**
     * An optional getter for the contained value which will return empty if the value has not changed from the default value (determined via reference comparison).
     *
     * @return container value as optional
     */
    OptionalDouble getAsOptionalDouble();

    /**
     * Applies to default value to this instance.
     */
    default void applyDefaultDouble() {
        this.accept(this.getAsDefaultDouble());
    }

    /**
     * Sets the default value, then maps that value to something new.
     *
     * @param operator operator to apply to default value
     */
    default void mapDefaultDouble(DoubleUnaryOperator operator) {
        this.applyDefaultDouble();
        this.mapDouble(operator);
    }
}
