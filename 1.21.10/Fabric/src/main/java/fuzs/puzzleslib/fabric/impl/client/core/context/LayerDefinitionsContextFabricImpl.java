package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.LayerDefinitionsContext;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.Objects;
import java.util.function.Supplier;

public final class LayerDefinitionsContextFabricImpl implements LayerDefinitionsContext {

    @Override
    public void registerLayerDefinition(ModelLayerLocation modelLayer, Supplier<LayerDefinition> layerDefinitionSupplier) {
        Objects.requireNonNull(modelLayer, "layer location is null");
        Objects.requireNonNull(layerDefinitionSupplier, "layer supplier is null");
        EntityModelLayerRegistry.registerModelLayer(modelLayer, layerDefinitionSupplier::get);
    }
}
