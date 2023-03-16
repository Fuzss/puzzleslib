package fuzs.puzzleslib.client.model.geom;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * helper class for creating {@link ModelLayerFactory} objects with a provided namespace
 */
public final class ModelLayerFactory {
    /**
     * namespace used for {@link ResourceLocation}
     */
    private final String namespace;

    /**
     * @param namespace registry namespace
     */
    private ModelLayerFactory(String namespace) {
        this.namespace = namespace;
    }

    /**
     * @param path entity name
     * @return location for main
     */
    public ModelLayerLocation register(String path) {
        return this.register(path, "main");
    }

    /**
     * @param path entity name
     * @param layer layer name
     * @return location for <code>layer</code>
     */
    public ModelLayerLocation register(String path, String layer) {
        return new ModelLayerLocation(new ResourceLocation(this.namespace, path), layer);
    }

    /**
     * @param path entity name
     * @return location for inner armor
     */
    public ModelLayerLocation registerInnerArmor(String path) {
        return this.register(path, "inner_armor");
    }

    /**
     * @param path entity name
     * @return location for outer armor
     */
    public ModelLayerLocation registerOuterArmor(String path) {
        return this.register(path, "outer_armor");
    }

    /**
     * @param namespace registry namespace
     * @return new registry from <code>namespace</code>
     */
    public static ModelLayerFactory of(String namespace) {
        return new ModelLayerFactory(namespace);
    }
}
