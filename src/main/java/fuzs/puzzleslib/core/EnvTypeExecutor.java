package fuzs.puzzleslib.core;

import net.minecraftforge.api.distmarker.Dist;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * mostly copied from {@link net.minecraftforge.fml.DistExecutor} without pointless deprecation
 */
public class EnvTypeExecutor {

    /**
     * @param envType env type to run on
     * @param toRun to run
     * @param <T> return type
     * @return only returns when <code>envType</code> matches, null otherwise
     */
    @Nullable
    public static <T> T callWhenOn(Dist envType, Supplier<Callable<T>> toRun) {
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
    public static void runWhenOn(Dist envType, Supplier<Runnable> toRun) {
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
    public static <T> T runForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        return switch (ModLoaderEnvironment.getEnvironmentType()) {
            case CLIENT -> clientTarget.get().get();
            case DEDICATED_SERVER -> serverTarget.get().get();
        };
    }
}
