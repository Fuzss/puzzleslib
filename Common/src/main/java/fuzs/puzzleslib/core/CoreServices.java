package fuzs.puzzleslib.core;

import fuzs.puzzleslib.registry.CommonGameRuleFactory;

import java.util.ServiceLoader;

/**
 * common core services
 */
public class CoreServices {
    /**
     * helper class for mod loader environment
     */
    public static final ModLoaderEnvironment ENVIRONMENT = load(ModLoaderEnvironment.class);
    /**
     * important factories, mainly for networking, registering content and configs
     */
    public static final CommonFactories FACTORIES = load(CommonFactories.class);
    /**
     * helper class for creating and registering new game rules
     */
    public static final CommonGameRuleFactory GAME_RULES = load(CommonGameRuleFactory.class);

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