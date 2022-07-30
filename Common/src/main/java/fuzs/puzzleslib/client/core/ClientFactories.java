package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.model.geom.ModelLayerRegistry;

import java.util.function.Consumer;

/**
 * all sorts of instance factories that need to be created on a per-mod basis
 */
public interface ClientFactories {

    /**
     * this is very much unnecessary as the method is only ever called from loader specific code anyway which does have
     * access to the specific mod constructor, but for simplifying things and having this method in a common place we keep it here
     *
     * @param modId the mod id for registering events on Forge to the correct mod event bus
     * @return  provides a consumer for loading a mod being provided the base class
     */
    Consumer<ClientModConstructor> clientModConstructor(String modId);

    /**
     * helper for creating {@link ModelLayerRegistry} objects with a provided <code>modId</code>>
     *
     * @param modId the mod to create registry for
     * @return      mod specific registry instance
     */
    default ModelLayerRegistry modelLayerRegistration(String modId) {
        return ModelLayerRegistry.of(modId);
    }
}
