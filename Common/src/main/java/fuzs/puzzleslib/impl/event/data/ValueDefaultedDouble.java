package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;

import java.util.OptionalDouble;

public class ValueDefaultedDouble extends ValueMutableDouble implements DefaultedDouble {
    private final double defaultValue;
    private boolean dirty;

    public ValueDefaultedDouble(double value) {
        super(value);
        this.defaultValue = value;
    }

    @Override
    public void accept(double value) {
        this.dirty = true;
        super.accept(value);
    }

    @Override
    public double getAsDefaultDouble() {
        return this.defaultValue;
    }

    @Override
    public OptionalDouble getAsOptionalDouble() {
        return this.dirty ? OptionalDouble.of(this.getAsDouble()) : OptionalDouble.empty();
    }
}
