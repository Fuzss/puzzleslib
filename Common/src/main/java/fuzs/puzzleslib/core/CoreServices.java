package fuzs.puzzleslib.core;

import fuzs.puzzleslib.init.CommonGameRuleFactory;
import fuzs.puzzleslib.util.PuzzlesUtil;

import static fuzs.puzzleslib.util.PuzzlesUtil.loadServiceProvider;

/**
 * common core services
 *
 * <p>TODO make final
 */
public class CoreServices {
    /**
     * helper class for mod loader environment
     */
    public static final ModLoaderEnvironment ENVIRONMENT = loadServiceProvider(ModLoaderEnvironment.class);
    /**
     * important factories, mainly for networking, registering content and configs
     */
    public static final CommonFactories FACTORIES = loadServiceProvider(CommonFactories.class);
    /**
     * useful methods for gameplay related things that require mod loader specific abstractions
     */
    public static final CommonAbstractions ABSTRACTIONS = loadServiceProvider(CommonAbstractions.class);
    /**
     * helper class for creating and registering new game rules
     */
    public static final CommonGameRuleFactory GAME_RULES = loadServiceProvider(CommonGameRuleFactory.class);

    /**
     * loads a service provider interface yay
     *
     * @param clazz         service class to load
     * @param <T>           service type
     *
     * @return loaded service
     *
     * @deprecated use {@link PuzzlesUtil#loadServiceProvider}
     */
    @Deprecated(forRemoval = true)
    public static <T> T load(Class<T> clazz) {
        return loadServiceProvider(clazz);
    }
}