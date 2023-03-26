package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventMutableBoolean implements MutableBoolean {
    private final Consumer<Boolean> consumer;
    private final Supplier<Boolean> supplier;

    public EventMutableBoolean(Consumer<Boolean> consumer, Supplier<Boolean> supplier) {
        this.consumer = consumer;
        this.supplier = supplier;
    }

    @Override
    public void accept(boolean value) {
        this.consumer.accept(value);
    }

    @Override
    public boolean getAsBoolean() {
        return this.supplier.get();
    }
}
