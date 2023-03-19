package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventDefaultedFloat extends EventMutableFloat implements DefaultedFloat {
    private final Supplier<Float> defaultSupplier;

    public EventDefaultedFloat(Consumer<Float> consumer, Supplier<Float> supplier, Supplier<Float> defaultSupplier) {
        super(consumer, supplier);
        this.defaultSupplier = defaultSupplier;
    }

    @Override
    public float getAsDefaultFloat() {
        return this.defaultSupplier.get();
    }
}
