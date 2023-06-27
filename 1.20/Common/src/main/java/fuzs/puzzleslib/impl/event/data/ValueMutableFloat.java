package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.MutableFloat;

public class ValueMutableFloat implements MutableFloat {
    private float value;

    public ValueMutableFloat(float value) {
        this.value = value;
    }

    @Override
    public void accept(float value) {
        this.value = value;
    }

    @Override
    public float getAsFloat() {
        return this.value;
    }
}
