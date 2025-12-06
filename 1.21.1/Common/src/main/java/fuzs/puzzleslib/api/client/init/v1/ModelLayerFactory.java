package fuzs.puzzleslib.api.client.init.v1;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * A helper class for creating {@link ModelLayerLocation ModelLayerLocations} with a provided namespace.
 */
@FunctionalInterface
public interface ModelLayerFactory {

    /**
     * Creates a new instance from a mod id.
     *
     * @param modId model layer location namespace
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
    @Deprecated
    default ModelLayerLocation register(String path, String layer) {
        return this.registerModelLayer(path, layer);
    }

    /**
     * Creates a new {@link ModelLayerLocation}, the used layer is <code>main</code>.
     *
     * @param path the entity name
     * @return the new model layer location
     */
    @Deprecated
    default ModelLayerLocation register(String path) {
        return this.registerModelLayer(path);
    }

    /**
     * Creates a new {@link ModelLayerLocation}, the used layer is <code>inner_armor</code>.
     *
     * @param path the entity name
     * @return the new model layer location
     */
    @Deprecated
    default ModelLayerLocation registerInnerArmor(String path) {
        return this.registerModelLayer(path, "inner_armor");
    }

    /**
     * Creates a new {@link ModelLayerLocation}, the used layer is <code>outer_armor</code>.
     *
     * @param path the entity name
     * @return the new model layer location
     */
    @Deprecated
    default ModelLayerLocation registerOuterArmor(String path) {
        return this.registerModelLayer(path, "outer_armor");
    }

    /**
     * Creates a new {@link ModelLayerLocation}.
     *
     * @param path  the entity name
     * @param layer the layer name
     * @return the new model layer location
     */
    default ModelLayerLocation registerModelLayer(String path, String layer) {
        ModelLayerLocation modelLayerLocation = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(this.modId(),
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
     * Creates a new {@link ModelLayerLocation} for a raft; the used layer is {@code main}.
     *
     * @param boatType the boat type
     * @return the new model layer location
     */
    default ModelLayerLocation createRaftModelName(Boat.Type boatType) {
        return this.registerModelLayer("raft/" + boatType.getName());
    }

    /**
     * Creates a new {@link ModelLayerLocation} for a chest raft; the used layer is {@code main}.
     *
     * @param boatType the boat type
     * @return the new model layer location
     */
    default ModelLayerLocation createChestRaftModelName(Boat.Type boatType) {
        return this.registerModelLayer("chest_raft/" + boatType.getName());
    }

    /**
     * Creates a new {@link ModelLayerLocation} for a boat; the used layer is {@code main}.
     *
     * @param boatType the boat type
     * @return the new model layer location
     */
    default ModelLayerLocation createBoatModelName(Boat.Type boatType) {
        return this.registerModelLayer("boat/" + boatType.getName());
    }

    /**
     * Creates a new {@link ModelLayerLocation} for a chest boat; the used layer is {@code main}.
     *
     * @param boatType the boat type
     * @return the new model layer location
     */
    default ModelLayerLocation createChestBoatModelName(Boat.Type boatType) {
        return this.registerModelLayer("chest_boat/" + boatType.getName());
    }

    /**
     * Creates a new {@link ModelLayerLocation} for a sign; the used layer is {@code main}.
     *
     * @param woodType the wood type
     * @return the new model layer location
     */
    default ModelLayerLocation createSignModelName(WoodType woodType) {
        return this.registerModelLayer("sign/" + woodType.name());
    }

    /**
     * Creates a new {@link ModelLayerLocation} for a hanging sign; the used layer is {@code main}.
     *
     * @param woodType the wood type
     * @return the new model layer location
     */
    default ModelLayerLocation createHangingSignModelName(WoodType woodType) {
        return this.registerModelLayer("hanging_sign/" + woodType.name());
    }
}
