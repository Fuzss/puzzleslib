package fuzs.puzzleslib.core;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * a collection of utility methods for registering common content that doesn't use a {@link net.minecraft.core.Registry}
 */
@Deprecated(forRemoval = true)
public interface CommonRegistration {

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

    /**
     * register attributes for our own entities, modifying attributes for any other entity (vanilla or modded) should be done using {@link #modifyEntityAttribute}
     *
     * @param type type of entity
     * @param builder the attribute supplier builder
     */
    void registerEntityAttribute(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder);

    /**
     * use this method for modifying attributes of existing entity types
     * this overrides/adds attributes individually as this is what is done on Forge
     *
     * @param type                  type of entity
     * @param attribute             attribute to override/add
     */
    default void modifyEntityAttribute(EntityType<? extends LivingEntity> type, Attribute attribute) {
        this.modifyEntityAttribute(type, attribute, attribute.getDefaultValue());
    }

    /**
     * use this method for modifying attributes of existing entity types
     * this overrides/adds attributes individually as this is what is done on Forge
     *
     * @param type                  type of entity
     * @param attribute             attribute to override/add
     * @param attributeValue        new value, possibly {@link Attribute#getDefaultValue()}
     */
    void modifyEntityAttribute(EntityType<? extends LivingEntity> type, Attribute attribute, double attributeValue);
}
