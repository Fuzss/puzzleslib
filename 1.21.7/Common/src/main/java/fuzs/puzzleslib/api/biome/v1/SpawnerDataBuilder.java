package fuzs.puzzleslib.api.biome.v1;

import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.apache.commons.lang3.math.Fraction;

import java.util.Objects;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntFunction;

/**
 * A builder for configuring mob spawner data in biome spawn settings.
 */
public final class SpawnerDataBuilder {
    private final MobSpawnSettingsContext context;
    private final EntityType<?> entityType;
    private IntUnaryOperator weightMapper = IntUnaryOperator.identity();
    private ToIntFunction<MobSpawnSettings.SpawnerData> minCountMapper = MobSpawnSettings.SpawnerData::minCount;
    private ToIntFunction<MobSpawnSettings.SpawnerData> maxCountMapper = MobSpawnSettings.SpawnerData::maxCount;

    private SpawnerDataBuilder(MobSpawnSettingsContext context, EntityType<?> entityType) {
        Objects.requireNonNull(context, "context is null");
        Objects.requireNonNull(entityType, "entity type is null");
        this.context = context;
        this.entityType = entityType;
    }

    /**
     * Creates a new spawner data builder.
     *
     * @param context context for mob spawn settings
     * @param entityType the entity type to configure spawning for
     * @return the builder
     */
    public static SpawnerDataBuilder create(MobSpawnSettingsContext context, EntityType<?> entityType) {
        return new SpawnerDataBuilder(context, entityType);
    }

    /**
     * @param weight spawn weight for the entity
     * @return the builder
     */
    public SpawnerDataBuilder setWeight(int weight) {
        return this.setWeight((int oldWeight) -> weight);
    }

    /**
     * @param weight fractional spawn weight for the entity
     * @return the builder
     */
    public SpawnerDataBuilder setWeight(Fraction weight) {
        Objects.requireNonNull(weight, "weight is null");
        return this.setWeight((int oldWeight) -> weight.multiplyBy(Fraction.getFraction(oldWeight, 1)).intValue());
    }

    /**
     * @param weight custom weight mapping function
     * @return the builder
     */
    public SpawnerDataBuilder setWeight(IntUnaryOperator weight) {
        Objects.requireNonNull(weight, "weight is null");
        this.weightMapper = (int oldWeight) -> Math.max(1, weight.applyAsInt(oldWeight));
        return this;
    }

    /**
     * @param minCount minimum spawn count
     * @return the builder
     */
    public SpawnerDataBuilder setMinCount(int minCount) {
        return this.setMinCount((MobSpawnSettings.SpawnerData spawnerData) -> minCount);
    }

    /**
     * @param minCount fractional minimum spawn count
     * @return the builder
     */
    public SpawnerDataBuilder setMinCount(Fraction minCount) {
        Objects.requireNonNull(minCount, "min count is null");
        return this.setMinCount((MobSpawnSettings.SpawnerData spawnerData) -> minCount.multiplyBy(Fraction.getFraction(
                spawnerData.minCount(),
                1)).intValue());
    }

    /**
     * @param minCount custom minimum count mapping function
     * @return the builder
     */
    public SpawnerDataBuilder setMinCount(ToIntFunction<MobSpawnSettings.SpawnerData> minCount) {
        Objects.requireNonNull(minCount, "min count is null");
        this.minCountMapper = (MobSpawnSettings.SpawnerData spawnerData) -> Math.max(1,
                minCount.applyAsInt(spawnerData));
        return this;
    }

    /**
     * @param maxCount maximum spawn count
     * @return the builder
     */
    public SpawnerDataBuilder setMaxCount(int maxCount) {
        return this.setMaxCount((MobSpawnSettings.SpawnerData spawnerData) -> maxCount);
    }

    /**
     * @param maxCount fractional maximum spawn count
     * @return the builder
     */
    public SpawnerDataBuilder setMaxCount(Fraction maxCount) {
        Objects.requireNonNull(maxCount, "max count is null");
        return this.setMaxCount((MobSpawnSettings.SpawnerData spawnerData) -> maxCount.multiplyBy(Fraction.getFraction(
                spawnerData.maxCount(),
                1)).intValue());
    }

    /**
     * @param maxCount custom maximum count mapping function
     * @return the builder
     */
    public SpawnerDataBuilder setMaxCount(ToIntFunction<MobSpawnSettings.SpawnerData> maxCount) {
        Objects.requireNonNull(maxCount, "max count is null");
        this.maxCountMapper = (MobSpawnSettings.SpawnerData spawnerData) -> Math.max(1,
                maxCount.applyAsInt(spawnerData));
        return this;
    }

    /**
     * Applies spawner data to the specified entity type.
     *
     * @param entityType the entity type to apply spawner data to
     */
    public void apply(EntityType<?> entityType) {
        for (MobCategory mobCategory : this.context.getMobCategoriesWithSpawns()) {
            this.getSpawnerDataForType(this.context, mobCategory, this.entityType)
                    .ifPresent((Weighted<MobSpawnSettings.SpawnerData> spawnerData) -> {
                        int weight = this.weightMapper.applyAsInt(spawnerData.weight());
                        int minCount = this.minCountMapper.applyAsInt(spawnerData.value());
                        int maxCount = this.maxCountMapper.applyAsInt(spawnerData.value());
                        this.context.addSpawn(mobCategory,
                                weight,
                                new MobSpawnSettings.SpawnerData(entityType, Math.min(minCount, maxCount), maxCount));
                    });
        }
    }

    private Optional<Weighted<MobSpawnSettings.SpawnerData>> getSpawnerDataForType(MobSpawnSettingsContext context, MobCategory mobCategory, EntityType<?> entityType) {
        return context.getSpawnerData(mobCategory)
                .stream()
                .filter((Weighted<MobSpawnSettings.SpawnerData> spawnerData) -> spawnerData.value().type() ==
                        entityType)
                .findAny();
    }
}
