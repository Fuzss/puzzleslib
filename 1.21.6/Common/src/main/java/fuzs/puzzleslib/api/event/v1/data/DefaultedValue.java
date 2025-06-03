package fuzs.puzzleslib.api.event.v1.data;

import fuzs.puzzleslib.impl.event.data.EventDefaultedValue;
import fuzs.puzzleslib.impl.event.data.ValueDefaultedValue;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * A mutable value implementation with a default value useful for events.
 */
public interface DefaultedValue<T> extends MutableValue<T> {

    /**
     * Creates a new instance from an initial value.
     *
     * @param value initial value
     * @return new instance
     */
    static <T> DefaultedValue<T> fromValue(T value) {
        return new ValueDefaultedValue<>(value);
    }

    /**
     * Creates a new instance backed by a consumer and supplier.
     *
     * @param consumer        value consumer
     * @param supplier        value supplier
     * @param defaultSupplier default value supplier
     * @return new instance
     */
    static <T> DefaultedValue<T> fromEvent(Consumer<T> consumer, Supplier<T> supplier, Supplier<T> defaultSupplier) {
        return new EventDefaultedValue<>(consumer, supplier, defaultSupplier);
    }

    /**
     * Creates a new instance backed by a consumer and supplier.
     *
     * @param consumer     value consumer
     * @param supplier     value supplier
     * @param defaultValue default value
     * @return new instance
     */
    static <T> DefaultedValue<T> fromEventWithValue(Consumer<T> consumer, Supplier<T> supplier, T defaultValue) {
        return fromEvent(consumer, supplier, (Supplier<T>) () -> defaultValue);
    }

    /**
     * A getter for the default value.
     *
     * @return default value
     */
    T getAsDefault();

    /**
     * An optional getter for the contained value which will return empty if the value has not changed from the default
     * value (determined via reference comparison).
     *
     * @return container value as optional
     */
    Optional<T> getAsOptional();

    /**
     * Applies to default value to this instance.
     */
    default void applyDefault() {
        this.accept(this.getAsDefault());
    }

    /**
     * Sets the default value, then maps that value to something new.
     *
     * @param operator operator to apply to default value
     */
    default void mapDefault(UnaryOperator<T> operator) {
        this.applyDefault();
        this.map(operator);
    }
}
