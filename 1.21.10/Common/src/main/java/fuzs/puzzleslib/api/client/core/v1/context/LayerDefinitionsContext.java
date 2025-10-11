package fuzs.puzzleslib.api.client.core.v1.context;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;

import java.util.function.Supplier;

/**
 * Register layer definitions for entity models.
 */
@FunctionalInterface
public interface LayerDefinitionsContext {

    /**
     * @param modelLayer    the model layer location
     * @param layerSupplier the layer definition supplier
     */
    void registerLayerDefinition(ModelLayerLocation modelLayer, Supplier<LayerDefinition> layerSupplier);

    /**
     * TODO rename to proper method with V2
     *
     * @param modelLayerSet    the model layer location set
     * @param layerSupplierSet the layer definition supplier set
     * @see ArmorModelSet#putFrom(ArmorModelSet, ImmutableMap.Builder)
     */
    default void registerArmorDefinitionV2(ArmorModelSet<ModelLayerLocation> modelLayerSet, ArmorModelSet<Supplier<LayerDefinition>> layerSupplierSet) {
        this.registerLayerDefinition(modelLayerSet.head(), layerSupplierSet.head());
        this.registerLayerDefinition(modelLayerSet.chest(), layerSupplierSet.chest());
        this.registerLayerDefinition(modelLayerSet.legs(), layerSupplierSet.legs());
        this.registerLayerDefinition(modelLayerSet.feet(), layerSupplierSet.feet());
    }

    /**
     * @param modelLayerSet      the model layer location set
     * @param layerDefinitionSet the layer definitions
     * @see ArmorModelSet#putFrom(ArmorModelSet, ImmutableMap.Builder)
     */
    @Deprecated(forRemoval = true)
    default void registerArmorDefinition(ArmorModelSet<ModelLayerLocation> modelLayerSet, ArmorModelSet<LayerDefinition> layerDefinitionSet) {
        this.registerArmorDefinitionV2(modelLayerSet, layerDefinitionSet.map((LayerDefinition layerDefinition) -> {
            return () -> layerDefinition;
        }));
    }
}
