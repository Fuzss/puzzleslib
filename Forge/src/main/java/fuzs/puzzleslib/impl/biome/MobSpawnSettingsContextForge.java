package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.MobSpawnSettingsContext;
import fuzs.puzzleslib.mixin.accessor.MobSpawnSettingsBuilderForgeAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

public class MobSpawnSettingsContextForge implements MobSpawnSettingsContext {
    private final MobSpawnSettingsBuilder context;

    public MobSpawnSettingsContextForge(MobSpawnSettingsBuilder context) {
        this.context = context;
    }

    @Override
    public void setCreatureSpawnProbability(float probability) {
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
    public void setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit) {
        this.context.addMobCharge(entityType, mass, gravityLimit);
    }

    @Override
    public boolean clearSpawnCost(EntityType<?> entityType) {
        return ((MobSpawnSettingsBuilderForgeAccessor) this.context).puzzleslib$getMobSpawnCosts().remove(entityType) != null;
    }

    @Override
    public Set<MobCategory> getSpawnerMobCategories() {
        return this.context.getSpawnerTypes();
    }

    @Override
    public List<MobSpawnSettings.SpawnerData> getSpawnerData(MobCategory type) {
        return this.context.getSpawner(type);
    }

    @Override
    public Set<EntityType<?>> getSpawnCostEntityTypes() {
        return this.context.getEntityTypes();
    }

    @Override
    public @Nullable MobSpawnSettings.MobSpawnCost getMobSpawnCost(EntityType<?> type) {
        return this.context.getCost(type);
    }

    @Override
    public float getCreatureSpawnProbability() {
        return this.context.getProbability();
    }
}
