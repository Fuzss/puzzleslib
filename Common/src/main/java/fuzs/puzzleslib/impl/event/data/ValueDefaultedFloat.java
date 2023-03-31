package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;

public class ValueDefaultedFloat extends ValueMutableFloat implements DefaultedFloat {
    private final float defaultValue;
    private boolean dirty;

    public ValueDefaultedFloat(float value) {
        super(value);
        this.defaultValue = value;
    }

    @Override
    public void accept(float value) {
        this.dirty = true;
        super.accept(value);
    }

    @Override
    public float getAsDefaultFloat() {
        return this.defaultValue;
    }

    @Override
    public boolean markedDirty() {
        return this.dirty;
    }
}
