package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.impl.core.ModContext;

import java.nio.file.Path;
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
     * @return is this Forge
     */
    default boolean isForge() {
        return this.getModLoader().isForge();
    }

    /**
     * @return is this Fabric
     */
    default boolean isFabric() {
        return this.getModLoader().isFabric();
    }

    /**
     * @return is this Quilt
     */
    default boolean isQuilt() {
        return this.getModLoader().isQuilt();
    }

    /**
     * @return current physical environment
     */
    @Deprecated(forRemoval = true)
    default DistType getEnvironmentType() {
        return this.isClient() ? DistType.CLIENT : DistType.SERVER;
    }

    /**
     * @param envType env to check
     * @return is this the current physical environment
     */
    @Deprecated(forRemoval = true)
    default boolean isEnvironmentType(DistType envType) {
        return this.getEnvironmentType() == envType;
    }

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
     * Finds a resource in a mod jar file.
     *
     * @param id       the mod id to check the jar file from
     * @param pathName resource name, if entered as single string path components are separated using "/"
     * @return path to the resource if it exists, otherwise empty
     */
    Optional<Path> findModResource(String id, String... pathName);

    /**
     * @return is this running in a development environment
     */
    boolean isDevelopmentEnvironment();

    /**
     * @param modId mod id to check
     * @return is this mod loaded
     */
    boolean isModLoaded(String modId);

    /**
     * safe version of {@link #isModLoaded} on fml
     *
     * @param modId mod id to check
     * @return is this mod loaded or have available mods not been collected yet (mod list is still null)
     */
    @Deprecated(forRemoval = true)
    default boolean isModLoadedSafe(String modId) {
        return this.isModPresent(modId);
    }

    /**
     * safe version of {@link #isModLoaded} on fml
     *
     * @param modId mod id to check
     * @return is this mod loaded or have available mods not been collected yet (mod list is still null)
     */
    default boolean isModPresent(String modId) {
        return this.isModLoaded(modId);
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
     * Finds the display name associated with a certain <code>modId</code>.
     *
     * @param modId the mod id
     * @return the corresponding display name
     */
    Optional<String> getModName(String modId);

    /**
     * @return a wrapper for Fabric's object share, will return a dummy wrapper on Forge
     */
    ObjectShareAccess getObjectShareAccess();
}