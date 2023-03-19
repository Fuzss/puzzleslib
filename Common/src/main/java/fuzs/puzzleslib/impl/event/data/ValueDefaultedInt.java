package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;

public class ValueDefaultedInt extends ValueMutableInt implements DefaultedInt {
    private final int defaultValue;

    public ValueDefaultedInt(int value) {
        super(value);
        this.defaultValue = value;
    }

    @Override
    public int getAsDefaultInt() {
        return this.defaultValue;
    }
}
