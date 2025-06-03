package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.MutableInt;

public class ValueMutableInt implements MutableInt {
    private int value;

    public ValueMutableInt(int value) {
        this.value = value;
    }

    @Override
    public void accept(int value) {
        this.value = value;
    }

    @Override
    public int getAsInt() {
        return this.value;
    }

    @Override
    public String toString() {
        return "MutableInt[" + this.getAsInt() + "]";
    }
}
