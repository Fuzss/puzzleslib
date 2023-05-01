package fuzs.puzzleslib.impl.event.data;

import fuzs.puzzleslib.api.event.v1.data.DefaultedBoolean;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventDefaultedBoolean extends EventMutableBoolean implements DefaultedBoolean {
    private final Supplier<Boolean> defaultSupplier;
    private boolean dirty;

    public EventDefaultedBoolean(Consumer<Boolean> consumer, Supplier<Boolean> supplier, Supplier<Boolean> defaultSupplier) {
        super(consumer, supplier);
        this.defaultSupplier = defaultSupplier;
    }

    @Override
    public void accept(boolean value) {
        this.dirty = true;
        super.accept(value);
    }

    @Override
    public boolean getAsDefaultBoolean() {
        return this.defaultSupplier.get();
    }

    @Override
    public Optional<Boolean> getAsOptionalBoolean() {
        return this.dirty ? Optional.of(this.getAsBoolean()) : Optional.empty();
    }
}
