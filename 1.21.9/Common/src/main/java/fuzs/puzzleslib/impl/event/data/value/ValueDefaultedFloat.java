package fuzs.puzzleslib.impl.event.data.value;

import fuzs.puzzleslib.impl.event.data.DefaultedFloat;

import java.util.Optional;

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
    public Optional<Float> getAsOptionalFloat() {
        return this.dirty ? Optional.of(this.getAsFloat()) : Optional.empty();
    }
}
