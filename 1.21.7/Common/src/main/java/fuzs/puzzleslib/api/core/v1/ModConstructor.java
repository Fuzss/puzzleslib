package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.api.core.v1.context.*;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.resources.ResourceLocation;

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
        construct(ResourceLocationHelper.fromNamespaceAndPath(modId, "common"), modConstructorSupplier);
    }

    /**
     * Construct the {@link ModConstructor} instance to begin initialization of a mod.
     *
     * @param resourceLocation       the identifier for the provided mod instance
     * @param modConstructorSupplier the mod instance for the setup
     */
    static void construct(ResourceLocation resourceLocation, Supplier<ModConstructor> modConstructorSupplier) {
        ModConstructorImpl.construct(resourceLocation,
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
     * TODO remove context from method name
     *
     * @param context register built-in static registries
     */
    default void onRegisterGameRegistriesContext(GameRegistriesContext context) {
        // NO-OP
    }

    /**
     * TODO remove context from method name
     *
     * @param context register data pack-driven dynamic registries
     */
    default void onRegisterDataPackRegistriesContext(DataPackRegistriesContext context) {
        // NO-OP
    }

    /**
     * @param context register new villager trades
     */
    default void onRegisterVillagerTrades(VillagerTradesContext context) {
        // NO-OP
    }
}
