package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.api.core.v1.context.*;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

/**
 * A base class for a mod's main common class, containing a bunch of methods for registering various common content and
 * components.
 */
public interface ModConstructor {

    /**
     * Construct the {@link ModConstructor} instance to begin initialization of a mod.
     *
     * @param modId                  the mod id
     * @param modConstructorSupplier the mod instance for the setup
     */
    static void construct(String modId, Supplier<ModConstructor> modConstructorSupplier) {
        construct(Identifier.fromNamespaceAndPath(modId, "common"), modConstructorSupplier);
    }

    /**
     * Construct the {@link ModConstructor} instance to begin initialization of a mod.
     *
     * @param identifier       the identifier for the provided mod instance
     * @param modConstructorSupplier the mod instance for the setup
     */
    static void construct(Identifier identifier, Supplier<ModConstructor> modConstructorSupplier) {
        ModConstructorImpl.construct(identifier,
                modConstructorSupplier,
                ProxyImpl.get()::getModConstructorImpl,
                ModContext::runBeforeConstruction);
    }

    /**
     * Runs when the mod is first constructed. Used for registering game content, configs, network packages, and event
     * callbacks.
     */
    default void onConstructMod() {
        // NO-OP
    }

    /**
     * Runs after content has been registered. Used to set various values and settings for already registered content.
     *
     * @see fuzs.puzzleslib.api.event.v1.CommonSetupCallback
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
    default void onRegisterGameRegistries(GameRegistriesContext context) {
        // NO-OP
    }

    /**
     * @param context register data pack-driven dynamic registries
     */
    default void onRegisterDataPackRegistries(DataPackRegistriesContext context) {
        // NO-OP
    }

    /**
     * @param context register new villager trades
     */
    default void onRegisterVillagerTrades(VillagerTradesContext context) {
        // NO-OP
    }

    /**
     * @param context register server resource reload listeners
     */
    default void onAddDataPackReloadListeners(DataPackReloadListenersContext context) {
        // NO-OP
    }
}
