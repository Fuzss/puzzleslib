package fuzs.puzzleslib.impl.event.data.event;

import fuzs.puzzleslib.impl.event.data.DefaultedInt;

import java.util.OptionalInt;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class EventDefaultedInt extends EventMutableInt implements DefaultedInt {
    private final IntSupplier defaultSupplier;
    private boolean dirty;

    public EventDefaultedInt(IntConsumer consumer, IntSupplier supplier, IntSupplier defaultSupplier) {
        super(consumer, supplier);
        this.defaultSupplier = defaultSupplier;
    }

    @Override
    public void accept(int value) {
        this.dirty = true;
        super.accept(value);
    }

    @Override
    public int getAsDefaultInt() {
        return this.defaultSupplier.getAsInt();
    }

    @Override
    public OptionalInt getAsOptionalInt() {
        return this.dirty ? OptionalInt.of(this.getAsInt()) : OptionalInt.empty();
    }
}
