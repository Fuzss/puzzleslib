package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.SpecialEffectsContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.*;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SpecialEffectsContextFabric implements SpecialEffectsContext {
    private final BiomeSpecialEffects specialEffects;
    private final BiomeModificationContext.EffectsContext context;

    public SpecialEffectsContextFabric(BiomeSpecialEffects specialEffects, BiomeModificationContext.EffectsContext context) {
        this.specialEffects = specialEffects;
        this.context = context;
    }

    @Override
    public void setFogColor(int color) {
        this.context.setFogColor(color);
    }

    @Override
    public int getFogColor() {
        return this.specialEffects.getFogColor();
    }

    @Override
    public void setWaterColor(int color) {
        this.context.setWaterColor(color);
    }

    @Override
    public int getWaterColor() {
        return this.specialEffects.getWaterColor();
    }

    @Override
    public void setWaterFogColor(int color) {
        this.context.setWaterFogColor(color);
    }

    @Override
    public int getWaterFogColor() {
        return this.specialEffects.getWaterFogColor();
    }

    @Override
    public void setSkyColor(int color) {
        this.context.setSkyColor(color);
    }

    @Override
    public int getSkyColor() {
        return this.specialEffects.getSkyColor();
    }

    @Override
    public void setFoliageColorOverride(Optional<Integer> color) {
        this.context.setFoliageColor(color);
    }

    @Override
    public Optional<Integer> getFoliageColorOverride() {
        return this.specialEffects.getFoliageColorOverride();
    }

    @Override
    public void setGrassColorOverride(Optional<Integer> color) {
        this.context.setGrassColor(color);
    }

    @Override
    public Optional<Integer> getGrassColorOverride() {
        return this.specialEffects.getGrassColorOverride();
    }

    @Override
    public void setGrassColorModifier(@NotNull BiomeSpecialEffects.GrassColorModifier colorModifier) {
        this.context.setGrassColorModifier(colorModifier);
    }

    @Override
    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
        return this.specialEffects.getGrassColorModifier();
    }

    @Override
    public void setAmbientParticleSettings(Optional<AmbientParticleSettings> particleConfig) {
        this.context.setParticleConfig(particleConfig);
    }

    @Override
    public Optional<AmbientParticleSettings> getAmbientParticleSettings() {
        return this.specialEffects.getAmbientParticleSettings();
    }

    @Override
    public void setAmbientLoopSoundEvent(Optional<SoundEvent> sound) {
        this.context.setAmbientSound(sound);
    }

    @Override
    public Optional<SoundEvent> getAmbientLoopSoundEvent() {
        return this.specialEffects.getAmbientLoopSoundEvent();
    }

    @Override
    public void setAmbientMoodSettings(Optional<AmbientMoodSettings> sound) {
        this.context.setMoodSound(sound);
    }

    @Override
    public Optional<AmbientMoodSettings> getAmbientMoodSettings() {
        return this.specialEffects.getAmbientMoodSettings();
    }

    @Override
    public void setAmbientAdditionsSettings(Optional<AmbientAdditionsSettings> sound) {
        this.context.setAdditionsSound(sound);
    }

    @Override
    public Optional<AmbientAdditionsSettings> getAmbientAdditionsSettings() {
        return this.specialEffects.getAmbientAdditionsSettings();
    }

    @Override
    public void setBackgroundMusic(Optional<Music> sound) {
        this.context.setMusic(sound);
    }

    @Override
    public Optional<Music> getBackgroundMusic() {
        return this.specialEffects.getBackgroundMusic();
    }
}
