package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.MutableFloat;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventMutableFloat implements MutableFloat {
    private final Consumer<Float> consumer;
    private final Supplier<Float> supplier;

    public EventMutableFloat(Consumer<Float> consumer, Supplier<Float> supplier) {
        this.consumer = consumer;
        this.supplier = supplier;
    }

    @Override
    public void accept(float value) {
        this.consumer.accept(value);
    }

    @Override
    public float getAsFloat() {
        return this.supplier.get();
    }

    @Override
    public String toString() {
        return "MutableFloat[" + this.getAsFloat() + "]";
    }
}
