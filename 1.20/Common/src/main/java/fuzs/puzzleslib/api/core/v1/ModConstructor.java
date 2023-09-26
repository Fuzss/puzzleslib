package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.api.core.v1.context.*;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.core.CommonFactories;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.util.Strings;

import java.util.Set;
import java.util.function.Supplier;

/**
 * a base class for a mods main common class, contains a bunch of methods for registering various things
 */
public interface ModConstructor extends BaseModConstructor {

    /**
     * Construct the main {@link ModConstructor} instance provided as <code>supplier</code> to begin initialization of a mod.
     *
     * @param modId    the mod id for registering events on Forge to the correct mod event bus
     * @param supplier the main mod instance for mod setup
     */
    static void construct(String modId, Supplier<ModConstructor> supplier) {
        construct(modId, supplier, new ContentRegistrationFlags[0]);
    }

    /**
     * Construct the main {@link ModConstructor} instance provided as <code>supplier</code> to begin initialization of a mod.
     *
     * @param modId    the mod id for registering events on Forge to the correct mod event bus
     * @param supplier the main mod instance for mod setup
     * @param flags    specific content this mod uses that needs to be additionally registered
     * @deprecated replaced provided flags with {@link BaseModConstructor#getContentRegistrationFlags()}
     */
    @Deprecated(forRemoval = true)
    static void construct(String modId, Supplier<ModConstructor> supplier, ContentRegistrationFlags... flags) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("mod id is empty");
        // build first to force class being loaded for executing buildables
        ModConstructor instance = supplier.get();
        ResourceLocation identifier = ModContext.getPairingIdentifier(modId, instance);
        PuzzlesLib.LOGGER.info("Constructing common components for {}", identifier);
        ModContext modContext = ModContext.get(modId);
        ContentRegistrationFlags[] builtInFlags = instance.getContentRegistrationFlags();
        Set<ContentRegistrationFlags> availableFlags = Set.of(builtInFlags.length != 0 ? builtInFlags : flags);
        Set<ContentRegistrationFlags> flagsToHandle = modContext.getFlagsToHandle(availableFlags);
        modContext.beforeModConstruction();
        CommonFactories.INSTANCE.constructMod(modId, instance, availableFlags, flagsToHandle);
        modContext.afterModConstruction(identifier);
    }

    /**
     * runs when the mod is first constructed, mainly used for registering game content, configs, network packages, and event callbacks
     */
    default void onConstructMod() {

    }

    /**
     * runs after content has been registered, so it's safe to use here
     * used to set various values and settings for already registered content
     *
     * @param context enqueue work to be run sequentially for all mods as the setup phase runs in parallel on Forge
     * @deprecated now always runs deferred, use {@link #onCommonSetup()}
     */
    default void onCommonSetup(final ModLifecycleContext context) {

    }

    /**
     * runs after content has been registered, so it's safe to use here
     * used to set various values and settings for already registered content
     */
    default void onCommonSetup() {
        this.onCommonSetup(Runnable::run);
    }

    /**
     * provides a place for registering spawn placements for entities
     *
     * @param context add to spawn placement register
     */
    default void onRegisterSpawnPlacements(final SpawnPlacementsContext context) {

    }

    /**
     * allows for registering default attributes for our own entities
     * anything related to already existing entities (vanilla and modded) needs to be done in {@link #onEntityAttributeModification}
     *
     * @param context add to entity attribute map
     */
    default void onEntityAttributeCreation(final EntityAttributesCreateContext context) {

    }

    /**
     * allows for modifying the attributes of an already existing entity, attributes are modified individually
     *
     * @param context replace/add attribute to entity attribute map
     */
    default void onEntityAttributeModification(final EntityAttributesModifyContext context) {

    }

    /**
     * allows for setting burn times for fuel items, e.g. in a furnace
     *
     * @param context add fuel burn time for items/blocks
     */
    default void onRegisterFuelBurnTimes(final FuelBurnTimesContext context) {

    }

    /**
     * @param context allows for registering modifications (including additions and removals) to biomes loaded from the current data pack
     */
    default void onRegisterBiomeModifications(final BiomeModificationsContext context) {

    }

    /**
     * @param context register blocks that {@link net.minecraft.world.level.block.FireBlock} can spread to
     */
    default void onRegisterFlammableBlocks(final FlammableBlocksContext context) {

    }

    /**
     * @param context register various block transformations triggered by right-clicking with certain vanilla tools
     */
    default void onRegisterBlockInteractions(BlockInteractionsContext context) {

    }

    /**
     * @param context register new creative mode tabs via the respective builder
     */
    default void onRegisterCreativeModeTabs(final CreativeModeTabContext context) {

    }

    /**
     * @param context add items to a creative tab
     */
    default void onBuildCreativeModeTabContents(final BuildCreativeModeTabContentsContext context) {

    }

    /**
     * @param context register additional data pack sources
     */
    default void onAddDataPackFinders(final PackRepositorySourcesContext context) {

    }

    /**
     * @param context adds a listener to the server resource manager to reload at the end of all resources
     */
    default void onRegisterDataPackReloadListeners(final AddReloadListenersContext context) {

    }
}
