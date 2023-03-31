package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedBoolean;

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
    public boolean markedDirty() {
        return this.dirty;
    }
}
