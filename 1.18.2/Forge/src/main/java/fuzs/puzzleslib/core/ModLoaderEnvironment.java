package fuzs.puzzleslib.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.Optional;

/**
 * easy access to various mod loader methods and fields for forge
 */
public class ModLoaderEnvironment {

    /**
     * @return current physical environment
     */
    public static Dist getEnvironmentType() {
        return FMLEnvironment.dist;
    }

    /**
     * @param envType env to check
     * @return is this the current physical environment
     */
    public static boolean isEnvironmentType(Dist envType) {
        return getEnvironmentType() == envType;
    }

    /**
     * @return is physical environment client
     */
    public static boolean isClient() {
        return getEnvironmentType().isClient();
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
        return FMLPaths.GAMEDIR.get();
    }

    /**
     * @return forge config dir within game dir
     */
    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    /**
     * @return is this running in a development environment
     */
    public static boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    /**
     * @param modId mod id to check
     * @return is this mod loaded
     */
    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    /**
     * @param modId mod id to get
     * @return container for mod, empty if mod not present
     */
    public static Optional<? extends ModContainer> getModContainer(String modId) {
        return ModList.get().getModContainerById(modId);
    }
}