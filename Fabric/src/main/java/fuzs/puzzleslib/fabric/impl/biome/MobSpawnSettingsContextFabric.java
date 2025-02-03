package fuzs.puzzleslib.fabric.impl.biome;

import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.api.biome.v1.MobSpawnSettingsContext;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.utility.ReflectionHelper;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public record MobSpawnSettingsContextFabric(MobSpawnSettings mobSpawnSettings,
                                            BiomeModificationContext.SpawnSettingsContext context) implements MobSpawnSettingsContext {

    @Override
    public void setCreatureGenerationProbability(float probability) {
        this.context.setCreatureSpawnProbability(probability);
    }

    @Override
    public void addSpawn(MobCategory mobCategory, MobSpawnSettings.SpawnerData spawnerData) {
        this.context.addSpawn(mobCategory, spawnerData);
    }

    @Override
    public boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate) {
        return this.context.removeSpawns(predicate);
    }

    @Override
    public void setSpawnCost(EntityType<?> entityType, double energyBudget, double charge) {
        this.context.setSpawnCost(entityType, charge, energyBudget);
    }

    @Override
    public boolean clearSpawnCost(EntityType<?> entityType) {
        if (this.mobSpawnSettings.getMobSpawnCost(entityType) != null) {
            this.context.clearSpawnCost(entityType);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Set<MobCategory> getMobCategoriesWithSpawns() {
        // Fabric handles MobSpawnSettings$SpawnerData in its own map, use vanilla only as a fallback in case something with the api implementation changes.
        // Also note that vanilla only represents the initial state and does not reflect any changes made while the builder is 'active', so using the fallback is not desirable (it's not a view).
        // The implementation based on the fabricSpawners field also does not provide a view, which is necessary to be able to filter out empty mob categories.
        // Otherwise, the implementation would simply return MobCategory::values.
        Optional<EnumMap<MobCategory, List<MobSpawnSettings.SpawnerData>>> optional = this.getFabricSpawners();
        return optional.map(map -> map.entrySet().stream().filter(e -> !e.getValue().isEmpty()).map(Map.Entry::getKey))
                .orElseGet(() -> Stream.of(MobCategory.values())
                        .filter(mobCategory -> !this.mobSpawnSettings.getMobs(mobCategory).isEmpty()))
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public List<MobSpawnSettings.SpawnerData> getSpawnerData(MobCategory mobCategory) {
        // Fabric handles MobSpawnSettings$SpawnerData in its own map, use vanilla only as a fallback in case something with the api implementation changes.
        // Also note that vanilla only represents the initial state and does not reflect any changes made while the builder is 'active', so using the fallback is not desirable (it's not a view).
        Optional<EnumMap<MobCategory, List<MobSpawnSettings.SpawnerData>>> optional = this.getFabricSpawners();
        return optional.map(map -> Collections.unmodifiableList(map.get(mobCategory)))
                .orElseGet(() -> this.mobSpawnSettings.getMobs(mobCategory).unwrap());
    }

    private Optional<EnumMap<MobCategory, List<MobSpawnSettings.SpawnerData>>> getFabricSpawners() {
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isFabric()) {
            Field field = ReflectionHelper.findField(
                    "net.fabricmc.fabric.impl.biome.modification.BiomeModificationContextImpl$SpawnSettingsContextImpl",
                    "fabricSpawners",
                    true);
            return ReflectionHelper.getValue(field, this.context);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Set<EntityType<?>> getEntityTypesWithSpawnCost() {
        // be careful: does not provide a view, but a copy, so avoid caching this result
        return BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(entityType -> this.mobSpawnSettings.getMobSpawnCost(entityType) != null)
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
