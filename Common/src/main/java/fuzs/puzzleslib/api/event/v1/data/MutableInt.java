package fuzs.puzzleslib.api.event.v1.data;

import fuzs.puzzleslib.impl.event.data.event.EventMutableInt;
import fuzs.puzzleslib.impl.event.data.value.ValueMutableInt;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;

/**
 * A mutable int implementation useful for events.
 */
public interface MutableInt extends IntConsumer, IntSupplier {

    /**
     * Creates a new instance from an initial value.
     *
     * @param value initial value
     * @return new instance
     */
    static MutableInt fromValue(int value) {
        return new ValueMutableInt(value);
    }

    /**
     * Creates a new instance backed by a consumer and supplier.
     *
     * @param consumer value consumer
     * @param supplier value supplier
     * @return new instance
     */
    static MutableInt fromEvent(IntConsumer consumer, IntSupplier supplier) {
        return new EventMutableInt(consumer, supplier);
    }

    /**
     * Maps contained value to something new.
     *
     * @param operator operator to apply to contained value
     */
    default void mapAsInt(IntUnaryOperator operator) {
        this.accept(operator.applyAsInt(this.getAsInt()));
    }
}
