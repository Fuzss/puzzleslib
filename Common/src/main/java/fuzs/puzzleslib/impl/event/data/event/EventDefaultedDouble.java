package fuzs.puzzleslib.impl.event.data.event;

import fuzs.puzzleslib.impl.event.data.DefaultedDouble;

import java.util.OptionalDouble;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class EventDefaultedDouble extends EventMutableDouble implements DefaultedDouble {
    private final DoubleSupplier defaultSupplier;
    private boolean dirty;

    public EventDefaultedDouble(DoubleConsumer consumer, DoubleSupplier supplier, DoubleSupplier defaultSupplier) {
        super(consumer, supplier);
        this.defaultSupplier = defaultSupplier;
    }

    @Override
    public void accept(double value) {
        this.dirty = true;
        super.accept(value);
    }

    @Override
    public double getAsDefaultDouble() {
        return this.defaultSupplier.getAsDouble();
    }

    @Override
    public OptionalDouble getAsOptionalDouble() {
        return this.dirty ? OptionalDouble.of(this.getAsDouble()) : OptionalDouble.empty();
    }
}
