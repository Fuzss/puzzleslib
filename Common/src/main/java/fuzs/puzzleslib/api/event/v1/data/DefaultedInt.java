package fuzs.puzzleslib.api.event.v1.data;

import fuzs.puzzleslib.impl.event.data.EventDefaultedInt;
import fuzs.puzzleslib.impl.event.data.ValueDefaultedInt;

import java.util.OptionalInt;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;

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
     * @param consumer value consumer
     * @param supplier value supplier
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
     * An optional getter for the contained value which will return empty if the value has not changed from the default value (determined via reference comparison).
     *
     * @return container value as optional
     */
    OptionalInt getAsOptionalInt();

    /**
     * Applies to default value to this instance.
     */
    default void applyDefaultInt() {
        this.accept(this.getAsDefaultInt());
    }

    /**
     * Sets the default value, then maps that value to something new.
     *
     * @param operator operator to apply to default value
     */
    default void mapDefaultInt(IntUnaryOperator operator) {
        this.applyDefaultInt();
        this.mapInt(operator);
    }
}
