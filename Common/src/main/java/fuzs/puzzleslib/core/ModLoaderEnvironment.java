package fuzs.puzzleslib.core;

import java.nio.file.Path;

/**
 * easy access to various mod loader methods and fields for forge
 */
public interface ModLoaderEnvironment {

    /**
     * @return current physical environment
     */
    DistType getEnvironmentType();

    /**
     * @param envType env to check
     * @return is this the current physical environment
     */
    default boolean isEnvironmentType(DistType envType) {
        return this.getEnvironmentType() == envType;
    }

    /**
     * @return is physical environment client
     */
    default boolean isClient() {
        return this.isEnvironmentType(DistType.CLIENT);
    }

    /**
     * @return is physical environment server
     */
    default boolean isServer() {
        return !this.isClient();
    }

    /**
     * @return main minecraft game dir
     */
    Path getGameDir();

    /**
     * @return config dir within game dir
     */
    Path getConfigDir();

    /**
     * @return is this running in a development environment
     */
    boolean isDevelopmentEnvironment();

    /**
     * @param modId mod id to check
     * @return is this mod loaded
     */
    boolean isModLoaded(String modId);
}