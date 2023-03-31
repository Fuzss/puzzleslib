package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;

public class ValueDefaultedInt extends ValueMutableInt implements DefaultedInt {
    private final int defaultValue;
    private boolean dirty;

    public ValueDefaultedInt(int value) {
        super(value);
        this.defaultValue = value;
    }

    @Override
    public void accept(int value) {
        this.dirty = true;
        super.accept(value);
    }

    @Override
    public int getAsDefaultInt() {
        return this.defaultValue;
    }

    @Override
    public boolean markedDirty() {
        return this.dirty;
    }
}
