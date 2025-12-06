package fuzs.puzzleslib.api.client.core.v1.context;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * Register layer definitions for entity models.
 */
public interface LayerDefinitionsContext {

    /**
     * @param modelLayer    the model layer location
     * @param layerSupplier the layer definition supplier
     */
    void registerLayerDefinition(ModelLayerLocation modelLayer, Supplier<LayerDefinition> layerSupplier);

    /**
     * @param modelLayerSet    the model layer location set
     * @param layerSupplierSet the layer definition supplier set
     * @see ArmorModelSet#putFrom(ArmorModelSet, ImmutableMap.Builder)
     */
    @Deprecated(forRemoval = true)
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

    /**
     * @param modelLayerSet    the model layer location set
     * @param layerSetSupplier the layer definition set supplier
     * @see ArmorModelSet#putFrom(ArmorModelSet, ImmutableMap.Builder)
     */
    default void registerArmorDefinition(ArmorModelSet<ModelLayerLocation> modelLayerSet, Supplier<ArmorModelSet<LayerDefinition>> layerSetSupplier) {
        ArmorModelSet<MutableObject<LayerDefinition>> mutableLayerSet = new ArmorModelSet<>(new MutableObject<>(),
                new MutableObject<>(),
                new MutableObject<>(),
                new MutableObject<>());
        this.registerArmorDefinition(modelLayerSet,
                mutableLayerSet,
                layerSetSupplier,
                ArmorModelSet::head,
                ArmorModelSet::chest,
                ArmorModelSet::legs,
                ArmorModelSet::feet);
        this.registerArmorDefinition(modelLayerSet,
                mutableLayerSet,
                layerSetSupplier,
                ArmorModelSet::chest,
                ArmorModelSet::head,
                ArmorModelSet::legs,
                ArmorModelSet::feet);
        this.registerArmorDefinition(modelLayerSet,
                mutableLayerSet,
                layerSetSupplier,
                ArmorModelSet::legs,
                ArmorModelSet::head,
                ArmorModelSet::chest,
                ArmorModelSet::feet);
        this.registerArmorDefinition(modelLayerSet,
                mutableLayerSet,
                layerSetSupplier,
                ArmorModelSet::feet,
                ArmorModelSet::head,
                ArmorModelSet::chest,
                ArmorModelSet::legs);
    }

    private void registerArmorDefinition(ArmorModelSet<ModelLayerLocation> modelLayerSet, ArmorModelSet<MutableObject<LayerDefinition>> mutableLayerSet, Supplier<ArmorModelSet<LayerDefinition>> layerSetSupplier, ArmorModelSetGetter primaryGetter, ArmorModelSetGetter secondaryGetter, ArmorModelSetGetter tertiaryGetter, ArmorModelSetGetter quaternaryGetter) {
        this.registerLayerDefinition(primaryGetter.apply(modelLayerSet), () -> {
            return this.storeArmorModelLayers(mutableLayerSet,
                    layerSetSupplier,
                    primaryGetter,
                    secondaryGetter,
                    tertiaryGetter,
                    quaternaryGetter);
        });
    }

    /**
     * This implementation tries to create the armor models just once from the supplier, then stores all remaining three
     * models until they are used.
     * <p>
     * Since {@link LayerDefinition} registration is likely to run sequentially in order this should work to reduce
     * overhead and avoid recreating models unnecessarily.
     */
    private LayerDefinition storeArmorModelLayers(ArmorModelSet<MutableObject<LayerDefinition>> mutableLayerSet, Supplier<ArmorModelSet<LayerDefinition>> layerSetSupplier, ArmorModelSetGetter primaryGetter, ArmorModelSetGetter secondaryGetter, ArmorModelSetGetter tertiaryGetter, ArmorModelSetGetter quaternaryGetter) {
        LayerDefinition layerDefinition = primaryGetter.apply(mutableLayerSet).getValue();
        if (layerDefinition != null) {
            primaryGetter.apply(mutableLayerSet).setValue(null);
            return layerDefinition;
        } else {
            ArmorModelSet<LayerDefinition> armorModelSet = layerSetSupplier.get();
            secondaryGetter.apply(mutableLayerSet).setValue(secondaryGetter.apply(armorModelSet));
            tertiaryGetter.apply(mutableLayerSet).setValue(tertiaryGetter.apply(armorModelSet));
            quaternaryGetter.apply(mutableLayerSet).setValue(quaternaryGetter.apply(armorModelSet));
            return primaryGetter.apply(armorModelSet);
        }
    }

    @ApiStatus.Internal
    @FunctionalInterface
    interface ArmorModelSetGetter {
        <T> T apply(ArmorModelSet<T> armorModelSet);
    }
}
