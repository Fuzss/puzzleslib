package fuzs.puzzleslib.core;

import net.fabricmc.api.EnvType;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * mostly copied from Forge's DistExecutor without pointless deprecation
 */
public class EnvTypeExecutor {
    /**
     * @param envType env type to run on
     * @param toRun to run
     * @param <T> return type
     * @return only returns when <code>envType</code> matches, null otherwise
     */
    @Nullable
    public static <T> T callWhenOn(EnvType envType, Supplier<Callable<T>> toRun) {
        if (ModLoaderEnvironment.isEnvironmentType(envType)) {
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
    public static void runWhenOn(EnvType envType, Supplier<Runnable> toRun) {
        if (ModLoaderEnvironment.isEnvironmentType(envType)) {
            toRun.get().run();
        }
    }

    /**
     * @param clientTarget get on client
     * @param serverTarget get on dedicated server
     * @param <T>          return type
     * @return value
     */
    public static <T> T runForEnvType(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        return switch (ModLoaderEnvironment.getEnvironmentType()) {
            case CLIENT -> clientTarget.get().get();
            case SERVER -> serverTarget.get().get();
        };
    }
}
