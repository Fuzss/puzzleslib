package fuzs.puzzleslib.impl.event.data.value;

import fuzs.puzzleslib.api.event.v1.data.MutableDouble;

public class ValueMutableDouble implements MutableDouble {
    private double value;

    public ValueMutableDouble(double value) {
        this.value = value;
    }

    @Override
    public void accept(double value) {
        this.value = value;
    }

    @Override
    public double getAsDouble() {
        return this.value;
    }

    @Override
    public String toString() {
        return "MutableDouble[" + this.getAsDouble() + "]";
    }
}
