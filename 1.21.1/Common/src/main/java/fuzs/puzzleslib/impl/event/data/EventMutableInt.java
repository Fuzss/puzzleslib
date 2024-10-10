package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.MutableInt;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class EventMutableInt implements MutableInt {
    private final IntConsumer consumer;
    private final IntSupplier supplier;

    public EventMutableInt(IntConsumer consumer, IntSupplier supplier) {
        this.consumer = consumer;
        this.supplier = supplier;
    }

    @Override
    public void accept(int value) {
        this.consumer.accept(value);
    }

    @Override
    public int getAsInt() {
        return this.supplier.getAsInt();
    }

    @Override
    public String toString() {
        return "MutableInt[" + this.getAsInt() + "]";
    }
}
