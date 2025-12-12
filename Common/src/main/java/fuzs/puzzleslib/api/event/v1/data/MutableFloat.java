package fuzs.puzzleslib.api.event.v1.data;

import fuzs.puzzleslib.impl.event.data.event.EventMutableFloat;
import fuzs.puzzleslib.impl.event.data.value.ValueMutableFloat;

import java.util.function.*;

/**
 * A mutable float implementation useful for events.
 */
public interface MutableFloat {

    /**
     * Creates a new instance from an initial value.
     *
     * @param value initial value
     * @return new instance
     */
    static MutableFloat fromValue(float value) {
        return new ValueMutableFloat(value);
    }

    /**
     * Creates a new instance backed by a consumer and supplier.
     *
     * @param consumer value consumer
     * @param supplier value supplier
     * @return new instance
     */
    static MutableFloat fromEvent(Consumer<Float> consumer, Supplier<Float> supplier) {
        return new EventMutableFloat(consumer, supplier);
    }

    /**
     * A setter for the contained value.
     *
     * @param value new value to set
     */
    void accept(float value);

    /**
     * A getter for the contained value.
     *
     * @return contained value
     */
    float getAsFloat();

    /**
     * Maps contained value to something new.
     *
     * @param operator operator to apply to contained value
     */
    default void mapAsFloat(UnaryOperator<Float> operator) {
        this.accept(operator.apply(this.getAsFloat()));
    }
}
