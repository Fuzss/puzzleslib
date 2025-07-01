package fuzs.puzzleslib.impl.event.data.value;

import fuzs.puzzleslib.impl.event.data.DefaultedBoolean;

import java.util.Optional;

public class ValueDefaultedBoolean extends ValueMutableBoolean implements DefaultedBoolean {
    private final boolean defaultValue;
    private boolean dirty;

    public ValueDefaultedBoolean(boolean value) {
        super(value);
        this.defaultValue = value;
    }

    @Override
    public void accept(boolean value) {
        this.dirty = true;
        super.accept(value);
    }

    @Override
    public boolean getAsDefaultBoolean() {
        return this.defaultValue;
    }

    @Override
    public Optional<Boolean> getAsOptionalBoolean() {
        return this.dirty ? Optional.of(this.getAsBoolean()) : Optional.empty();
    }
}
