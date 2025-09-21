package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import fuzs.puzzleslib.impl.event.data.event.EventDefaultedDouble;
import fuzs.puzzleslib.impl.event.data.value.ValueDefaultedDouble;

import java.util.OptionalDouble;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

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
     * @param consumer        value consumer
     * @param supplier        value supplier
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
     * An optional getter for the contained value which will return empty if the value has not changed from the default
     * value (determined via reference comparison).
     *
     * @return container value as optional
     */
    OptionalDouble getAsOptionalDouble();
}
