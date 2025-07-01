package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.impl.event.data.event.EventDefaultedInt;
import fuzs.puzzleslib.impl.event.data.value.ValueDefaultedInt;

import java.util.OptionalInt;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * A mutable int implementation with a default value useful for events.
 */
public interface DefaultedInt extends MutableInt {

    /**
     * Creates a new instance from an initial value.
     *
     * @param value initial value
     * @return new instance
     */
    static DefaultedInt fromValue(int value) {
        return new ValueDefaultedInt(value);
    }

    /**
     * Creates a new instance backed by a consumer and supplier.
     *
     * @param consumer        value consumer
     * @param supplier        value supplier
     * @param defaultSupplier default value supplier
     * @return new instance
     */
    static DefaultedInt fromEvent(IntConsumer consumer, IntSupplier supplier, IntSupplier defaultSupplier) {
        return new EventDefaultedInt(consumer, supplier, defaultSupplier);
    }

    /**
     * A getter for the default value.
     *
     * @return default value
     */
    int getAsDefaultInt();

    /**
     * An optional getter for the contained value which will return empty if the value has not changed from the default
     * value (determined via reference comparison).
     *
     * @return container value as optional
     */
    OptionalInt getAsOptionalInt();
}
