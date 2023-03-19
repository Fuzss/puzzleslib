package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventDefaultedValue<T> extends EventMutableValue<T> implements DefaultedValue<T> {
    private final Supplier<T> defaultSupplier;

    public EventDefaultedValue(Consumer<T> consumer, Supplier<T> supplier, Supplier<T> defaultSupplier) {
        super(consumer, supplier);
        this.defaultSupplier = defaultSupplier;
    }

    @Override
    public T getAsDefault() {
        return this.defaultSupplier.get();
    }
}
