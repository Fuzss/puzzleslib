package fuzs.puzzleslib.api.biome.v1;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

public interface MobSpawnSettingsContext {

    /**
     * Associated JSON property: <code>creature_spawn_probability</code>.
     *
     * @see MobSpawnSettings#getCreatureProbability()
     * @see MobSpawnSettings.Builder#creatureGenerationProbability(float)
     */
    void setCreatureSpawnProbability(float probability);

    /**
     * Associated JSON property: <code>spawners</code>.
     *
     * @see MobSpawnSettings#getMobs(MobCategory)
     * @see MobSpawnSettings.Builder#addSpawn(MobCategory, MobSpawnSettings.SpawnerData)
     */
    void addSpawn(MobCategory spawnGroup, MobSpawnSettings.SpawnerData spawnEntry);

    /**
     * Removes any spawns matching the given predicate from this biome, and returns true if any matched.
     *
     * <p>Associated JSON property: <code>spawners</code>.
     */
    boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate);

    /**
     * Removes all spawns of the given entity type.
     *
     * <p>Associated JSON property: <code>spawners</code>.
     *
     * @return True if any spawns were removed.
     */
    default boolean removeSpawnsOfEntityType(EntityType<?> entityType) {
        return this.removeSpawns((spawnGroup, spawnEntry) -> spawnEntry.type == entityType);
    }

    /**
     * Removes all spawns of the given spawn group.
     *
     * <p>Associated JSON property: <code>spawners</code>.
     */
    default void clearSpawns(MobCategory group) {
        this.removeSpawns((spawnGroup, spawnEntry) -> spawnGroup == group);
    }

    /**
     * Removes all spawns.
     *
     * <p>Associated JSON property: <code>spawners</code>.
     */
    default void clearSpawns() {
        this.removeSpawns((spawnGroup, spawnEntry) -> true);
    }

    /**
     * Associated JSON property: <code>spawn_costs</code>.
     *
     * @see MobSpawnSettings#getMobSpawnCost(EntityType)
     * @see MobSpawnSettings.Builder#addMobCharge(EntityType, double, double)
     */
    void setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit);

    /**
     * Removes a spawn cost entry for a given entity type.
     *
     * <p>Associated JSON property: <code>spawn_costs</code>.
     *
     * @return
     */
    boolean clearSpawnCost(EntityType<?> entityType);

    Set<MobCategory> getSpawnerMobCategories();

    List<MobSpawnSettings.SpawnerData> getSpawnerData(MobCategory type);

    Set<EntityType<?>> getSpawnCostEntityTypes();

    @Nullable MobSpawnSettings.MobSpawnCost getMobSpawnCost(EntityType<?> type);

    /**
     * Associated JSON property: <code>creature_spawn_probability</code>.
     *
     * @see MobSpawnSettings#getCreatureProbability()
     * @see MobSpawnSettings.Builder#creatureGenerationProbability(float)
     */
    float getCreatureSpawnProbability();
}
