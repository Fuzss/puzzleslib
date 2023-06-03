package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.SpecialEffectsContext;
import fuzs.puzzleslib.mixin.accessor.BiomeSpecialEffectsBuilderForgeAccessor;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * This implementation uses an accessor mixin to directly set {@link Optional}s to valid fields to allow for clearing certain options that are already present.
 * Resetting a value to an empty optional would otherwise not be possible.
 */
public class SpecialEffectsContextForge implements SpecialEffectsContext {
    public BiomeSpecialEffects context;

    public SpecialEffectsContextForge(BiomeSpecialEffects context) {
        this.context = context;
    }

    @Override
    public void setFogColor(int color) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(color).waterColor(this.context.getWaterColor()).waterFogColor(this.context.getWaterFogColor()).skyColor(this.context.getSkyColor());
        this.context.getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.context.getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.context.getGrassColorModifier());
        this.context.getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.context.getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.context.getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.context.getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.context.getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.context = builder.build();
    }

    @Override
    public int getFogColor() {
        return this.context.getFogColor();
    }

    @Override
    public void setWaterColor(int color) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.context.getFogColor()).waterColor(color).waterFogColor(this.context.getWaterFogColor()).skyColor(this.context.getSkyColor());
        this.context.getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.context.getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.context.getGrassColorModifier());
        this.context.getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.context.getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.context.getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.context.getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.context.getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.context = builder.build();
    }

    @Override
    public int getWaterColor() {
        return this.context.getWaterColor();
    }

    @Override
    public void setWaterFogColor(int color) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.context.getFogColor()).waterColor(this.context.getWaterColor()).waterFogColor(color).skyColor(this.context.getSkyColor());
        this.context.getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.context.getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.context.getGrassColorModifier());
        this.context.getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.context.getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.context.getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.context.getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.context.getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.context = builder.build();
    }

    @Override
    public int getWaterFogColor() {
        return this.context.getWaterFogColor();
    }

    @Override
    public void setSkyColor(int color) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.context.getFogColor()).waterColor(this.context.getWaterColor()).waterFogColor(this.context.getWaterFogColor()).skyColor(color);
        this.context.getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.context.getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.context.getGrassColorModifier());
        this.context.getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.context.getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.context.getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.context.getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.context.getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.context = builder.build();
    }

    @Override
    public int getSkyColor() {
        return this.context.getSkyColor();
    }

    @Override
    public void setFoliageColorOverride(Optional<Integer> color) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.context.getFogColor()).waterColor(this.context.getWaterColor()).waterFogColor(this.context.getWaterFogColor()).skyColor(this.context.getSkyColor());
        ((BiomeSpecialEffectsBuilderForgeAccessor) builder).puzzleslib$setFoliageColorOverride(color);
        this.context.getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.context.getGrassColorModifier());
        this.context.getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.context.getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.context.getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.context.getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.context.getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.context = builder.build();
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

        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.context.getFogColor()).waterColor(this.context.getWaterColor()).waterFogColor(this.context.getWaterFogColor()).skyColor(this.context.getSkyColor());
        this.context.getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.context.getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.context.getGrassColorModifier());
        this.context.getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.context.getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.context.getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.context.getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.context.getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.context = builder.build();
    }

    @Override
    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
        return this.context.getGrassColorModifier();
    }

    @Override
    public void setAmbientParticleSettings(Optional<AmbientParticleSettings> particleConfig) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.context.getFogColor()).waterColor(this.context.getWaterColor()).waterFogColor(this.context.getWaterFogColor()).skyColor(this.context.getSkyColor());
        this.context.getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.context.getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.context.getGrassColorModifier());
        ((BiomeSpecialEffectsBuilderForgeAccessor) builder).puzzleslib$setAmbientParticle(particleConfig);
        this.context.getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.context.getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.context.getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.context.getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.context = builder.build();
    }

    @Override
    public Optional<AmbientParticleSettings> getAmbientParticleSettings() {
        return this.context.getAmbientParticleSettings();
    }

    @Override
    public void setAmbientLoopSoundEvent(Optional<Holder<SoundEvent>> sound) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.context.getFogColor()).waterColor(this.context.getWaterColor()).waterFogColor(this.context.getWaterFogColor()).skyColor(this.context.getSkyColor());
        this.context.getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.context.getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.context.getGrassColorModifier());
        this.context.getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        ((BiomeSpecialEffectsBuilderForgeAccessor) builder).puzzleslib$setAmbientLoopSoundEvent(sound);
        this.context.getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.context.getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.context.getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.context = builder.build();
    }

    @Override
    public Optional<SoundEvent> getAmbientLoopSoundEvent() {
        return this.context.getAmbientLoopSoundEvent();
    }

    @Override
    public void setAmbientMoodSettings(Optional<AmbientMoodSettings> sound) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.context.getFogColor()).waterColor(this.context.getWaterColor()).waterFogColor(this.context.getWaterFogColor()).skyColor(this.context.getSkyColor());
        this.context.getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.context.getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.context.getGrassColorModifier());
        this.context.getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.context.getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        ((BiomeSpecialEffectsBuilderForgeAccessor) builder).puzzleslib$setAmbientMoodSettings(sound);
        this.context.getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.context.getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.context = builder.build();
    }

    @Override
    public Optional<AmbientMoodSettings> getAmbientMoodSettings() {
        return this.context.getAmbientMoodSettings();
    }

    @Override
    public void setAmbientAdditionsSettings(Optional<AmbientAdditionsSettings> sound) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.context.getFogColor()).waterColor(this.context.getWaterColor()).waterFogColor(this.context.getWaterFogColor()).skyColor(this.context.getSkyColor());
        this.context.getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.context.getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.context.getGrassColorModifier());
        this.context.getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.context.getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.context.getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        ((BiomeSpecialEffectsBuilderForgeAccessor) builder).puzzleslib$setAmbientAdditionsSettings(sound);
        this.context.getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.context = builder.build();
    }

    @Override
    public Optional<AmbientAdditionsSettings> getAmbientAdditionsSettings() {
        return this.context.getAmbientAdditionsSettings();
    }

    @Override
    public void setBackgroundMusic(Optional<Music> sound) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.context.getFogColor()).waterColor(this.context.getWaterColor()).waterFogColor(this.context.getWaterFogColor()).skyColor(this.context.getSkyColor());
        this.context.getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.context.getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.context.getGrassColorModifier());
        this.context.getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.context.getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.context.getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.context.getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        ((BiomeSpecialEffectsBuilderForgeAccessor) builder).puzzleslib$setBackgroundMusic(sound);
        this.context = builder.build();
    }

    @Override
    public Optional<Music> getBackgroundMusic() {
        return this.context.getBackgroundMusic();
    }
}
