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
     * The following mapping namespaces are used in the certain situations:
     * <ul>
     * <li><code>named</code> in a development environment and in production using NeoForge</li>
     * <li><code>intermediary</code> in production using Fabric</li>
     * <li><code>srg</code> in production using older Forge versions</li>
     * </ul>
     *
     * @return runtime mappings namespace
     */
    String getCurrentMappingsNamespace();

    /**
     * @return is this running in a development environment
     */
    boolean isDevelopmentEnvironment();

    /**
     * @return is this running from a data generation configuration
     */
    boolean isDataGeneration();

    /**
     * @param modId the mod id
     * @return is this running in a development environment with the system property
     *         {@code ${modId}.isDevelopmentEnvironment=true} set
     */
    default boolean isDevelopmentEnvironmentWithoutDataGeneration(String modId) {
        if (this.isDataGeneration()) {
            return false;
        } else {
            return this.isDevelopmentEnvironment(modId);
        }
    }

    /**
     * @param modId the mod id
     * @return is this running in a development environment with the system property
     *         {@code ${modId}.isDevelopmentEnvironment=true} set
     */
    default boolean isDevelopmentEnvironment(String modId) {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment()) {
            return false;
        } else {
            return Boolean.getBoolean(modId + ".isDevelopmentEnvironment");
        }
    }

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
        return Optional.ofNullable(this.getModList().get(modId));
    }

    /**
     * A simple check for any mod constructed using Puzzles Lib to find if the mod is installed on the server.
     * <p>
     * Useful for altering client behavior depending on the server mod state.
     * <p>
     * This method <b>CANNOT</b> be used for checking the state of any arbitrary mod, only Puzzles Lib mods are
     * supported.
     *
     * @param modId the mod to check
     * @return is the mod installed on the server
     */
    default boolean isModPresentServerside(String modId) {
        return ModContext.isPresentServerside(modId);
    }
}