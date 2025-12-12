package fuzs.puzzleslib.api.biome.v1;

import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * The modification context for the biomes effects.
 *
 * <p>Mostly copied from Fabric API's Biome API, specifically
 * <code>net.fabricmc.fabric.api.biome.v1.BiomeModificationContext$GenerationSettingsContext</code>
 * to allow for use in common project and to allow reimplementation on Forge using Forge's native biome modification
 * system.
 *
 * <p>Copyright (c) FabricMC
 * <p>SPDX-License-Identifier: Apache-2.0
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public interface SpecialEffectsContext {

    /**
     * @see BiomeSpecialEffects#waterColor()
     * @see BiomeSpecialEffects.Builder#waterColor(int)
     */
    void setWaterColor(int waterColor);

    /**
     * @see BiomeSpecialEffects#waterColor()
     * @see BiomeSpecialEffects.Builder#waterColor(int)
     */
    int getWaterColor();

    /**
     * @see BiomeSpecialEffects#foliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    void setFoliageColorOverride(Optional<Integer> foliageColorOverride);

    /**
     * @see BiomeSpecialEffects#foliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    Optional<Integer> getFoliageColorOverride();

    /**
     * @see BiomeSpecialEffects#foliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    default void setFoliageColorOverride(int foliageColorOverride) {
        this.setFoliageColorOverride(Optional.of(foliageColorOverride));
    }

    /**
     * @see BiomeSpecialEffects#foliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    default void clearFoliageColorOverride() {
        this.setFoliageColorOverride(Optional.empty());
    }

    /**
     * @see BiomeSpecialEffects#dryFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#dryFoliageColorOverride(int)
     */
    void setDryFoliageColorOverride(Optional<Integer> dryFoliageColorOverride);

    /**
     * @see BiomeSpecialEffects#dryFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#dryFoliageColorOverride(int)
     */
    Optional<Integer> getDryFoliageColorOverride();

    /**
     * @see BiomeSpecialEffects#dryFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#dryFoliageColorOverride(int)
     */
    default void setDryFoliageColorOverride(int dryFoliageColorOverride) {
        this.setDryFoliageColorOverride(Optional.of(dryFoliageColorOverride));
    }

    /**
     * @see BiomeSpecialEffects#dryFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#dryFoliageColorOverride(int)
     */
    default void clearDryFoliageColorOverride() {
        this.setDryFoliageColorOverride(Optional.empty());
    }

    /**
     * @see BiomeSpecialEffects#grassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    void setGrassColorOverride(Optional<Integer> grassColorOverride);

    /**
     * @see BiomeSpecialEffects#grassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    Optional<Integer> getGrassColorOverride();

    /**
     * @see BiomeSpecialEffects#grassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    default void setGrassColorOverride(int grassColorOverride) {
        this.setGrassColorOverride(Optional.of(grassColorOverride));
    }

    /**
     * @see BiomeSpecialEffects#grassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    default void clearGrassColorOverride() {
        this.setGrassColorOverride(Optional.empty());
    }

    /**
     * @see BiomeSpecialEffects#grassColorModifier()
     * @see BiomeSpecialEffects.Builder#grassColorModifier(BiomeSpecialEffects.GrassColorModifier)
     */
    void setGrassColorModifier(BiomeSpecialEffects.@NonNull GrassColorModifier grassColorModifier);

    /**
     * @see BiomeSpecialEffects#grassColorModifier()
     * @see BiomeSpecialEffects.Builder#grassColorModifier(BiomeSpecialEffects.GrassColorModifier)
     */
    BiomeSpecialEffects.GrassColorModifier getGrassColorModifier();
}
