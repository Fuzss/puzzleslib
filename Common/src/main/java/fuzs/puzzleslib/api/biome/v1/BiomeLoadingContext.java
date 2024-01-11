package fuzs.puzzleslib.api.biome.v1;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.Optional;

/**
 * Context given to a biome selector for deciding whether it applies to a biome or not.
 *
 * <p>Mostly copied from Fabric API's Biome API, specifically <code>net.fabricmc.fabric.api.biome.v1.BiomeModificationContext</code>
 * to allow for use in common project and to allow reimplementation on Forge using Forge's native biome modification system.
 *
 * <p>Copyright (c) FabricMC
 * <p>SPDX-License-Identifier: Apache-2.0
 */
public interface BiomeLoadingContext {

    /**
     * Returns the key used to represent this biome in the dynamic biome registry.
     */
    ResourceKey<Biome> getResourceKey();

    /**
     * Returns the biome with modifications by biome modifiers of higher priority already applied.
     */
    Biome getBiome();

    /**
     * Returns this biome wrapped in a holder.
     */
    Holder<Biome> holder();

    /**
     * Returns true if this biome contains a placed feature referencing a configured feature with the given key.
     */
    default boolean hasFeature(ResourceKey<ConfiguredFeature<?, ?>> key) {
        List<HolderSet<PlacedFeature>> featureSteps = this.getBiome().getGenerationSettings().features();

        for (HolderSet<PlacedFeature> featureSuppliers : featureSteps) {
            for (Holder<PlacedFeature> featureSupplier : featureSuppliers) {
                if (featureSupplier.value().getFeatures().anyMatch(cf -> this.getFeatureKey(cf).orElse(null) == key)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true if this biome contains a placed feature with the given key.
     */
    default boolean hasPlacedFeature(ResourceKey<PlacedFeature> key) {
        List<HolderSet<PlacedFeature>> featureSteps = this.getBiome().getGenerationSettings().features();

        for (HolderSet<PlacedFeature> featureSuppliers : featureSteps) {
            for (Holder<PlacedFeature> featureSupplier : featureSuppliers) {
                if (this.getPlacedFeatureKey(featureSupplier.value()).orElse(null) == key) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Tries to retrieve the registry key for the given configured feature, which should be from this biomes
     * current feature list. May be empty if the configured feature is not registered, or does not come
     * from this biomes feature list.
     */
    Optional<ResourceKey<ConfiguredFeature<?, ?>>> getFeatureKey(ConfiguredFeature<?, ?> configuredFeature);

    /**
     * Tries to retrieve the registry key for the given placed feature, which should be from this biomes
     * current feature list. May be empty if the placed feature is not registered, or does not come
     * from this biomes feature list.
     */
    Optional<ResourceKey<PlacedFeature>> getPlacedFeatureKey(PlacedFeature placedFeature);

    /**
     * Returns true if the configured structure with the given key can start in this biome in any chunk generator
     * used by the current world-save.
     */
    boolean validForStructure(ResourceKey<ConfiguredStructureFeature<?, ?>> key);

    /**
     * Tries to retrieve the registry key for the given configured feature, which should be from this biomes
     * current structure list. May be empty if the configured feature is not registered, or does not come
     * from this biomes feature list.
     */
    Optional<ResourceKey<ConfiguredStructureFeature<?, ?>>> getStructureKey(ConfiguredStructureFeature<?, ?> structureFeature);

    /**
     * Tries to determine whether this biome generates in a specific dimension, based on the {@link net.minecraft.world.level.levelgen.WorldGenSettings}
     * used by the current world-save.
     *
     * <p>If no dimension options exist for the given dimension key, <code>false</code> is returned.
     */
    boolean canGenerateIn(ResourceKey<LevelStem> dimensionKey);

    /**
     * @param tag tag key to check for biome
     * @return is the biome in this context contained in the given <code>tag</code> key
     */
    boolean is(TagKey<Biome> tag);

    /**
     * @param biome biome to check for equality
     * @return is the biome in this context equal to the given <code>biome</code>
     */
    default boolean is(Biome biome) {
        return this.getBiome() == biome;
    }

    /**
     * @param holder holder to check for equality
     * @return is the holder for the biome in this context equal to the given <code>holder</code>
     */
    default boolean is(Holder<Biome> holder) {
        return this.holder() == holder;
    }

    /**
     * @param resourceKey resourceKey to check for equality
     * @return is the resource key for the biome in this context equal to the given <code>resourceKey</code>
     */
    default boolean is(ResourceKey<Biome> resourceKey) {
        return this.getResourceKey() == resourceKey;
    }
}
