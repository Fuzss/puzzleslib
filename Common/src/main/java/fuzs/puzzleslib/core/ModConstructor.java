package fuzs.puzzleslib.core;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;

/**
 * a base class for a mods main common class, contains a bunch of methods for registering various things
 */
public interface ModConstructor {

    /**
     * runs when the mod is first constructed, mainly used for registering game content, configs, network packages, and event callbacks
     */
    default void onConstructMod() {

    }

    /**
     * runs after content has been registered, so it's safe to use here
     * used to set various values and settings for already registered content
     */
    default void onCommonSetup() {

    }

    /**
     * provides a place for registering spawn placements for entities
     *
     * @param context add to spawn placement register
     */
    default void onRegisterSpawnPlacements(SpawnPlacementsContext context) {

    }

    /**
     * allows for registering default attributes for our own entities
     * anything related to already existing entities (vanilla and modded) needs to be done in {@link #onEntityAttributeModification}
     *
     * @param context add to entity attribute map
     */
    default void onEntityAttributeCreation(EntityAttributesCreateContext context) {

    }

    /**
     * allows for modifying the attributes of an already existing entity, attributes are modified individually
     *
     * @param context replace/add attribute to entity attribute map
     */
    default void onEntityAttributeModification(EntityAttributesModifyContext context) {

    }

    /**
     * allows for setting burn times for fuel items, e.g. in a furnace
     *
     * @param context add fuel burn time for items/blocks
     */
    default void onRegisterFuelBurnTimes(FuelBurnTimesContext context) {

    }

