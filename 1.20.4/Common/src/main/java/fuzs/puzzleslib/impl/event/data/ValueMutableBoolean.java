package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;

public class ValueMutableBoolean implements MutableBoolean {
    private boolean value;

    public ValueMutableBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public void accept(boolean value) {
        this.value = value;
    }

    @Override
    public boolean getAsBoolean() {
        return this.value;
    }
}
