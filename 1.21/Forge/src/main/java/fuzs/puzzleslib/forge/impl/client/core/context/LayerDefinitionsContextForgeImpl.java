package fuzs.puzzleslib.forge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.LayerDefinitionsContext;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public record LayerDefinitionsContextForgeImpl(
        BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) implements LayerDefinitionsContext {

    @Override
    public void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
        Objects.requireNonNull(layerLocation, "layer location is null");
        Objects.requireNonNull(supplier, "layer supplier is null");
        this.consumer.accept(layerLocation, supplier);
    }
}
