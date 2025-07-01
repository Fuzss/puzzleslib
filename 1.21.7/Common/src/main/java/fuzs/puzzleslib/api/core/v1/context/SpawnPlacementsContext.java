package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Register a default spawn placement for entities.
 */
@FunctionalInterface
public interface SpawnPlacementsContext {

    /**
     * Registers a spawning behavior for an entity type.
     *
     * @param entityType         the entity type
     * @param spawnPlacementType the type of spawn placement, probably
     *                           {@link net.minecraft.world.entity.SpawnPlacementTypes#ON_GROUND}
     * @param heightmapType      the heightmap type, probably {@link Heightmap.Types#MOTION_BLOCKING_NO_LEAVES}
     * @param spawnPredicate     the custom spawn predicate for the mob
     * @param <T>                the type of entity
     */
    <T extends Mob> void registerSpawnPlacement(EntityType<T> entityType, SpawnPlacementType spawnPlacementType, Heightmap.Types heightmapType, SpawnPlacements.SpawnPredicate<T> spawnPredicate);
}
