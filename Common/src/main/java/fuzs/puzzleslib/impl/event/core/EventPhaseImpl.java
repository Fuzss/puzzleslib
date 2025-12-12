package fuzs.puzzleslib.impl.event.core;

import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.function.BiConsumer;

public record EventPhaseImpl(Identifier identifier,
                             EventPhase parent,
                             EventPhaseImpl.Ordering ordering) implements EventPhase {

    @Override
    public void applyOrdering(Identifier identifier, BiConsumer<Identifier, Identifier> phaseOrderingConsumer) {
        Objects.requireNonNull(this.parent, "parent is null");
        Objects.requireNonNull(this.ordering, "ordering is null");
        this.ordering.apply(phaseOrderingConsumer, identifier, this.parent.identifier());
    }

    @Override
    public int getOrderingValue() {
        Objects.requireNonNull(this.ordering, "ordering is null");
        return this.ordering.value;
    }

    public enum Ordering {
        BEFORE(-1) {
            @Override
            public void apply(BiConsumer<Identifier, Identifier> consumer, Identifier first, Identifier second) {
                consumer.accept(first, second);
            }
        },
        AFTER(1) {
            @Override
            public void apply(BiConsumer<Identifier, Identifier> consumer, Identifier first, Identifier second) {
                consumer.accept(second, first);
            }
        };

        public final int value;

        Ordering(int value) {
            this.value = value;
        }

        public abstract void apply(BiConsumer<Identifier, Identifier> consumer, Identifier first, Identifier second);
    }
}
