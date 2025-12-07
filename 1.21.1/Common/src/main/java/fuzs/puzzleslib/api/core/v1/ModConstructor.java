package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.api.core.v1.context.*;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * A base class for a mods main common class, contains a bunch of methods for registering various common content and
 * components.
 */
public interface ModConstructor extends BaseModConstructor {

    /**
     * Construct the {@link ModConstructor} instance to begin initialization of a mod.
     *
     * @param modId                  the mod id
     * @param modConstructorSupplier the mod instance for the setup
     */
    static void construct(String modId, Supplier<ModConstructor> modConstructorSupplier) {
        construct(ResourceLocation.fromNamespaceAndPath(modId, "common"), modConstructorSupplier);
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
                ModContext::runBeforeConstruction);
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
     * @param context register attributes for entities
     */
    default void onRegisterEntityAttributes(EntityAttributesContext context) {
        // NO-OP
    }

    /**
     * @param context add to entity spawn placement register
     */
    default void onRegisterSpawnPlacements(final SpawnPlacementsContext context) {
        // NO-OP
    }

    /**
     * @param context add default attributes for our own entities to entity attribute map
     */
    @Deprecated
    default void onEntityAttributeCreation(final EntityAttributesCreateContext context) {
        // NO-OP
    }

    /**
     * @param context replace or add attribute in entity attribute map
     */
    @Deprecated
    default void onEntityAttributeModification(final EntityAttributesModifyContext context) {
        // NO-OP
    }

    /**
     * @param context allows for registering modifications (including additions and removals) to biomes loaded from the
     *                current data pack
     */
    default void onRegisterBiomeModifications(final fuzs.puzzleslib.api.core.v2.context.BiomeModificationsContext context) {
        // NO-OP
    }

    /**
     * @param context allows for registering modifications (including additions and removals) to biomes loaded from the
     *                current data pack
     */
    @Deprecated
    default void onRegisterBiomeModifications(final BiomeModificationsContext context) {
        // NO-OP
    }

    /**
     * @param context register content to various gameplay registries
     */
    default void onRegisterGameplayContent(GameplayContentContext context) {
        // NO-OP
    }

    /**
     * @param context add fuel burn time for blocks and items, e.g. in a furnace
     */
    @Deprecated
    default void onRegisterFuelBurnTimes(final FuelBurnTimesContext context) {
        // NO-OP
    }

    /**
     * @param context register blocks that {@link net.minecraft.world.level.block.FireBlock} can spread to
     */
    @Deprecated
    default void onRegisterFlammableBlocks(final FlammableBlocksContext context) {
        // NO-OP
    }

    /**
     * @param context register items for usage with the composter block
     */
    @Deprecated
    default void onRegisterCompostableBlocks(CompostableBlocksContext context) {
        // NO-OP
    }

    /**
     * @param context register various block transformations triggered by right-clicking with certain vanilla tools
     */
    @Deprecated
    default void onRegisterBlockInteractions(BlockInteractionsContext context) {
        // NO-OP
    }

    /**
     * @param context register new creative mode tabs via the respective builder
     */
    @Deprecated
    default void onRegisterCreativeModeTabs(final CreativeModeTabContext context) {
        // NO-OP
    }

    /**
     * @param context add items to a creative tab
     */
    @Deprecated
    default void onBuildCreativeModeTabContents(final BuildCreativeModeTabContentsContext context) {
        // NO-OP
    }

    /**
     * @param context register additional data pack sources
     */
    default void onAddDataPackFinders(final PackRepositorySourcesContext context) {
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
}
