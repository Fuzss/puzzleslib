package fuzs.puzzleslib.impl.event.core;

import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.BiConsumer;

public record EventPhaseImpl(ResourceLocation resourceLocation,
                             EventPhase parent,
                             EventPhaseImpl.Ordering ordering) implements EventPhase {

    @Override
    public void applyOrdering(ResourceLocation resourceLocation, BiConsumer<ResourceLocation, ResourceLocation> phaseOrderingConsumer) {
        Objects.requireNonNull(this.parent, "parent is null");
        Objects.requireNonNull(this.ordering, "ordering is null");
        this.ordering.apply(phaseOrderingConsumer, resourceLocation, this.parent.resourceLocation());
    }

    @Override
    public int getOrderingValue() {
        Objects.requireNonNull(this.ordering, "ordering is null");
        return this.ordering.value;
    }

    public enum Ordering {
        BEFORE(-1) {
            @Override
            public void apply(BiConsumer<ResourceLocation, ResourceLocation> consumer, ResourceLocation first, ResourceLocation second) {
                consumer.accept(first, second);
            }
        },
        AFTER(1) {
            @Override
            public void apply(BiConsumer<ResourceLocation, ResourceLocation> consumer, ResourceLocation first, ResourceLocation second) {
                consumer.accept(second, first);
            }
        };

        public final int value;

        Ordering(int value) {
            this.value = value;
        }

        public abstract void apply(BiConsumer<ResourceLocation, ResourceLocation> consumer, ResourceLocation first, ResourceLocation second);
    }
}
