package fuzs.puzzleslib.impl.event.core;

import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public record EventPhaseImpl(ResourceLocation identifier, EventPhase parent, Ordering ordering) implements EventPhase {

    public void applyOrdering(BiConsumer<ResourceLocation, ResourceLocation> consumer) {
        this.ordering().apply(consumer, this.identifier(), this.parent().identifier());
    }

    public interface Ordering {
        Ordering BEFORE = (consumer, first, second) -> {
            consumer.accept(first, second);
        };
        Ordering AFTER = (consumer, first, second) -> {
            consumer.accept(second, first);
        };

        void apply(BiConsumer<ResourceLocation, ResourceLocation> consumer, ResourceLocation first, ResourceLocation second);
    }
}
