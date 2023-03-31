package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;

public class ValueDefaultedValue<T> extends ValueMutableValue<T> implements DefaultedValue<T> {
    private final T defaultValue;
    private boolean dirty;

    public ValueDefaultedValue(T value) {
        super(value);
        this.defaultValue = value;
    }

    @Override
    public void accept(T value) {
        this.dirty = true;
        super.accept(value);
    }

    @Override
    public T getAsDefault() {
        return this.defaultValue;
    }

    @Override
    public boolean markedDirty() {
        return this.dirty;
    }
}
