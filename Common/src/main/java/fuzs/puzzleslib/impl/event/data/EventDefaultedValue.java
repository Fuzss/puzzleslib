package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventDefaultedValue<T> extends EventMutableValue<T> implements DefaultedValue<T> {
    private final Supplier<T> defaultSupplier;
    private boolean dirty;

    public EventDefaultedValue(Consumer<T> consumer, Supplier<T> supplier, Supplier<T> defaultSupplier) {
        super(consumer, supplier);
        this.defaultSupplier = defaultSupplier;
    }

    @Override
    public void accept(T value) {
        this.dirty = true;
        super.accept(value);
    }

    @Override
    public T getAsDefault() {
        return this.defaultSupplier.get();
    }

    @Override
    public boolean markedDirty() {
        return this.dirty;
    }
}
