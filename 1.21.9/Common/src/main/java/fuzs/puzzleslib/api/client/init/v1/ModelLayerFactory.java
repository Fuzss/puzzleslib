package fuzs.puzzleslib.api.client.init.v1;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * A helper class for creating {@link ModelLayerLocation ModelLayerLocations} with a provided namespace.
 * <p>
 * Most methods are copied from {@link net.minecraft.client.model.geom.ModelLayers} to allow for usage with a custom
 * namespace.
 */
@FunctionalInterface
public interface ModelLayerFactory {

    /**
     * Creates a new instance from a mod id.
     *
     * @param modId the model layer location namespace
     * @return the new factory
     */
    static ModelLayerFactory from(String modId) {
        return () -> modId;
    }

    /**
     * @return the mod id
     */
    String modId();

    /**
     * Creates a new {@link ModelLayerLocation}.
     *
     * @param path  the entity name
     * @param layer the layer name
     * @return the new model layer location
     */
    default ModelLayerLocation registerModelLayer(String path, String layer) {
        ModelLayerLocation modelLayerLocation = new ModelLayerLocation(ResourceLocationHelper.fromNamespaceAndPath(this.modId(),
                path), layer);
        if (!ModelLayers.ALL_MODELS.add(modelLayerLocation)) {
            throw new IllegalStateException("Duplicate registration for " + modelLayerLocation);
        } else {
            return modelLayerLocation;
        }
    }

    /**
     * Creates a new {@link ModelLayerLocation}; the used layer is {@code main}.
     *
     * @param path the entity name
     * @return the new model layer location
     */
    default ModelLayerLocation registerModelLayer(String path) {
        return this.registerModelLayer(path, "main");
    }

    /**
     * Creates a new {@link ModelLayerLocation}; the used layer is {@code inner_armor}.
     *
     * @param path the entity name
     * @return the new model layer location
     */
    @Deprecated(forRemoval = true)
    default ModelLayerLocation registerInnerArmorModelLayer(String path) {
        return this.registerModelLayer(path, "inner_armor");
    }

    /**
     * Creates a new {@link ModelLayerLocation}; the used layer is {@code outer_armor}.
     *
     * @param path the entity name
     * @return the new model layer location
     */
    @Deprecated(forRemoval = true)
    default ModelLayerLocation registerOuterArmorModelLayer(String path) {
        return this.registerModelLayer(path, "outer_armor");
    }

    /**
     * Creates a new {@link ModelLayerLocation}; the used layer is {@code main}.
     *
     * @param path the entity name
     * @return the new model layer location
     */
    default ArmorModelSet<ModelLayerLocation> registerArmorSet(String path) {
        return new ArmorModelSet<>(this.registerModelLayer(path, "helmet"),
                this.registerModelLayer(path, "chestplate"),
                this.registerModelLayer(path, "leggings"),
                this.registerModelLayer(path, "boots"));
    }

    /**
     * Creates a new {@link ModelLayerLocation} for a standing sign; the used layer is {@code main}.
     *
     * @param woodType the wood type
     * @return the new model layer location
     */
    default ModelLayerLocation createStandingSignModelName(WoodType woodType) {
        return this.registerModelLayer("sign/standing/" + woodType.name());
    }

    /**
     * Creates a new {@link ModelLayerLocation} for a wall sign; the used layer is {@code main}.
     *
     * @param woodType the wood type
     * @return the new model layer location
     */
    default ModelLayerLocation createWallSignModelName(WoodType woodType) {
        return this.registerModelLayer("sign/wall/" + woodType.name());
    }

    /**
     * Creates a new {@link ModelLayerLocation} for a hanging sign; the used layer is {@code main}.
     *
     * @param woodType       the wood type
     * @param attachmentType the attachment type
     * @return the new model layer location
     */
    default ModelLayerLocation createHangingSignModelName(WoodType woodType, HangingSignRenderer.AttachmentType attachmentType) {
        return this.registerModelLayer("hanging_sign/" + woodType.name() + "/" + attachmentType.getSerializedName());
    }
}
