package fuzs.puzzleslib.core;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * mostly copied from Forge's DistExecutor without pointless deprecation
 */
public class DistTypeExecutor {

    /**
     * @param envType env type to run on
     * @param toRun to run
     * @param <T> return type
     * @return only returns when <code>envType</code> matches, null otherwise
     */
    @Nullable
    public static <T> T callWhenOn(DistType envType, Supplier<Callable<T>> toRun) {
        if (Services.ENVIRONMENT.isEnvironmentType(envType)) {
            try {
                return toRun.get().call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * @param envType env type to run on
     * @param toRun to run
     */
    public static void runWhenOn(DistType envType, Supplier<Runnable> toRun) {
        if (Services.ENVIRONMENT.isEnvironmentType(envType)) {
            toRun.get().run();
        }
    }

    /**
     * @param envType env type to run on
     * @param toGet to get
     * @return      supplier result or null
     * @param <T>   return type
     */
    @Nullable
    public static <T> T getWhenOn(DistType envType, Supplier<Supplier<T>> toGet) {
        if (Services.ENVIRONMENT.isEnvironmentType(envType)) {
            return toGet.get().get();
        }
        return null;
    }

    /**
     * @param clientTarget get on client
     * @param serverTarget get on dedicated server
     * @param <T>          return type
     * @return value
     */
    public static <T> T getForDistType(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        return switch (Services.ENVIRONMENT.getEnvironmentType()) {
            case CLIENT -> clientTarget.get().get();
            case SERVER -> serverTarget.get().get();
        };
    }
}
