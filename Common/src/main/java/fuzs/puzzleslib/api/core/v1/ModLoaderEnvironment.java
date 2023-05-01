package fuzs.puzzleslib.api.core.v1;

import java.nio.file.Path;
import java.util.Optional;

/**
 * easy access to various mod loader methods and fields for forge
 */
public interface ModLoaderEnvironment {
    /**
     * instance of the mod loader environment SPI
     */
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
    @Deprecated(forRemoval = true)
    default Path getGameDir() {
        return this.getGameDirectory();
    }

    /**
     * @return the minecraft game directory (<code>.minecraft</code> for clients, otherwise the server directory)
     */
    Path getGameDirectory();

    /**
     * @return <code>mods</code> directory for all installed mod jars inside the game directory
     */
    Path getModsDirectory();

    /**
     * @return config dir within game dir
     */
    @Deprecated(forRemoval = true)
    default Path getConfigDir() {
        return this.getConfigDirectory();
    }

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
    default boolean isModLoadedSafe(String modId) {
        return this.isModLoaded(modId);
    }

    /**
     * Finds the display name associated with a certain <code>modId</code>
     *
     * @param modId the mod id
     * @return the corresponding display name
     */
    Optional<String> getModName(String modId);
}