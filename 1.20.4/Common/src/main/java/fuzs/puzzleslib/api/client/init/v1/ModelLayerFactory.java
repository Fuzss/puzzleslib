package fuzs.puzzleslib.api.client.init.v1;

import fuzs.puzzleslib.impl.client.init.ModelLayerFactoryImpl;
import net.minecraft.client.model.geom.ModelLayerLocation;

/**
 * A helper class for creating {@link ModelLayerFactoryImpl} instances with a provided namespace.
 */
public interface ModelLayerFactory {

    /**
     * Creates a new instance from a namespace.
     *
     * @param namespace registry namespace
     * @return new registry from <code>namespace</code>
     */
    static ModelLayerFactory from(String namespace) {
        return new ModelLayerFactoryImpl(namespace);
    }

    /**
     * Creates a new {@link ModelLayerLocation}, the used layer is <code>main</code>.
     *
     * @param path entity name
     * @return location for main
     */
    ModelLayerLocation register(String path);

    /**
     * Creates a new {@link ModelLayerLocation}, the used layer is provided by the parameter <code>layer</code>.
     *
     * @param path  entity name
     * @param layer layer name
     * @return location for <code>layer</code>
     */
    ModelLayerLocation register(String path, String layer);

    /**
     * Creates a new {@link ModelLayerLocation}, the used layer is <code>inner_armor</code>.
     *
     * @param path entity name
     * @return location for inner armor
     */
    ModelLayerLocation registerInnerArmor(String path);

    /**
     * Creates a new {@link ModelLayerLocation}, the used layer is <code>outer_armor</code>.
     *
     * @param path entity name
     * @return location for outer armor
     */
    ModelLayerLocation registerOuterArmor(String path);
}
