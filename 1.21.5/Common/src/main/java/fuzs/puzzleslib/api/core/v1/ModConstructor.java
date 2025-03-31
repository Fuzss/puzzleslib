package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.api.core.v1.context.*;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;

import java.util.function.Supplier;

/**
 * A base class for a mods main common class, contains a bunch of methods for registering various common content and
 * components.
 */
public interface ModConstructor {

    /**
     * Construct the main {@link ModConstructor} instance to begin initialization of a mod.
     *
     * @param modId                  the mod id for registering events on Forge to the correct mod event bus
     * @param modConstructorSupplier the main mod instance for mod setup
     */
    static void construct(String modId, Supplier<ModConstructor> modConstructorSupplier) {
        PuzzlesLib.LOGGER.info("Constructing common components for {}", modId);
        ModConstructorImpl.construct(modId,
                modConstructorSupplier,
                ProxyImpl.get()::getModConstructorImpl,
                ModContext::buildAll);
    }

    /**
     * Runs when the mod is first constructed.
     * <p>
     * Mainly used for registering game content, configs, network packages, and event callbacks.
     */
    default void onConstructMod() {
        // NO-OP
    }

    /**
     * Runs after content has been registered, so it's safe to use here.
     * <p>
     * Used to set various values and settings for already registered content.
     */
    default void onCommonSetup() {
        // NO-OP
    }

    /**
     * @param context register custom messages that are compatible with vanilla
     *                {@link net.minecraft.network.protocol.Packet Packets}
     */
    default void onRegisterPayloadTypes(PayloadTypesContext context) {
        // NO-OP
    }

    /**
     * @param context register attributes for entities
     */
    default void onRegisterEntityAttributes(EntityAttributesContext context) {
        // NO-OP
    }

    /**
     * @param context add to entity spawn placement register
     */
    default void onRegisterSpawnPlacements(SpawnPlacementsContext context) {
        // NO-OP
    }

    /**
     * @param context allows for registering modifications (including additions and removals) to biomes loaded from the
     *                current data pack
     */
    default void onRegisterBiomeModifications(BiomeModificationsContext context) {
        // NO-OP
    }

    /**
     * @param context register content to various gameplay registries
     */
    default void onRegisterGameplayContent(GameplayContentContext context) {
        // NO-OP
    }

    /**
     * @param context register additional data pack sources
     */
    default void onAddDataPackFinders(PackRepositorySourcesContext context) {
        // NO-OP
    }

    /**
     * @param context register built-in static registries
     */
    default void onRegisterGameRegistriesContext(GameRegistriesContext context) {
        // NO-OP
    }

    /**
     * @param context register data pack-driven dynamic registries
     */
    default void onRegisterDataPackRegistriesContext(DataPackRegistriesContext context) {
        // NO-OP
    }
}
