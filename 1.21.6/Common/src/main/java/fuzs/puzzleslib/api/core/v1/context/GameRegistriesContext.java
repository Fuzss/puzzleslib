package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.Registry;

/**
 * Register built-in static registries.
 * <p>
 * For creating static registries see {@link fuzs.puzzleslib.api.init.v3.registry.RegistryFactory}.
 */
public interface GameRegistriesContext {

    /**
     * Register an already constructed static registry.
     *
     * @param registry the registry
     * @param <T>      registry values type
     */
    <T> void registerRegistry(Registry<T> registry);
}
