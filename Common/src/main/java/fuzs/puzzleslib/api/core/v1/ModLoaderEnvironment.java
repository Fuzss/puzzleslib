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
     * safe version of {@link #isModLoaded} on fml
     *
     * @param modId mod id to check
     * @return is this mod loaded or have available mods not been collected yet (mod list is still null)
     *
     * @deprecated no longer required, implementation on Forge now checks on its own which mod list to use
     */
    @Deprecated(forRemoval = true)
    default boolean isModLoadedSafe(String modId) {
        return this.isModLoaded(modId);
    }

    /**
     * Finds the display name associated with a certain <code>modId</code>.
     *
     * @param modId the mod id
     * @return the corresponding display name
     *
     * @deprecated use {@link #getModContainer(String)} and then {@link ModContainer#getDisplayName()} instead
     */
    @Deprecated(forRemoval = true)
    default Optional<String> getModName(String modId) {
        return this.getModContainer(modId).map(ModContainer::getDisplayName);
    }

    /**
     * Finds a resource in a mod jar file.
     *
     * @param modId the mod id to check the jar file from
     * @param path  resource name, if entered as single string path components are separated using "/"
     * @return path to the resource if it exists, otherwise empty
     *
     * @deprecated use {@link #getModContainer(String)} and then {@link ModContainer#findResource(String...)} instead
     */
    @Deprecated(forRemoval = true)
    default Optional<Path> findModResource(String modId, String... path) {
        return this.getModContainer(modId).flatMap(t -> t.findResource(path));
    }

    /**
     * @return a wrapper for Fabric's object share, will return a dummy wrapper on Forge
     */
    ObjectShareAccess getObjectShareAccess();
}