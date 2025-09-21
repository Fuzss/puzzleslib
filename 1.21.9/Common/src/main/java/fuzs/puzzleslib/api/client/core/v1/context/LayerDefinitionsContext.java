package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.Supplier;

/**
 * Register layer definitions for entity models.
 */
@FunctionalInterface
public interface LayerDefinitionsContext {

    /**
     * @param modelLayerLocation      the model layer location
     * @param layerDefinitionSupplier the layer definition supplier
     */
    void registerLayerDefinition(ModelLayerLocation modelLayerLocation, Supplier<LayerDefinition> layerDefinitionSupplier);
}
