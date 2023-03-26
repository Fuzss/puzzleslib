package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedBoolean;

public class ValueDefaultedBoolean extends ValueMutableBoolean implements DefaultedBoolean {
    private final boolean defaultValue;

    public ValueDefaultedBoolean(boolean value) {
        super(value);
        this.defaultValue = value;
    }

    @Override
    public boolean getAsDefaultBoolean() {
        return this.defaultValue;
    }
}
