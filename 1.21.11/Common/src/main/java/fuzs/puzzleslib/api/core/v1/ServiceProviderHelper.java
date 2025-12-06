package fuzs.puzzleslib.api.core.v1;

import java.util.ServiceLoader;

/**
 * Small helper for instantiating service provider interfaces.
 */
public final class ServiceProviderHelper {

    /**
     * Loads a service provider interface or throws an exception.
     *
     * @param clazz the service provider interface class to load
     * @param <T>   the interface type
     * @return loaded service
     */
    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz, ServiceProviderHelper.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
