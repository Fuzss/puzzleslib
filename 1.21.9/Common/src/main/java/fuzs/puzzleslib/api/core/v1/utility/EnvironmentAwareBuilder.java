package fuzs.puzzleslib.api.core.v1.utility;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.ModLoader;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * A builder template that can be configured based on the current mod loader environment.
 *
 * @param <T> type of builder instance
 */
public interface EnvironmentAwareBuilder<T> {

    /**
     * Allows for registering content in the common project for only Fabric-like mod loaders.
     *
     * @return this manager as a builder
     */
    default T whenOnFabricLike() {
        return this.whenOn(ModLoader.getFabricLike());
    }

    /**
     * Allows for registering content in the common project for only Forge-like mod loaders.
     *
     * @return this manager as a builder
     */
    default T whenOnForgeLike() {
        return this.whenOn(ModLoader.getForgeLike());
    }

    /**
     * Allows for registering content in the common project for only a few mod loaders.
     *
     * @param forbiddenModLoaders the mod loaders to not register on
     * @return this manager as a builder
     */
    default T whenNotOn(ModLoader... forbiddenModLoaders) {
        Preconditions.checkState(forbiddenModLoaders.length > 0, "mod loaders is empty");
        return this.whenOn(EnumSet.complementOf(EnumSet.copyOf(Arrays.asList(forbiddenModLoaders)))
                .toArray(ModLoader[]::new));
    }

    /**
     * Allows for registering content in the common project for only a few mod loaders.
     *
     * @param allowedModLoaders the mod loaders to register on, every mod loader not registered to should handle this in
     *                          the loader specific subproject
     * @return this manager as a builder
     */
    T whenOn(ModLoader... allowedModLoaders);
}
