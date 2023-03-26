package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedBoolean;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventDefaultedBoolean extends EventMutableBoolean implements DefaultedBoolean {
    private final Supplier<Boolean> defaultSupplier;

    public EventDefaultedBoolean(Consumer<Boolean> consumer, Supplier<Boolean> supplier, Supplier<Boolean> defaultSupplier) {
        super(consumer, supplier);
        this.defaultSupplier = defaultSupplier;
    }

    @Override
    public boolean getAsDefaultBoolean() {
        return this.defaultSupplier.get();
    }
}
