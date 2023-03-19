package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.api.core.v1.context.*;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.core.CommonFactories;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.logging.log4j.util.Strings;

import java.util.function.Supplier;

/**
 * a base class for a mods main common class, contains a bunch of methods for registering various things
 */
public interface ModConstructor {

    /**
     * this is very much unnecessary as the method is only ever called from loader specific code anyway which does have
     * access to the specific mod constructor, but for simplifying things and having this method in a common place we keep it here
     *
     * @param modId                the mod id for registering events on Forge to the correct mod event bus
     * @param modConstructor       the main mod instance for mod setup
     * @param contentRegistrations specific content this mod uses that needs to be additionally registered
     */
    static void construct(String modId, Supplier<ModConstructor> modConstructor, ContentRegistrationFlags... contentRegistrations) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("mod id must not be empty");
        PuzzlesLib.LOGGER.info("Constructing common components for mod {}", modId);
        CommonFactories.INSTANCE.constructMod(modId, modConstructor, contentRegistrations);
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
     */
    default void onCommonSetup(final ModLifecycleContext context) {

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
     * register a new command, also natively supports replacing existing commands
     *
     * @param context context with helper objects for registering commands
     */
    default void onRegisterCommands(final RegisterCommandsContext context) {

    }

    /**
     * allows for replacing built-in {@link LootTable}s on loading
     *
     * @param context replace a whole {@link LootTable}
     */
    default void onLootTableReplacement(final LootTablesContext.Replace context) {

    }

    /**
     * allows changing of {@link LootPool}s in a {@link LootTable}
     *
     * @param context add or remove a {@link LootPool}
     */
    default void onLootTableModification(final LootTablesContext.Modify context) {

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
     * @param context register new creative mode tabs via the respective builder
     */
    default void onRegisterCreativeModeTabs(final CreativeModeTabContext context) {

    }
}
