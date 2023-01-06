package fuzs.puzzleslib.impl.biome;

import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.api.biome.v1.MobSpawnSettingsContext;
import fuzs.puzzleslib.core.ReflectionHelperV2;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public class MobSpawnSettingsContextFabric implements MobSpawnSettingsContext {
    private final MobSpawnSettings mobSpawnSettings;
    private final BiomeModificationContext.SpawnSettingsContext context;

    public MobSpawnSettingsContextFabric(MobSpawnSettings mobSpawnSettings, BiomeModificationContext.SpawnSettingsContext context) {
        this.mobSpawnSettings = mobSpawnSettings;
        this.context = context;
    }

    @Override
    public void setCreatureSpawnProbability(float probability) {
        this.context.setCreatureSpawnProbability(probability);
    }

    @Override
    public void addSpawn(MobCategory spawnGroup, MobSpawnSettings.SpawnerData spawnEntry) {
        this.context.addSpawn(spawnGroup, spawnEntry);
    }

    @Override
    public boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate) {
        return this.context.removeSpawns(predicate);
    }

    @Override
    public void setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit) {
        this.context.setSpawnCost(entityType, mass, gravityLimit);
    }

    @Override
    public boolean clearSpawnCost(EntityType<?> entityType) {
        if (this.mobSpawnSettings.getMobSpawnCost(entityType) != null) {
            this.context.clearSpawnCost(entityType);
            return true;
        }
        return false;
    }

    @Override
    public Set<MobCategory> getSpawnerMobCategories() {
        // Fabric handles MobSpawnSettings$SpawnerData in its own map, use vanilla only as a fallback in case something with the api implementation changes
        // also note that vanilla only represents the initial state and does not reflect any changes, so using the fallback is not desirable
        Optional<EnumMap<MobCategory, List<MobSpawnSettings.SpawnerData>>> optional = this.getFabricSpawners();
        return optional.map(map -> Collections.unmodifiableSet(map.keySet())).orElseGet(() -> Stream.of(MobCategory.values()).filter(mobCategory -> !this.mobSpawnSettings.getMobs(mobCategory).isEmpty()).collect(ImmutableSet.toImmutableSet()));
    }

    @Override
    public List<MobSpawnSettings.SpawnerData> getSpawnerData(MobCategory type) {
        // Fabric handles MobSpawnSettings$SpawnerData in its own map, use vanilla only as a fallback in case something with the api implementation changes
        // also note that vanilla only represents the initial state and does not reflect any changes, so using the fallback is not desirable
        Optional<EnumMap<MobCategory, List<MobSpawnSettings.SpawnerData>>> optional = this.getFabricSpawners();
        return optional.map(map -> Collections.unmodifiableList(map.get(type))).orElseGet(() -> this.mobSpawnSettings.getMobs(type).unwrap());
    }

    private Optional<EnumMap<MobCategory, List<MobSpawnSettings.SpawnerData>>> getFabricSpawners() {
        Field field = ReflectionHelperV2.findField("net.fabricmc.fabric.impl.biome.modification.BiomeModificationContextImpl$SpawnSettingsContextImpl", "fabricSpawners", true);
        return ReflectionHelperV2.getValue(field, this.context);
    }

    @Override
    public Set<EntityType<?>> getSpawnCostEntityTypes() {
        // be careful: does not provide a view
        return Registry.ENTITY_TYPE.stream().filter(entityType -> this.mobSpawnSettings.getMobSpawnCost(entityType) != null).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public @Nullable MobSpawnSettings.MobSpawnCost getMobSpawnCost(EntityType<?> type) {
        return this.mobSpawnSettings.getMobSpawnCost(type);
    }

    @Override
    public float getCreatureSpawnProbability() {
        return this.mobSpawnSettings.getCreatureProbability();
    }
}
