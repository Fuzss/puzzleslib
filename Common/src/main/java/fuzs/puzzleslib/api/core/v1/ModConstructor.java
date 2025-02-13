package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.api.core.v1.context.*;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.core.CommonFactories;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.util.Strings;

import java.util.function.Supplier;

/**
 * A base class for a mods main common class, contains a bunch of methods for registering various common content and
 * components.
 */
public interface ModConstructor extends BaseModConstructor {

    /**
     * Construct the main {@link ModConstructor} instance to begin initialization of a mod.
     *
     * @param modId                  the mod id for registering events on Forge to the correct mod event bus
     * @param modConstructorSupplier the main mod instance for mod setup
     */
    static void construct(String modId, Supplier<ModConstructor> modConstructorSupplier) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("mod id is empty");
        // build first to force class being loaded for executing buildables
        ModConstructor modConstructor = modConstructorSupplier.get();
        ResourceLocation resourceLocation = ModContext.getPairingIdentifier(modId, modConstructor);
        PuzzlesLib.LOGGER.info("Constructing common components for {}", resourceLocation);
        ModContext modContext = ModContext.get(modId);
        modContext.beforeModConstruction();
        CommonFactories.INSTANCE.constructMod(modId, modConstructor);
        modContext.afterModConstruction(resourceLocation);
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
     * @param context add to entity spawn placement register
     */
    default void onRegisterSpawnPlacements(SpawnPlacementsContext context) {
        // NO-OP
    }

    /**
     * TODO rename onCreateEntityAttributes along with context
     *
     * @param context add default attributes for our own entities to entity attribute map
     */
    default void onEntityAttributeCreation(EntityAttributesCreateContext context) {
        // NO-OP
    }

    /**
     * TODO rename onModifyEntityAttributes along with context
     *
     * @param context replace or add attribute in entity attribute map
     */
    default void onEntityAttributeModification(EntityAttributesModifyContext context) {
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
     * @param context register blocks that {@link net.minecraft.world.level.block.FireBlock} can spread to
     */
    default void onRegisterFlammableBlocks(FlammableBlocksContext context) {
        // NO-OP
    }

    /**
     * @param context register items as furnace fuel
     */
    default void onRegisterFuelValues(FuelValuesContext context) {
        // NO-OP
    }

    /**
     * @param context register items for usage with the composter block
     */
    default void onRegisterCompostableBlocks(CompostableBlocksContext context) {
        // NO-OP
    }

    /**
     * @param context register various block transformations triggered by right-clicking with certain vanilla tools
     */
    default void onRegisterBlockInteractions(BlockInteractionsContext context) {
        // NO-OP
    }

    /**
     * @param context register additional data pack sources
     */
    default void onAddDataPackFinders(PackRepositorySourcesContext context) {
        // NO-OP
    }

    /**
     * TODO rename onRegisterGameRegistriesContext
     *
     * @param context register built-in static registries
     */
    default void onGameRegistriesContext(GameRegistriesContext context) {
        // NO-OP
    }

    /**
     * TODO rename onRegisterDataPackRegistriesContext
     *
     * @param context register data pack-driven dynamic registries
     */
    default void onDataPackRegistriesContext(DataPackRegistriesContext context) {
        // NO-OP
    }
}
