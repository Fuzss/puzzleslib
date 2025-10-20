package fuzs.puzzleslib.neoforge.impl.biome;

import fuzs.puzzleslib.api.biome.v1.MobSpawnSettingsContext;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.MobSpawnSettingsBuilder;
import org.apache.commons.lang3.mutable.MutableBoolean;
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
        MutableBoolean mutableBoolean = new MutableBoolean();
        for (MobCategory mobCategory : this.context.getSpawnerTypes()) {
            for (Weighted<MobSpawnSettings.SpawnerData> spawnerData : this.context.getSpawner(mobCategory).getList()) {
                if (filter.test(mobCategory, spawnerData.value())) {
                    mutableBoolean.setTrue();
                    break;
                }
            }

            this.context.getSpawner(mobCategory).removeIf((Weighted<MobSpawnSettings.SpawnerData> spawnerData) -> {
                return filter.test(mobCategory, spawnerData.value());
            });
        }

        return mutableBoolean.isTrue();
    }

    @Override
    public void setSpawnCost(EntityType<?> entityType, double energyBudget, double charge) {
        this.context.addMobCharge(entityType, charge, energyBudget);
    }

    @Override
    public boolean clearSpawnCost(EntityType<?> entityType) {
        if (this.getSpawnCost(entityType) != null) {
            this.context.removeSpawnCost(entityType);
            return true;
        } else {
            return false;
        }
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
