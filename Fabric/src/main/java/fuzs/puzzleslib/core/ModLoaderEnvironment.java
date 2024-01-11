package fuzs.puzzleslib.core;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;
import java.util.Optional;

/**
 * easy access to various mod loader methods and fields for forge
 */
public class ModLoaderEnvironment {
    /**
     * @return current physical environment
     */
    public static EnvType getEnvironmentType() {
        return FabricLoader.getInstance().getEnvironmentType();
    }

    /**
     * @param envType env to check
     * @return is this the current physical environment
     */
    public static boolean isEnvironmentType(EnvType envType) {
        return getEnvironmentType() == envType;
    }

    /**
     * @return is physical environment client
     */
    public static boolean isClient() {
        return isEnvironmentType(EnvType.CLIENT);
    }

    /**
     * @return is physical environment server
     */
    public static boolean isServer() {
        return !isClient();
    }

    /**
     * @return main minecraft game dir
     */
    public static Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    /**
     * @return forge config dir within game dir
     */
    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    /**
     * @return is this running in a development environment
     */
    public static boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    /**
     * @param modId mod id to check
     * @return is this mod loaded
     */
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    /**
     * @param modId mod id to get
     * @return container for mod, empty if mod not present
     */
    public static Optional<? extends ModContainer> getModContainer(String modId) {
        return FabricLoader.getInstance().getModContainer(modId);
    }
}