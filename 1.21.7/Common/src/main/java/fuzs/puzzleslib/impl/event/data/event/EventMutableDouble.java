package fuzs.puzzleslib.impl.event.data.event;

import fuzs.puzzleslib.api.event.v1.data.MutableDouble;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class EventMutableDouble implements MutableDouble {
    private final DoubleConsumer consumer;
    private final DoubleSupplier supplier;

    public EventMutableDouble(DoubleConsumer consumer, DoubleSupplier supplier) {
        this.consumer = consumer;
        this.supplier = supplier;
    }

    @Override
    public void accept(double value) {
        this.consumer.accept(value);
    }

    @Override
    public double getAsDouble() {
        return this.supplier.getAsDouble();
    }

    @Override
    public String toString() {
        return "MutableDouble[" + this.getAsDouble() + "]";
    }
}
