package fuzs.puzzleslib.impl.event.data.value;

import fuzs.puzzleslib.impl.event.data.DefaultedValue;

import java.util.Optional;

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
    public Optional<T> getAsOptional() {
        return this.dirty ? Optional.ofNullable(this.get()) : Optional.empty();
    }
}
