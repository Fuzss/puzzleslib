package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;

public class ValueDefaultedFloat extends ValueMutableFloat implements DefaultedFloat {
    private final float defaultValue;

    public ValueDefaultedFloat(float value) {
        super(value);
        this.defaultValue = value;
    }

    @Override
    public float getAsDefaultFloat() {
        return this.defaultValue;
    }
}
