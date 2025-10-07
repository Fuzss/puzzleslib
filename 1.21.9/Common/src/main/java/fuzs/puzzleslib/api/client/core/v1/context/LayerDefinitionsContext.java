package fuzs.puzzleslib.api.client.core.v1.context;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;

import java.util.function.Supplier;

/**
 * Register layer definitions for entity models.
 * <p>
 * TODO make this not take a supplier, it's pointless
 */
@FunctionalInterface
public interface LayerDefinitionsContext {

    /**
     * @param modelLayer              the model layer location
     * @param layerDefinitionSupplier the layer definition supplier
     */
    void registerLayerDefinition(ModelLayerLocation modelLayer, Supplier<LayerDefinition> layerDefinitionSupplier);

    /**
     * @param modelLayerSet      the model layer locations
     * @param layerDefinitionSet the layer definitions
     * @see ArmorModelSet#putFrom(ArmorModelSet, ImmutableMap.Builder)
     */
    default void registerArmorDefinition(ArmorModelSet<ModelLayerLocation> modelLayerSet, ArmorModelSet<LayerDefinition> layerDefinitionSet) {
        this.registerLayerDefinition(modelLayerSet.head(), layerDefinitionSet::head);
        this.registerLayerDefinition(modelLayerSet.chest(), layerDefinitionSet::chest);
        this.registerLayerDefinition(modelLayerSet.legs(), layerDefinitionSet::legs);
        this.registerLayerDefinition(modelLayerSet.feet(), layerDefinitionSet::feet);
    }
}
