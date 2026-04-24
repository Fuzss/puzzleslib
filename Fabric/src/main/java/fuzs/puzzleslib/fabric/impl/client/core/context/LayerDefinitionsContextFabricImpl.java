package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.common.api.client.core.v1.context.LayerDefinitionsContext;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.Objects;
import java.util.function.Supplier;

public final class LayerDefinitionsContextFabricImpl implements LayerDefinitionsContext {

    @Override
    public void registerLayerDefinition(ModelLayerLocation modelLayer, Supplier<LayerDefinition> layerSupplier) {
        Objects.requireNonNull(modelLayer, "layer location is null");
        Objects.requireNonNull(layerSupplier, "layer supplier is null");
        ModelLayerRegistry.registerModelLayer(modelLayer, layerSupplier::get);
    }
}
