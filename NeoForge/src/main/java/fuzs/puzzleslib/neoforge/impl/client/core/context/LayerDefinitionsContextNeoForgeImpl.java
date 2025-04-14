package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.LayerDefinitionsContext;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.Objects;
import java.util.function.Supplier;

public record LayerDefinitionsContextNeoForgeImpl(EntityRenderersEvent.RegisterLayerDefinitions evt) implements LayerDefinitionsContext {

    @Override
    public void registerLayerDefinition(ModelLayerLocation modelLayerLocation, Supplier<LayerDefinition> layerDefinitionSupplier) {
        Objects.requireNonNull(modelLayerLocation, "layer location is null");
        Objects.requireNonNull(layerDefinitionSupplier, "layer supplier is null");
        this.evt.registerLayerDefinition(modelLayerLocation, layerDefinitionSupplier);
    }
}
