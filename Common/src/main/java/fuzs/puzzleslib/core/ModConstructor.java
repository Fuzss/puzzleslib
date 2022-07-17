package fuzs.puzzleslib.core;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * a base class for a mods main common class, contains a bunch of methods for registering various things
 */
public interface ModConstructor {

    /**
     * runs when the mod is first constructed, mainly used for registering game content and configs
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
     * @param consumer add to spawn placement register
     */
    default void onRegisterSpawnPlacements(SpawnPlacementConsumer consumer) {
        consumer.register(EntityType.SKELETON, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
    }

    /**
     * allows for registering default attributes for our own entities
     * anything related to already existing entities (vanilla and modded) needs to be done in {@link #onEntityAttributeModification}
     *
     * @param consumer add to entity attribute map
     */
    default void onEntityAttributeCreation(EntityAttributeCreationConsumer consumer) {
        consumer.register(EntityType.BAT, Bat.createMobAttributes());
    }

    /**
     * allows for modifying the attributes of an already existing entity, attributes are modified individually
     *
     * @param consumer replace/add attribute to entity attribute map
     */
    default void onEntityAttributeModification(EntityAttributeModificationConsumer consumer) {
        consumer.register(EntityType.ALLAY, Attributes.ATTACK_DAMAGE, 10.0);
    }

    /**
     * allows for setting burn times for fuel items, e.g. in a furnace
     *
     * @param consumer add fuel burn time for items/blocks
     */
    default void onRegisterFuelBurnTimes(FuelBurnTimeConsumer consumer) {
        consumer.registerBlock(Blocks.BONE_BLOCK, 300);
    }

    /**
     * register a default spawn placement for entities
     */
    @FunctionalInterface
    interface SpawnPlacementConsumer {

        /**
         * registers a spawning behavior for an <code>entityType</code>
         *
         * @param entityType        the entity type
         * @param location          type of spawn placement, probably {@link SpawnPlacements.Type#ON_GROUND}
         * @param heightmap         heightmap type, probably {@link Heightmap.Types#MOTION_BLOCKING_NO_LEAVES}
         * @param spawnPredicate    custom spawn predicate for mob
         * @param <T>               type of entity
         */
        <T extends Mob> void register(EntityType<T> entityType, SpawnPlacements.Type location, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> spawnPredicate);
    }

    /**
     * register default attributes for our own entities
     */
    @FunctionalInterface
    interface EntityAttributeCreationConsumer {

        /**
         * register attributes for our own entities, modifying attributes for any other entity (vanilla or modded) should be done using {@link EntityAttributeModificationConsumer}
         *
         * @param type type of entity
         * @param builder the attribute supplier builder
         */
        void register(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder);
    }

    /**
     * registers modifications to attributes of already existing attributes
     */
    @FunctionalInterface
    interface EntityAttributeModificationConsumer {

        /**
         * use this method for modifying attributes of existing entity types
         * this overrides/adds attributes individually as this is what is done on Forge
         *
         * @param type                  type of entity
         * @param attribute             attribute to override/add
         */
        default void register(EntityType<? extends LivingEntity> type, Attribute attribute) {
            this.register(type, attribute, attribute.getDefaultValue());
        }

        /**
         * use this method for modifying attributes of existing entity types
         * this overrides/adds attributes individually as this is what is done on Forge
         *
         * @param type                  type of entity
         * @param attribute             attribute to override/add
         * @param attributeValue        new value, possibly {@link Attribute#getDefaultValue()}
         */
        void register(EntityType<? extends LivingEntity> type, Attribute attribute, double attributeValue);
    }

    /**
     * applies fuel burn times instead of implementing this on the item side
     * heavily inspired by FuelHandler found in Vazkii's Quark mod
     */
    @FunctionalInterface
    interface FuelBurnTimeConsumer {

        /**
         * @param item item to add
         * @param burnTime burn time
         */
        void registerItem(Item item, int burnTime);

        /**
         * @param block block to add
         * @param burnTime burn time
         */
        default void registerBlock(Block block, int burnTime) {
            this.registerItem(block.asItem(), burnTime);
        }

        /**
         * add wooden block with default vanilla times
         * @param block block to add
         */
        default void registerWoodenBlock(Block block) {
            this.registerBlock(block, block instanceof SlabBlock ? 150 : 300);
        }
    }
}