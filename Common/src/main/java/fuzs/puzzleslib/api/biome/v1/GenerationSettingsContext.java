package fuzs.puzzleslib.api.biome.v1;

import net.minecraft.core.Holder;
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
     * Adds a feature to one of this biomes generation steps, identified by the placed feature's registry key.
     */
    void addFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey);

    /**
     * Adds a configured carver to one of this biomes generation steps.
     */
    void addCarver(GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> carverKey);

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
