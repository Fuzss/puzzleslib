package fuzs.puzzleslib.api.biome.v1;

import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * The modification context for the biomes generation settings.
 *
 * <p>Mostly copied from Fabric API's Biome API, specifically <code>net.fabricmc.fabric.api.biome.v1.BiomeModificationContext$WeatherContext</code>
 * to allow for use in common project and to allow reimplementation on Forge using Forge's native biome modification system.
 *
 * <p>Copyright (c) FabricMC
 * <p>SPDX-License-Identifier: Apache-2.0
 */
public interface GenerationSettingsContext {
    /**
     * Removes a feature from one of this biomes generation steps, and returns if any features were removed.
     */
    boolean removeFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey);

    /**
     * Removes a feature from all of this biomes generation steps, and returns if any features were removed.
     */
    default boolean removeFeature(ResourceKey<PlacedFeature> featureKey) {
        boolean anyFound = false;

        for (GenerationStep.Decoration step : GenerationStep.Decoration.values()) {
            if (this.removeFeature(step, featureKey)) {
                anyFound = true;
            }
        }

        return anyFound;
    }

    /**
     * {@link #removeFeature(ResourceKey)} for built-in features (see {@link #addBuiltInFeature(GenerationStep.Decoration, PlacedFeature)}).
     */
    default boolean removeBuiltInFeature(PlacedFeature feature) {
        return this.removeFeature(BuiltinRegistries.PLACED_FEATURE.getResourceKey(feature).orElseThrow());
    }

    /**
     * {@link #removeFeature(GenerationStep.Decoration, ResourceKey)} for built-in features (see {@link #addBuiltInFeature(GenerationStep.Decoration, PlacedFeature)}).
     */
    default boolean removeBuiltInFeature(GenerationStep.Decoration step, PlacedFeature feature) {
        return this.removeFeature(step, BuiltinRegistries.PLACED_FEATURE.getResourceKey(feature).orElseThrow());
    }

    /**
     * Adds a feature to one of this biomes generation steps, identified by the placed feature's registry key.
     *
     * @see BuiltinRegistries#PLACED_FEATURE
     */
    void addFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey);

    /**
     * Adds a placed feature from {@link BuiltinRegistries#PLACED_FEATURE} to this biome.
     *
     * <p>This method is intended for use with the placed features found in
     * classes such as {@link net.minecraft.data.worldgen.placement.OrePlacements}.
     *
     * <p><b>NOTE:</b> In case the placed feature is overridden in a datapack, the datapacks version
     * will be used.
     */
    default void addBuiltInFeature(GenerationStep.Decoration step, PlacedFeature feature) {
        this.addFeature(step, BuiltinRegistries.PLACED_FEATURE.getResourceKey(feature).orElseThrow());
    }

    /**
     * Adds a configured carver to one of this biomes generation steps.
     */
    void addCarver(GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> carverKey);

    /**
     * Adds a configured carver from {@link BuiltinRegistries#CONFIGURED_CARVER} to this biome.
     *
     * <p>This method is intended for use with the configured carvers found in {@link net.minecraft.data.worldgen.Carvers}.
     *
     * <p><b>NOTE:</b> In case the configured carver is overridden in a datapack, the datapacks version
     * will be used.
     */
    default void addBuiltInCarver(GenerationStep.Carving step, ConfiguredWorldCarver<?> carver) {
        this.addCarver(step, BuiltinRegistries.CONFIGURED_CARVER.getResourceKey(carver).orElseThrow());
    }

    /**
     * Removes all carvers with the given key from one of this biomes generation steps.
     *
     * @return True if any carvers were removed.
     */
    boolean removeCarver(GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> carverKey);

    /**
     * Removes all carvers with the given key from all of this biomes generation steps.
     *
     * @return True if any carvers were removed.
     */
    default boolean removeCarver(ResourceKey<ConfiguredWorldCarver<?>> carverKey) {
        boolean anyFound = false;

        for (GenerationStep.Carving step : GenerationStep.Carving.values()) {
            if (this.removeCarver(step, carverKey)) {
                anyFound = true;
            }
        }

        return anyFound;
    }

    /**
     * {@link #removeCarver(ResourceKey)} for built-in carvers (see {@link #addBuiltInCarver(GenerationStep.Carving, ConfiguredWorldCarver)}).
     */
    default boolean removeBuiltInCarver(ConfiguredWorldCarver<?> carver) {
        return this.removeCarver(BuiltinRegistries.CONFIGURED_CARVER.getResourceKey(carver).orElseThrow());
    }

    /**
     * {@link #removeCarver(GenerationStep.Carving, ResourceKey)} for built-in carvers (see {@link #addBuiltInCarver(GenerationStep.Carving, ConfiguredWorldCarver)}).
     */
    default boolean removeBuiltInCarver(GenerationStep.Carving step, ConfiguredWorldCarver<?> carver) {
        return this.removeCarver(step, BuiltinRegistries.CONFIGURED_CARVER.getResourceKey(carver).orElseThrow());
    }

    /**
     * @param stage decoration stage
     * @return all features registered for the given <code>stage</code>
     */
    Iterable<Holder<PlacedFeature>> getFeatures(GenerationStep.Decoration stage);

    /**
     * @param stage carving stage
     * @return all carvers registered for the given <code>stage</code>
     */
    Iterable<Holder<ConfiguredWorldCarver<?>>> getCarvers(GenerationStep.Carving stage);
}
