package fuzs.puzzleslib.api.client.core.v1.contexts;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.Supplier;

/**
 * register layer definitions for entity models
 */
@FunctionalInterface
public interface LayerDefinitionsContext {

    /**
     * registers a new layer definition (used for entity model parts)
     *
     * @param layerLocation model location
     * @param supplier      layer definition supplier
     */
    void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier);
}
