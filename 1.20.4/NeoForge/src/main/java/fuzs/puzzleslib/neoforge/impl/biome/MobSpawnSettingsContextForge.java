package fuzs.puzzleslib.neoforge.impl.biome;

import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.api.biome.v1.MobSpawnSettingsContext;
import fuzs.puzzleslib.neoforge.mixin.accessor.MobSpawnSettingsBuilderForgeAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.MobSpawnSettingsBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

public record MobSpawnSettingsContextForge(MobSpawnSettingsBuilder context) implements MobSpawnSettingsContext {

    @Override
    public void setCreatureGenerationProbability(float probability) {
        this.context.creatureGenerationProbability(probability);
    }

    @Override
    public void addSpawn(MobCategory spawnGroup, MobSpawnSettings.SpawnerData spawnEntry) {
        this.context.addSpawn(spawnGroup, spawnEntry);
    }

    @Override
    public boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate) {
        boolean anyRemoved = false;

        for (MobCategory group : this.context.getSpawnerTypes()) {
            if (this.context.getSpawner(group).removeIf(entry -> predicate.test(group, entry))) {
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
        return ((MobSpawnSettingsBuilderForgeAccessor) this.context).puzzleslib$getMobSpawnCosts().remove(entityType) != null;
    }

    @Override
    public Set<MobCategory> getMobCategoriesWithSpawns() {
        // This implementation does not provide a view, which is necessary to be able to filter out empty mob categories.
        // Otherwise, the implementation would simply return MobCategory::values.
        return this.context.getSpawnerTypes().stream().filter(mobCategory -> !this.context.getSpawner(mobCategory).isEmpty()).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public List<MobSpawnSettings.SpawnerData> getSpawnerData(MobCategory type) {
        return this.context.getSpawner(type);
    }

    @Override
    public Set<EntityType<?>> getEntityTypesWithSpawnCost() {
        return this.context.getEntityTypes();
    }

    @Override
    public @Nullable MobSpawnSettings.MobSpawnCost getSpawnCost(EntityType<?> type) {
        return this.context.getCost(type);
    }

    @Override
    public float getCreatureGenerationProbability() {
        return this.context.getProbability();
    }
}