    /**
     * register a new command, also supports replacing existing commands by default
     *
     * @param dispatcher    the dispatcher used for registering commands
     * @param environment   command selection environment
     * @param context       registry access context
     *
     * @deprecated          use {@link #onRegisterCommands(RegisterCommandsContext)} instead
     */
    @Deprecated(forRemoval = true)
    default void onRegisterCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) {

    }

    /**
     * register a new command, also natively supports replacing existing commands
     *
     * @param context context with helper objects for registering commands
     */
    default void onRegisterCommands(RegisterCommandsContext context) {
        this.onRegisterCommands(context.dispatcher(), context.context(), context.environment());
    }

    /**
     * allows for replacing built-in {@link LootTable}s on loading
     *
     * @param context replace a whole {@link LootTable}
     */
    default void onLootTableReplacement(LootTablesReplaceContext context) {

    }

    /**
     * allows changing of {@link LootPool}s in a {@link LootTable}
     *
     * @param context add or remove a {@link LootPool}
     */
    default void onLootTableModification(LootTablesModifyContext context) {

    }

    /**
     * register a default spawn placement for entities
     */
    @FunctionalInterface
    interface SpawnPlacementsContext {

        /**
         * registers a spawning behavior for an <code>entityType</code>
         *
         * @param entityType        the entity type
         * @param location          type of spawn placement, probably {@link SpawnPlacements.Type#ON_GROUND}
         * @param heightmap         heightmap type, probably {@link Heightmap.Types#MOTION_BLOCKING_NO_LEAVES}
         * @param spawnPredicate    custom spawn predicate for mob
         * @param <T>               type of entity
         */
        <T extends Mob> void registerSpawnPlacement(EntityType<T> entityType, SpawnPlacements.Type location, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> spawnPredicate);
    }

    /**
     * register default attributes for our own entities
     */
    @FunctionalInterface
    interface EntityAttributesCreateContext {

        /**
         * register attributes for our own entities, modifying attributes for any other entity (vanilla or modded) should be done using {@link EntityAttributesModifyContext}
         *
         * @param type type of entity
         * @param builder the attribute supplier builder
         */
        void registerEntityAttributes(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder);
    }

    /**
     * registers modifications to attributes of already existing entities (not from our mod)
     */
    @FunctionalInterface
    interface EntityAttributesModifyContext {

        /**
         * use this method for modifying attributes of existing entity types
         * this overrides/adds attributes individually as this is what is done on Forge
         *
         * @param type                  type of entity
         * @param attribute             attribute to override/add
         */
        default void registerAttributeModification(EntityType<? extends LivingEntity> type, Attribute attribute) {
            this.registerAttributeModification(type, attribute, attribute.getDefaultValue());
        }

        /**
         * use this method for modifying attributes of existing entity types
         * this overrides/adds attributes individually as this is what is done on Forge
         *
         * @param type                  type of entity
         * @param attribute             attribute to override/add
         * @param attributeValue        new value, possibly {@link Attribute#getDefaultValue()}
         */
        void registerAttributeModification(EntityType<? extends LivingEntity> type, Attribute attribute, double attributeValue);
    }

    /**
     * applies fuel burn times instead of implementing this on the item side
     * heavily inspired by FuelHandler found in Vazkii's Quark mod
     */
    @FunctionalInterface
    interface FuelBurnTimesContext {

        /**
         * base method, registers a fuel item
         *
         * @param item item to add
         * @param burnTime burn time in ticks
         */
        void registerFuelItem(Item item, int burnTime);

        /**
         * overload method for blocks
         *
         * @param block block to add
         * @param burnTime burn time in ticks
         */
        default void registerFuelBlock(Block block, int burnTime) {
            this.registerFuelItem(block.asItem(), burnTime);
        }

        /**
         * add wooden block with default vanilla times
         *
         * @param block block to add with burn time of 300 ticks
         */
        default void registerWoodenBlock(Block block) {
            this.registerFuelBlock(block, block instanceof SlabBlock ? 150 : 300);
        }
    }

    /**
     * register a new command, also supports replacing existing commands by default
     *
     * @param dispatcher    the dispatcher used for registering commands
     * @param environment   command selection environment
     * @param context       registry access context
     */
    record RegisterCommandsContext(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) {

    }

    /**
     * basic context for loot tables
     */
    abstract class LootTablesContext {
        /**
         * access to all loot tables
         */
        private final LootTables lootManager;
        /**
         * the loot table id
         */
        private final ResourceLocation id;

        /**
         * @param lootManager   access to all loot tables
         * @param id            the loot table id
         */
        protected LootTablesContext(LootTables lootManager, ResourceLocation id) {
            this.lootManager = lootManager;
            this.id = id;
        }

        /**
         * @return  access to all loot tables
         */
        public final LootTables getLootManager() {
            return this.lootManager;
        }

        /**
         * @return  the loot table id
         */
        public final ResourceLocation getId() {
            return this.id;
        }
    }

    /**
     * allows for replacing built-in {@link LootTable}s on loading
     */
    abstract class LootTablesReplaceContext extends LootTablesContext {
        /**
         * the original loot table that will be replaced
         */
        private final LootTable original;

        /**
         * @param lootManager   access to all loot tables
         * @param id            the loot table id
         * @param original      the original loot table that will be replaced
         */
        public LootTablesReplaceContext(LootTables lootManager, ResourceLocation id, LootTable original) {
            super(lootManager, id);
            this.original = original;
        }

        /**
         * @return the original loot table that will be replaced
         */
        public LootTable getLootTable() {
            return this.original;
        }

        /**
         * @param table     replacement for <code>original</code>
         */
        public abstract void setLootTable(LootTable table);
    }

    /**
     * allows changing of {@link LootPool}s in a {@link LootTable}
     */
    abstract class LootTablesModifyContext extends LootTablesContext {

        /**
         * @param lootManager   access to all loot tables
         * @param id            the loot table id
         */
        public LootTablesModifyContext(LootTables lootManager, ResourceLocation id) {
            super(lootManager, id);
        }

        /**
         * @param pool  add a {@link LootPool}
         */
        public abstract void addLootPool(LootPool pool);

        /**
         * @param index     pool to remove at index, pools are indexed starting from 0
         * @return          was removing the pool successful (has a pool at <code>index</code> been found)
         */
        public abstract boolean removeLootPool(int index);
    }
}
