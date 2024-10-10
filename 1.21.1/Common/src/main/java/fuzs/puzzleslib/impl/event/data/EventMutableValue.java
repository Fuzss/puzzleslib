package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.MutableValue;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventMutableValue<T> implements MutableValue<T> {
    private final Consumer<T> consumer;
    private final Supplier<T> supplier;

    public EventMutableValue(Consumer<T> consumer, Supplier<T> supplier) {
        this.consumer = consumer;
        this.supplier = supplier;
    }

    @Override
    public void accept(T value) {
        this.consumer.accept(value);
    }

    @Override
    public T get() {
        return this.supplier.get();
    }

    @Override
    public String toString() {
        return "MutableValue[" + this.get() + "]";
    }
}
