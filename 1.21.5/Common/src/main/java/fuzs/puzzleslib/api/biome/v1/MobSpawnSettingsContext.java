package fuzs.puzzleslib.api.biome.v1;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * The modification context for the biomes spawn settings.
 *
 * <p>Mostly copied from Fabric API's Biome API, specifically
 * <code>net.fabricmc.fabric.api.biome.v1.BiomeModificationContext$SpawnSettingsContext</code>
 * to allow for use in common project and to allow reimplementation on Forge using Forge's native biome modification
 * system.
 *
 * <p>Copyright (c) FabricMC
 * <p>SPDX-License-Identifier: Apache-2.0
 */
public interface MobSpawnSettingsContext {

    /**
     * Associated JSON property: <code>spawners</code>.
     *
     * @see MobSpawnSettings#getMobs(MobCategory)
     * @see MobSpawnSettings.Builder#addSpawn(MobCategory, MobSpawnSettings.SpawnerData)
     */
    void addSpawn(MobCategory mobCategory, MobSpawnSettings.SpawnerData spawnerData);

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
    default void clearSpawns(MobCategory mobCategory) {
        this.removeSpawns((spawnGroup, spawnEntry) -> spawnGroup == mobCategory);
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
     * @param entityType   the entity type
     * @param energyBudget a tolerance level for how close other spawns can happen nearby, like an aura; the higher this
     *                     value is the closer mobs can spawn together, usually <code>0.15</code> in vanilla
     * @param charge       the strength of a spawn aura, defines how far away other spawn attempts are affected, usually
     *                     <code>0.7</code> in vanilla
     * @see MobSpawnSettings#getMobSpawnCost(EntityType)
     * @see MobSpawnSettings.Builder#addMobCharge(EntityType, double, double)
     * @see <a
     *         href="https://www.reddit.com/r/minecraft_configs/comments/idmyyr/so_how_does_spawn_costs_actuallywork/">Reddit:
     *         So how does spawn_costs actually...work?</a>
     */
    void setSpawnCost(EntityType<?> entityType, double energyBudget, double charge);

    /**
     * Removes a spawn cost entry for a given entity type.
     *
     * <p>Associated JSON property: <code>spawn_costs</code>.
     */
    boolean clearSpawnCost(EntityType<?> entityType);

    /**
     * @return all {@link MobCategory}s that have any spawns registered for them
     */
    Set<MobCategory> getMobCategoriesWithSpawns();

    /**
     * @param mobCategory mob category
     * @return all {@link net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData} registered for the given
     *         <code>type</code>
     */
    List<MobSpawnSettings.SpawnerData> getSpawnerData(MobCategory mobCategory);

    /**
     * @return all {@link EntityType}s with a registered spawn cost
     */
    Set<EntityType<?>> getEntityTypesWithSpawnCost();

    /**
     * @param entityType entity type
     * @return the {@link net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost} for the given
     *         <code>type</code>
     */
    @Nullable MobSpawnSettings.MobSpawnCost getSpawnCost(EntityType<?> entityType);

    /**
     * Associated JSON property: <code>creature_spawn_probability</code>.
     *
     * @see MobSpawnSettings#getCreatureProbability()
     * @see MobSpawnSettings.Builder#creatureGenerationProbability(float)
     */
    float getCreatureGenerationProbability();

    /**
     * Associated JSON property: <code>creature_spawn_probability</code>.
     *
     * @see MobSpawnSettings#getCreatureProbability()
     * @see MobSpawnSettings.Builder#creatureGenerationProbability(float)
     */
    void setCreatureGenerationProbability(float probability);
}
