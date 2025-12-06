package fuzs.puzzleslib.fabric.impl.biome;

import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.api.biome.v1.MobSpawnSettingsContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

public record MobSpawnSettingsContextFabric(MobSpawnSettings mobSpawnSettings,
                                            BiomeModificationContext.SpawnSettingsContext context) implements MobSpawnSettingsContext {

    @Override
    public void setCreatureGenerationProbability(float probability) {
        this.context.setCreatureSpawnProbability(probability);
    }

    @Override
    public void addSpawn(MobCategory mobCategory, int weight, MobSpawnSettings.SpawnerData spawnerData) {
        this.context.addSpawn(mobCategory, spawnerData, weight);
    }

    @Override
    public boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> filter) {
        return this.context.removeSpawns(filter);
    }

    @Override
    public void setSpawnCost(EntityType<?> entityType, double energyBudget, double charge) {
        this.context.setSpawnCost(entityType, charge, energyBudget);
    }

    @Override
    public boolean clearSpawnCost(EntityType<?> entityType) {
        if (this.getSpawnCost(entityType) != null) {
            this.context.clearSpawnCost(entityType);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Weighted<MobSpawnSettings.SpawnerData>> getSpawnerData(MobCategory mobCategory) {
        return this.context.getSpawnEntries(mobCategory);
    }

    @Override
    public Set<EntityType<?>> getEntityTypesWithSpawnCost() {
        return BuiltInRegistries.ENTITY_TYPE.stream()
                .filter((EntityType<?> entityType) -> this.getSpawnCost(entityType) != null)
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public @Nullable MobSpawnSettings.MobSpawnCost getSpawnCost(EntityType<?> entityType) {
        return this.mobSpawnSettings.getMobSpawnCost(entityType);
    }

    @Override
    public float getCreatureGenerationProbability() {
        return this.mobSpawnSettings.getCreatureProbability();
    }
}
