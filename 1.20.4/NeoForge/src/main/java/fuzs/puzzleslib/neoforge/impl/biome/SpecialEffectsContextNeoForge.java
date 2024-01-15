package fuzs.puzzleslib.neoforge.impl.biome;

import fuzs.puzzleslib.api.biome.v1.SpecialEffectsContext;
import fuzs.puzzleslib.neoforge.mixin.accessor.BiomeSpecialEffectsBuilderForgeAccessor;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.neoforged.neoforge.common.world.BiomeSpecialEffectsBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * This implementation uses an accessor mixin to directly set {@link Optional}s to valid fields to allow for clearing certain options that are already present.
 * Resetting a value to an empty optional would otherwise not be possible.
 */
public record SpecialEffectsContextNeoForge(BiomeSpecialEffectsBuilder context) implements SpecialEffectsContext {

    @Override
    public void setFogColor(int fogColor) {
        this.context.fogColor(fogColor);
    }

    @Override
    public int getFogColor() {
        return this.context.getFogColor();
    }

    @Override
    public void setWaterColor(int waterColor) {
        this.context.waterColor(waterColor);
    }

    @Override
    public int getWaterColor() {
        return this.context.waterColor();
    }

    @Override
    public void setWaterFogColor(int waterFogColor) {
        this.context.waterFogColor(waterFogColor);
    }

    @Override
    public int getWaterFogColor() {
        return this.context.getWaterFogColor();
    }

    @Override
    public void setSkyColor(int skyColor) {
        this.context.skyColor(skyColor);
    }

    @Override
    public int getSkyColor() {
        return this.context.getSkyColor();
    }

    @Override
    public void setFoliageColorOverride(Optional<Integer> foliageColorOverride) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setFoliageColorOverride(foliageColorOverride);
    }

    @Override
    public Optional<Integer> getFoliageColorOverride() {
        return this.context.getFoliageColorOverride();
    }

    @Override
    public void setGrassColorOverride(Optional<Integer> grassColorOverride) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setGrassColorOverride(grassColorOverride);
    }

    @Override
    public Optional<Integer> getGrassColorOverride() {
        return this.context.getGrassColorOverride();
    }

    @Override
    public void setGrassColorModifier(@NotNull BiomeSpecialEffects.GrassColorModifier grassColorModifier) {
        Objects.requireNonNull(grassColorModifier, "grass color modifier is null");
        this.context.grassColorModifier(grassColorModifier);
    }

    @Override
    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
        return this.context.getGrassColorModifier();
    }

    @Override
    public void setAmbientParticleSettings(Optional<AmbientParticleSettings> ambientParticleSettings) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setAmbientParticle(ambientParticleSettings);
    }

    @Override
    public Optional<AmbientParticleSettings> getAmbientParticleSettings() {
        return this.context.getAmbientParticle();
    }

    @Override
    public void setAmbientLoopSoundEvent(Optional<Holder<SoundEvent>> ambientLoopSoundEvent) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setAmbientLoopSoundEvent(ambientLoopSoundEvent);
    }

    @Override
    public Optional<Holder<SoundEvent>> getAmbientLoopSoundEvent() {
        return this.context.getAmbientLoopSound();
    }

    @Override
    public void setAmbientMoodSettings(Optional<AmbientMoodSettings> ambientMoodSettings) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setAmbientMoodSettings(ambientMoodSettings);
    }

    @Override
    public Optional<AmbientMoodSettings> getAmbientMoodSettings() {
        return this.context.getAmbientMoodSound();
    }

    @Override
    public void setAmbientAdditionsSettings(Optional<AmbientAdditionsSettings> ambientAdditionsSettings) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setAmbientAdditionsSettings(ambientAdditionsSettings);
    }

    @Override
    public Optional<AmbientAdditionsSettings> getAmbientAdditionsSettings() {
        return this.context.getAmbientAdditionsSound();
    }

    @Override
    public void setBackgroundMusic(Optional<Music> backgroundMusic) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setBackgroundMusic(backgroundMusic);
    }

    @Override
    public Optional<Music> getBackgroundMusic() {
        return this.context.getBackgroundMusic();
    }
}
