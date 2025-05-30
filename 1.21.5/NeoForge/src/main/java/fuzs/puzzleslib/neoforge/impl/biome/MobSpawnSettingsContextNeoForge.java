package fuzs.puzzleslib.neoforge.impl.biome;

import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.api.biome.v1.MobSpawnSettingsContext;
import fuzs.puzzleslib.neoforge.mixin.accessor.MobSpawnSettingsBuilderNeoForgeAccessor;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.MobSpawnSettingsBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

public record MobSpawnSettingsContextNeoForge(MobSpawnSettingsBuilder context) implements MobSpawnSettingsContext {

    @Override
    public void setCreatureGenerationProbability(float probability) {
        this.context.creatureGenerationProbability(probability);
    }

    @Override
    public void addSpawn(MobCategory mobCategory, int weight, MobSpawnSettings.SpawnerData spawnerData) {
        this.context.addSpawn(mobCategory, weight, spawnerData);
    }

    @Override
    public boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> filter) {
        boolean anyRemoved = false;

        for (MobCategory mobCategory : this.context.getSpawnerTypes()) {
            if (this.context.getSpawner(mobCategory)
                    .getList()
                    .removeIf((Weighted<MobSpawnSettings.SpawnerData> spawnerData) -> {
                        return filter.test(mobCategory, spawnerData.value());
                    })) {
                anyRemoved = true;
            }
        }

        return anyRemoved;
    }

    @Override
    public void setSpawnCost(EntityType<?> entityType, double energyBudget, double charge) {
        this.context.addMobCharge(entityType, charge, energyBudget);
    }

    @Override
    public boolean clearSpawnCost(EntityType<?> entityType) {
        return ((MobSpawnSettingsBuilderNeoForgeAccessor) this.context).puzzleslib$getMobSpawnCosts()
                .remove(entityType) != null;
    }

    @Override
    public Set<MobCategory> getMobCategoriesWithSpawns() {
        // This implementation does not provide a view, which is necessary to be able to filter out empty mob categories.
        // Otherwise, the implementation would simply return MobCategory::values.
        return this.context.getSpawnerTypes()
                .stream()
                .filter((MobCategory mobCategory) -> !this.context.getSpawner(mobCategory).getList().isEmpty())
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public List<Weighted<MobSpawnSettings.SpawnerData>> getSpawnerData(MobCategory mobCategory) {
        return this.context.getSpawner(mobCategory).getList();
    }

    @Override
    public Set<EntityType<?>> getEntityTypesWithSpawnCost() {
        return this.context.getEntityTypes();
    }

    @Override
    public @Nullable MobSpawnSettings.MobSpawnCost getSpawnCost(EntityType<?> entityType) {
        return this.context.getCost(entityType);
    }

    @Override
    public float getCreatureGenerationProbability() {
        return this.context.getProbability();
    }
}
