package fuzs.puzzleslib.api.core.v1.contexts;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * register a default spawn placement for entities
 */
@FunctionalInterface
public interface SpawnPlacementsContext {

    /**
     * registers a spawning behavior for an <code>entityType</code>
     *
     * @param entityType     the entity type
     * @param location       type of spawn placement, probably {@link SpawnPlacements.Type#ON_GROUND}
     * @param heightmap      heightmap type, probably {@link Heightmap.Types#MOTION_BLOCKING_NO_LEAVES}
     * @param spawnPredicate custom spawn predicate for mob
     * @param <T>            type of entity
     */
    <T extends Mob> void registerSpawnPlacement(EntityType<T> entityType, SpawnPlacements.Type location, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> spawnPredicate);
}
