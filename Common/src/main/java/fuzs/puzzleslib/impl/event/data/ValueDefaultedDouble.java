package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;

public class ValueDefaultedDouble extends ValueMutableDouble implements DefaultedDouble {
    private final double defaultValue;

    public ValueDefaultedDouble(double value) {
        super(value);
        this.defaultValue = value;
    }

    @Override
    public double getAsDefaultDouble() {
        return this.defaultValue;
    }
}
