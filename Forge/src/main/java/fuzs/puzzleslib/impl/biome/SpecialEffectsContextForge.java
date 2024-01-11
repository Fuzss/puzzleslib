package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.SpecialEffectsContext;
import fuzs.puzzleslib.mixin.accessor.BiomeSpecialEffectsBuilderForgeAccessor;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraftforge.common.world.BiomeSpecialEffectsBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * This implementation uses an accessor mixin to directly set {@link Optional}s to valid fields to allow for clearing certain options that are already present.
 * Resetting a value to an empty optional would otherwise not be possible.
 */
public class SpecialEffectsContextForge implements SpecialEffectsContext {
    private final BiomeSpecialEffectsBuilder context;

    public SpecialEffectsContextForge(BiomeSpecialEffectsBuilder context) {
        this.context = context;
    }

    @Override
    public void setFogColor(int color) {
        this.context.fogColor(color);
    }

    @Override
    public int getFogColor() {
        return this.context.getFogColor();
    }

    @Override
    public void setWaterColor(int color) {
        this.context.waterColor(color);
    }

    @Override
    public int getWaterColor() {
        return this.context.waterColor();
    }

    @Override
    public void setWaterFogColor(int color) {
        this.context.waterFogColor(color);
    }

    @Override
    public int getWaterFogColor() {
        return this.context.getWaterFogColor();
    }

    @Override
    public void setSkyColor(int color) {
        this.context.skyColor(color);
    }

    @Override
    public int getSkyColor() {
        return this.context.getSkyColor();
    }

    @Override
    public void setFoliageColorOverride(Optional<Integer> color) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setFoliageColorOverride(color);
    }

    @Override
    public Optional<Integer> getFoliageColorOverride() {
        return this.context.getFoliageColorOverride();
    }

    @Override
    public void setGrassColorOverride(Optional<Integer> color) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setGrassColorOverride(color);
    }

    @Override
    public Optional<Integer> getGrassColorOverride() {
        return this.context.getGrassColorOverride();
    }

    @Override
    public void setGrassColorModifier(@NotNull BiomeSpecialEffects.GrassColorModifier colorModifier) {
        Objects.requireNonNull(colorModifier);
        this.context.grassColorModifier(colorModifier);
    }

    @Override
    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
        return this.context.getGrassColorModifier();
    }

    @Override
    public void setAmbientParticleSettings(Optional<AmbientParticleSettings> particleConfig) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setAmbientParticle(particleConfig);
    }

    @Override
    public Optional<AmbientParticleSettings> getAmbientParticleSettings() {
        return this.context.getAmbientParticle();
    }

    @Override
    public void setAmbientLoopSoundEvent(Optional<SoundEvent> sound) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setAmbientLoopSoundEvent(sound);
    }

    @Override
    public Optional<SoundEvent> getAmbientLoopSoundEvent() {
        return this.context.getAmbientLoopSound();
    }

    @Override
    public void setAmbientMoodSettings(Optional<AmbientMoodSettings> sound) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setAmbientMoodSettings(sound);
    }

    @Override
    public Optional<AmbientMoodSettings> getAmbientMoodSettings() {
        return this.context.getAmbientMoodSound();
    }

    @Override
    public void setAmbientAdditionsSettings(Optional<AmbientAdditionsSettings> sound) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setAmbientAdditionsSettings(sound);
    }

    @Override
    public Optional<AmbientAdditionsSettings> getAmbientAdditionsSettings() {
        return this.context.getAmbientAdditionsSound();
    }

    @Override
    public void setBackgroundMusic(Optional<Music> sound) {
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.context).puzzleslib$setBackgroundMusic(sound);
    }

    @Override
    public Optional<Music> getBackgroundMusic() {
        return this.context.getBackgroundMusic();
    }
}
