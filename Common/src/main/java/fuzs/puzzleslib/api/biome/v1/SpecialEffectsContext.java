package fuzs.puzzleslib.api.biome.v1;

import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

import java.util.Objects;
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
     * @see BiomeSpecialEffects#getFogColor()
     * @see BiomeSpecialEffects.Builder#fogColor(int)
     */
    void setFogColor(int fogColor);

    /**
     * @see BiomeSpecialEffects#getFogColor()
     * @see BiomeSpecialEffects.Builder#fogColor(int)
     */
    int getFogColor();

    /**
     * @see BiomeSpecialEffects#getWaterColor()
     * @see BiomeSpecialEffects.Builder#waterColor(int)
     */
    void setWaterColor(int waterColor);

    /**
     * @see BiomeSpecialEffects#getWaterColor()
     * @see BiomeSpecialEffects.Builder#waterColor(int)
     */
    int getWaterColor();

    /**
     * @see BiomeSpecialEffects#getWaterFogColor()
     * @see BiomeSpecialEffects.Builder#waterFogColor(int)
     */
    void setWaterFogColor(int waterFogColor);

    /**
     * @see BiomeSpecialEffects#getWaterFogColor()
     * @see BiomeSpecialEffects.Builder#waterFogColor(int)
     */
    int getWaterFogColor();

    /**
     * @see BiomeSpecialEffects#getSkyColor()
     * @see BiomeSpecialEffects.Builder#skyColor(int)
     */
    void setSkyColor(int skyColor);

    /**
     * @see BiomeSpecialEffects#getSkyColor()
     * @see BiomeSpecialEffects.Builder#skyColor(int)
     */
    int getSkyColor();

    /**
     * @see BiomeSpecialEffects#getFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    void setFoliageColorOverride(Optional<Integer> foliageColorOverride);

    /**
     * @see BiomeSpecialEffects#getFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    Optional<Integer> getFoliageColorOverride();

    /**
     * @see BiomeSpecialEffects#getFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    default void setFoliageColorOverride(int foliageColorOverride) {
        this.setFoliageColorOverride(Optional.of(foliageColorOverride));
    }

    /**
     * @see BiomeSpecialEffects#getFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    default void clearFoliageColorOverride() {
        this.setFoliageColorOverride(Optional.empty());
    }

    /**
     * @see BiomeSpecialEffects#getGrassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    void setGrassColorOverride(Optional<Integer> grassColorOverride);

    /**
     * @see BiomeSpecialEffects#getGrassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    Optional<Integer> getGrassColorOverride();

    /**
     * @see BiomeSpecialEffects#getGrassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    default void setGrassColorOverride(int grassColorOverride) {
        this.setGrassColorOverride(Optional.of(grassColorOverride));
    }

    /**
     * @see BiomeSpecialEffects#getGrassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    default void clearGrassColorOverride() {
        this.setGrassColorOverride(Optional.empty());
    }

    /**
     * @see BiomeSpecialEffects#getGrassColorModifier()
     * @see BiomeSpecialEffects.Builder#grassColorModifier(BiomeSpecialEffects.GrassColorModifier)
     */
    void setGrassColorModifier(BiomeSpecialEffects.GrassColorModifier grassColorModifier);

    /**
     * @see BiomeSpecialEffects#getGrassColorModifier()
     * @see BiomeSpecialEffects.Builder#grassColorModifier(BiomeSpecialEffects.GrassColorModifier)
     */
    BiomeSpecialEffects.GrassColorModifier getGrassColorModifier();

    /**
     * @see BiomeSpecialEffects#getAmbientParticleSettings()
     * @see BiomeSpecialEffects.Builder#ambientParticle(AmbientParticleSettings)
     */
    void setAmbientParticleSettings(Optional<AmbientParticleSettings> ambientParticleSettings);

    /**
     * @see BiomeSpecialEffects#getAmbientParticleSettings()
     * @see BiomeSpecialEffects.Builder#ambientParticle(AmbientParticleSettings)
     */
    Optional<AmbientParticleSettings> getAmbientParticleSettings();

    /**
     * @see BiomeSpecialEffects#getAmbientParticleSettings()
     * @see BiomeSpecialEffects.Builder#ambientParticle(AmbientParticleSettings)
     */
    default void setAmbientParticleSettings(AmbientParticleSettings ambientParticleSettings) {
        Objects.requireNonNull(ambientParticleSettings, "ambient particle settings is null");
        this.setAmbientParticleSettings(Optional.of(ambientParticleSettings));
    }

    /**
     * @see BiomeSpecialEffects#getAmbientParticleSettings()
     * @see BiomeSpecialEffects.Builder#ambientParticle(AmbientParticleSettings)
     */
    default void clearAmbientParticleSettings() {
        this.setAmbientParticleSettings(Optional.empty());
    }

    /**
     * @see BiomeSpecialEffects#getAmbientLoopSoundEvent()
     * @see BiomeSpecialEffects.Builder#ambientLoopSound(Holder)
     */
    void setAmbientLoopSoundEvent(Optional<Holder<SoundEvent>> ambientLoopSoundEvent);

    /**
     * @see BiomeSpecialEffects#getAmbientLoopSoundEvent()
     * @see BiomeSpecialEffects.Builder#ambientLoopSound(Holder)
     */
    Optional<Holder<SoundEvent>> getAmbientLoopSoundEvent();

    /**
     * @see BiomeSpecialEffects#getAmbientLoopSoundEvent()
     * @see BiomeSpecialEffects.Builder#ambientLoopSound(Holder)
     */
    default void setAmbientLoopSoundEvent(Holder<SoundEvent> ambientLoopSoundEvent) {
        Objects.requireNonNull(ambientLoopSoundEvent, "ambient loop sound event is null");
        this.setAmbientLoopSoundEvent(Optional.of(ambientLoopSoundEvent));
    }

    /**
     * @see BiomeSpecialEffects#getAmbientLoopSoundEvent()
     * @see BiomeSpecialEffects.Builder#ambientLoopSound(Holder)
     */
    default void clearAmbientLoopSoundEvent() {
        this.setAmbientLoopSoundEvent(Optional.empty());
    }

    /**
     * @see BiomeSpecialEffects#getAmbientMoodSettings()
     * @see BiomeSpecialEffects.Builder#ambientMoodSound(AmbientMoodSettings)
     */
    void setAmbientMoodSettings(Optional<AmbientMoodSettings> ambientMoodSettings);

    /**
     * @see BiomeSpecialEffects#getAmbientMoodSettings()
     * @see BiomeSpecialEffects.Builder#ambientMoodSound(AmbientMoodSettings)
     */
    Optional<AmbientMoodSettings> getAmbientMoodSettings();

    /**
     * @see BiomeSpecialEffects#getAmbientMoodSettings()
     * @see BiomeSpecialEffects.Builder#ambientMoodSound(AmbientMoodSettings)
     */
    default void setAmbientMoodSettings(AmbientMoodSettings ambientMoodSettings) {
        Objects.requireNonNull(ambientMoodSettings, "ambient mood settings is null");
        this.setAmbientMoodSettings(Optional.of(ambientMoodSettings));
    }

    /**
     * @see BiomeSpecialEffects#getAmbientMoodSettings()
     * @see BiomeSpecialEffects.Builder#ambientMoodSound(AmbientMoodSettings)
     */
    default void clearAmbientMoodSettings() {
        this.setAmbientMoodSettings(Optional.empty());
    }

    /**
     * @see BiomeSpecialEffects#getAmbientAdditionsSettings()
     * @see BiomeSpecialEffects.Builder#ambientAdditionsSound(AmbientAdditionsSettings)
     */
    void setAmbientAdditionsSettings(Optional<AmbientAdditionsSettings> ambientAdditionsSettings);

    /**
     * @see BiomeSpecialEffects#getAmbientAdditionsSettings()
     * @see BiomeSpecialEffects.Builder#ambientAdditionsSound(AmbientAdditionsSettings)
     */
    Optional<AmbientAdditionsSettings> getAmbientAdditionsSettings();

    /**
     * @see BiomeSpecialEffects#getAmbientAdditionsSettings()
     * @see BiomeSpecialEffects.Builder#ambientAdditionsSound(AmbientAdditionsSettings)
     */
    default void setAmbientAdditionsSettings(AmbientAdditionsSettings ambientAdditionsSettings) {
        Objects.requireNonNull(ambientAdditionsSettings, "ambient additions settings is null");
        this.setAmbientAdditionsSettings(Optional.of(ambientAdditionsSettings));
    }

    /**
     * @see BiomeSpecialEffects#getAmbientAdditionsSettings()
     * @see BiomeSpecialEffects.Builder#ambientAdditionsSound(AmbientAdditionsSettings)
     */
    default void clearAmbientAdditionsSettings() {
        this.setAmbientAdditionsSettings(Optional.empty());
    }

    /**
     * @see BiomeSpecialEffects#getBackgroundMusic()
     * @see BiomeSpecialEffects.Builder#backgroundMusic(Music)
     */
    void setBackgroundMusic(Optional<SimpleWeightedRandomList<Music>> backgroundMusic);

    /**
     * @see BiomeSpecialEffects#getBackgroundMusic()
     * @see BiomeSpecialEffects.Builder#backgroundMusic(Music)
     */
    Optional<SimpleWeightedRandomList<Music>> getBackgroundMusic();

    /**
     * @see BiomeSpecialEffects#getBackgroundMusic()
     * @see BiomeSpecialEffects.Builder#backgroundMusic(Music)
     */
    default void setBackgroundMusic(SimpleWeightedRandomList<Music> backgroundMusic) {
        Objects.requireNonNull(backgroundMusic, "background music is null");
        this.setBackgroundMusic(Optional.of(backgroundMusic));
    }

    /**
     * @see BiomeSpecialEffects#getBackgroundMusic()
     * @see BiomeSpecialEffects.Builder#backgroundMusic(Music)
     */
    default void clearBackgroundMusic() {
        this.setBackgroundMusic(Optional.empty());
    }
}
