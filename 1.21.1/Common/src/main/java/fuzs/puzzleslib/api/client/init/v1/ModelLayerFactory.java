package fuzs.puzzleslib.api.client.init.v1;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;

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
    default ModelLayerLocation register(String path, String layer) {
        return new ModelLayerLocation(ResourceLocationHelper.fromNamespaceAndPath(this.modId(), path), layer);
    }

    /**
     * Creates a new {@link ModelLayerLocation}, the used layer is <code>main</code>.
     *
     * @param path the entity name
     * @return the new model layer location
     */
    default ModelLayerLocation register(String path) {
        return this.register(path, "main");
    }

    /**
     * Creates a new {@link ModelLayerLocation}, the used layer is <code>inner_armor</code>.
     *
     * @param path the entity name
     * @return the new model layer location
     */
    default ModelLayerLocation registerInnerArmor(String path) {
        return this.register(path, "inner_armor");
    }

    /**
     * Creates a new {@link ModelLayerLocation}, the used layer is <code>outer_armor</code>.
     *
     * @param path the entity name
     * @return the new model layer location
     */
    default ModelLayerLocation registerOuterArmor(String path) {
        return this.register(path, "outer_armor");
    }
}
