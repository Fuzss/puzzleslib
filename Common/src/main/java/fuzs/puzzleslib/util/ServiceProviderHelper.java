package fuzs.puzzleslib.util;

import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

/**
 * Small helper methods related to service provider interfaces.
 *
 * <p>Class from 1.19.3+, only copied here internally to allow early class loading of just this.
 */
@ApiStatus.Internal
public final class ServiceProviderHelper {

    /**
     * Loads a service provider interface or throws an exception.
     *
     * @param clazz         service provider interface class to load
     * @param <T>           interface type
     *
     * @return loaded service
     */
    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz, ServiceProviderHelper.class.getClassLoader()).findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
