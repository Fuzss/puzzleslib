package fuzs.puzzleslib.core;

import fuzs.puzzleslib.registry.FuelManager;

import java.util.ServiceLoader;

/**
 * common services
 */
public class Services {
    /**
     * helper class for mod loader environment
     */
    public static final ModLoaderEnvironment ENVIRONMENT = load(ModLoaderEnvironment.class);
    /**
     * important factories, mainly for networking, registering content and configs
     */
    public static final CommonFactories FACTORIES = load(CommonFactories.class);
    /**
     * register for furnace fuel items, might be included in another service in the future
     */
    public static final FuelManager FUEL_MANAGER = load(FuelManager.class);

    /**
     * loads a service yay
     * @param clazz service class to load
     * @param <T>   service type
     * @return      loaded service
     */
    protected static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}