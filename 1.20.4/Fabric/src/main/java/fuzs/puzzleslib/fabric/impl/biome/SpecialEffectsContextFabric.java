package fuzs.puzzleslib.fabric.impl.biome;

import fuzs.puzzleslib.api.biome.v1.SpecialEffectsContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record SpecialEffectsContextFabric(BiomeSpecialEffects specialEffects, BiomeModificationContext.EffectsContext context) implements SpecialEffectsContext {

    @Override
    public void setFogColor(int fogColor) {
        this.context.setFogColor(fogColor);
    }

    @Override
    public int getFogColor() {
        return this.specialEffects.getFogColor();
    }

    @Override
    public void setWaterColor(int waterColor) {
        this.context.setWaterColor(waterColor);
    }

    @Override
    public int getWaterColor() {
        return this.specialEffects.getWaterColor();
    }

    @Override
    public void setWaterFogColor(int waterFogColor) {
        this.context.setWaterFogColor(waterFogColor);
    }

    @Override
    public int getWaterFogColor() {
        return this.specialEffects.getWaterFogColor();
    }

    @Override
    public void setSkyColor(int skyColor) {
        this.context.setSkyColor(skyColor);
    }

    @Override
    public int getSkyColor() {
        return this.specialEffects.getSkyColor();
    }

    @Override
    public void setFoliageColorOverride(Optional<Integer> foliageColorOverride) {
        this.context.setFoliageColor(foliageColorOverride);
    }

    @Override
    public Optional<Integer> getFoliageColorOverride() {
        return this.specialEffects.getFoliageColorOverride();
    }

    @Override
    public void setGrassColorOverride(Optional<Integer> grassColorOverride) {
        this.context.setGrassColor(grassColorOverride);
    }

    @Override
    public Optional<Integer> getGrassColorOverride() {
        return this.specialEffects.getGrassColorOverride();
    }

    @Override
    public void setGrassColorModifier(@NotNull BiomeSpecialEffects.GrassColorModifier grassColorModifier) {
        this.context.setGrassColorModifier(grassColorModifier);
    }

    @Override
    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
        return this.specialEffects.getGrassColorModifier();
    }

    @Override
    public void setAmbientParticleSettings(Optional<AmbientParticleSettings> ambientParticleSettings) {
        this.context.setParticleConfig(ambientParticleSettings);
    }

    @Override
    public Optional<AmbientParticleSettings> getAmbientParticleSettings() {
        return this.specialEffects.getAmbientParticleSettings();
    }

    @Override
    public void setAmbientLoopSoundEvent(Optional<Holder<SoundEvent>> ambientLoopSoundEvent) {
        this.context.setAmbientSound(ambientLoopSoundEvent);
    }

    @Override
    public Optional<Holder<SoundEvent>> getAmbientLoopSoundEvent() {
        return this.specialEffects.getAmbientLoopSoundEvent();
    }

    @Override
    public void setAmbientMoodSettings(Optional<AmbientMoodSettings> ambientMoodSettings) {
        this.context.setMoodSound(ambientMoodSettings);
    }

    @Override
    public Optional<AmbientMoodSettings> getAmbientMoodSettings() {
        return this.specialEffects.getAmbientMoodSettings();
    }

    @Override
    public void setAmbientAdditionsSettings(Optional<AmbientAdditionsSettings> ambientAdditionsSettings) {
        this.context.setAdditionsSound(ambientAdditionsSettings);
    }

    @Override
    public Optional<AmbientAdditionsSettings> getAmbientAdditionsSettings() {
        return this.specialEffects.getAmbientAdditionsSettings();
    }

    @Override
    public void setBackgroundMusic(Optional<Music> backgroundMusic) {
        this.context.setMusic(backgroundMusic);
    }

    @Override
    public Optional<Music> getBackgroundMusic() {
        return this.specialEffects.getBackgroundMusic();
    }
}
