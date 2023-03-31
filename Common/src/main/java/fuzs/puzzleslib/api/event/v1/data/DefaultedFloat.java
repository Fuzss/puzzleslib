package fuzs.puzzleslib.api.event.v1.data;

import fuzs.puzzleslib.impl.event.data.EventDefaultedFloat;
import fuzs.puzzleslib.impl.event.data.ValueDefaultedFloat;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * A mutable float implementation with a default value useful for events.
 */
public interface DefaultedFloat extends MutableFloat {

    /**
     * Creates a new instance from an initial value.
     *
     * @param value initial value
     * @return new instance
     */
    static DefaultedFloat fromValue(float value) {
        return new ValueDefaultedFloat(value);
    }

    /**
     * Creates a new instance backed by a consumer and supplier.
     *
     * @param consumer value consumer
     * @param supplier value supplier
     * @param defaultSupplier default value supplier
     * @return new instance
     */
    static DefaultedFloat fromEvent(Consumer<Float> consumer, Supplier<Float> supplier, Supplier<Float> defaultSupplier) {
        return new EventDefaultedFloat(consumer, supplier, defaultSupplier);
    }

    /**
     * A getter for the default value.
     *
     * @return default value
     */
    float getAsDefaultFloat();

    /**
     * A flag keeping track if the mutable value has changed at least once from calling {@link #accept}.
     *
     * @return has the value changed at least once
     */
    boolean markedDirty();

    /**
     * An optional getter for the contained value which will return empty if the value has not changed from the default value (determined via reference comparison).
     *
     * @return container value as optional
     */
    default Optional<Float> getAsOptionalFloat() {
        return this.markedDirty() ? Optional.of(this.getAsFloat()) : Optional.empty();
    }

    /**
     * Applies to default value to this instance.
     */
    default void applyDefaultFloat() {
        this.accept(this.getAsDefaultFloat());
    }

    /**
     * Sets the default value, then maps that value to something new.
     *
     * @param operator operator to apply to default value
     */
    default void mapDefaultFloat(UnaryOperator<Float> operator) {
        this.applyDefaultFloat();
        this.mapFloat(operator);
    }
}
