package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.impl.core.ModContext;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * Common access to various mod loader methods and fields.
 */
public interface ModLoaderEnvironment {
    ModLoaderEnvironment INSTANCE = ServiceProviderHelper.load(ModLoaderEnvironment.class);

    /**
     * @return the type of mod loader we are loaded on
     */
    ModLoader getModLoader();

    /**
     * @return is physical environment client
     */
    boolean isClient();

    /**
     * @return is physical environment server
     */
    boolean isServer();

    /**
     * @return the minecraft game directory (<code>.minecraft</code> for clients, otherwise the server directory)
     */
    Path getGameDirectory();

    /**
     * @return <code>mods</code> directory for all installed mod jars inside the game directory
     */
    Path getModsDirectory();

    /**
     * @return <code>config</code> directory inside the game directory
     */
    Path getConfigDirectory();

    /**
     * @return is this running in a development environment
     */
    boolean isDevelopmentEnvironment();

    /**
     * @return a wrapped mod list
     */
    Map<String, ModContainer> getModList();

    /**
     * @param modId mod id to check
     * @return is this mod loaded
     */
    default boolean isModLoaded(String modId) {
        return this.getModList().containsKey(modId);
    }

    /**
     * @param modId mod id to check
     * @return the corresponding mod container if found
     */
    default Optional<ModContainer> getModContainer(String modId) {
        return this.isModLoaded(modId) ? Optional.of(this.getModList().get(modId)) : Optional.empty();
    }

    /**
     * A simple check for any mod constructed using Puzzles Lib to find if the mod is installed on the server.
     * <p>Useful for altering client behavior depending on the server mod state.
     * <p>This method CANNOT be used for checking the state of any arbitrary mod, only Puzzles Lib mods are supported.
     *
     * @param modId the mod to check
     * @return is the mod installed on the server
     */
    default boolean isModPresentServerside(String modId) {
        return ModContext.isPresentServerside(modId);
    }

    /**
     * @return a wrapper for Fabric's object share, will return a dummy wrapper on Forge
     */
    ObjectShareAccess getObjectShareAccess();
}