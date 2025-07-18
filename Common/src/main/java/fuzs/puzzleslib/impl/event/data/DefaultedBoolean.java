package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;
import fuzs.puzzleslib.impl.event.data.event.EventDefaultedBoolean;
import fuzs.puzzleslib.impl.event.data.value.ValueDefaultedBoolean;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A mutable boolean implementation with a default value useful for events.
 */
public interface DefaultedBoolean extends MutableBoolean {

    /**
     * Creates a new instance from an initial value.
     *
     * @param value initial value
     * @return new instance
     */
    static DefaultedBoolean fromValue(boolean value) {
        return new ValueDefaultedBoolean(value);
    }

    /**
     * Creates a new instance backed by a consumer and supplier.
     *
     * @param consumer        value consumer
     * @param supplier        value supplier
     * @param defaultSupplier default value supplier
     * @return new instance
     */
    static DefaultedBoolean fromEvent(Consumer<Boolean> consumer, Supplier<Boolean> supplier, Supplier<Boolean> defaultSupplier) {
        return new EventDefaultedBoolean(consumer, supplier, defaultSupplier);
    }

    /**
     * A getter for the default value.
     *
     * @return default value
     */
    boolean getAsDefaultBoolean();

    /**
     * An optional getter for the contained value which will return empty if the value has not changed from the default
     * value (determined via reference comparison).
     *
     * @return container value as optional
     */
    Optional<Boolean> getAsOptionalBoolean();
}
