package fuzs.puzzleslib.api.core.v1;

import java.util.ServiceLoader;

/**
 * small helper methods
 */
public final class ServiceProviderHelper {

    /**
     * loads a service provider interface yay
     *
     * @param clazz         service provider interface class to load
     * @param <T>           interface type
     *
     * @return loaded service
     */
    public static <T> T loadServiceProvider(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
