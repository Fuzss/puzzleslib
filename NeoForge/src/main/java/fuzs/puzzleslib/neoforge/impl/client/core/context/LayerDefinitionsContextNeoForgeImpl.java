package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.LayerDefinitionsContext;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.Objects;
import java.util.function.Supplier;

public record LayerDefinitionsContextNeoForgeImpl(EntityRenderersEvent.RegisterLayerDefinitions event) implements LayerDefinitionsContext {

    @Override
    public void registerLayerDefinition(ModelLayerLocation modelLayer, Supplier<LayerDefinition> layerSupplier) {
        Objects.requireNonNull(modelLayer, "layer location is null");
        Objects.requireNonNull(layerSupplier, "layer supplier is null");
        this.event.registerLayerDefinition(modelLayer, layerSupplier);
    }
}
