package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class EventDefaultedInt extends EventMutableInt implements DefaultedInt {
    private final IntSupplier defaultSupplier;

    public EventDefaultedInt(IntConsumer consumer, IntSupplier supplier, IntSupplier defaultSupplier) {
        super(consumer, supplier);
        this.defaultSupplier = defaultSupplier;
    }

    @Override
    public int getAsDefaultInt() {
        return this.defaultSupplier.getAsInt();
    }
}
