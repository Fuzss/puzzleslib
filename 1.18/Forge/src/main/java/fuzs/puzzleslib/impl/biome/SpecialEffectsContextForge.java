package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.SpecialEffectsContext;
import fuzs.puzzleslib.mixin.accessor.BiomeSpecialEffectsBuilderForgeAccessor;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This implementation uses an accessor mixin to directly set {@link Optional}s to valid fields to allow for clearing certain options that are already present.
 * Resetting a value to an empty optional would otherwise not be possible.
 */
public record SpecialEffectsContextForge(Supplier<BiomeSpecialEffects> supplier, Consumer<BiomeSpecialEffects> consumer) implements SpecialEffectsContext {

    @Override
    public void setFogColor(int fogColor) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(fogColor).waterColor(this.getContext().getWaterColor()).waterFogColor(this.getContext().getWaterFogColor()).skyColor(this.getContext().getSkyColor());
        this.getContext().getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.getContext().getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.getContext().getGrassColorModifier());
        this.getContext().getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.getContext().getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.getContext().getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.getContext().getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.getContext().getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.consumer.accept(builder.build());
    }

    @Override
    public int getFogColor() {
        return this.getContext().getFogColor();
    }

    @Override
    public void setWaterColor(int waterColor) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.getContext().getFogColor()).waterColor(waterColor).waterFogColor(this.getContext().getWaterFogColor()).skyColor(this.getContext().getSkyColor());
        this.getContext().getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.getContext().getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.getContext().getGrassColorModifier());
        this.getContext().getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.getContext().getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.getContext().getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.getContext().getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.getContext().getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.consumer.accept(builder.build());
    }

    @Override
    public int getWaterColor() {
        return this.getContext().getWaterColor();
    }

    @Override
    public void setWaterFogColor(int waterFogColor) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.getContext().getFogColor()).waterColor(this.getContext().getWaterColor()).waterFogColor(waterFogColor).skyColor(this.getContext().getSkyColor());
        this.getContext().getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.getContext().getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.getContext().getGrassColorModifier());
        this.getContext().getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.getContext().getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.getContext().getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.getContext().getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.getContext().getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.consumer.accept(builder.build());
    }

    @Override
    public int getWaterFogColor() {
        return this.getContext().getWaterFogColor();
    }

    @Override
    public void setSkyColor(int skyColor) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.getContext().getFogColor()).waterColor(this.getContext().getWaterColor()).waterFogColor(this.getContext().getWaterFogColor()).skyColor(skyColor);
        this.getContext().getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.getContext().getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.getContext().getGrassColorModifier());
        this.getContext().getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.getContext().getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.getContext().getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.getContext().getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.getContext().getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.consumer.accept(builder.build());
    }

    @Override
    public int getSkyColor() {
        return this.getContext().getSkyColor();
    }

    @Override
    public void setFoliageColorOverride(Optional<Integer> foliageColorOverride) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.getContext().getFogColor()).waterColor(this.getContext().getWaterColor()).waterFogColor(this.getContext().getWaterFogColor()).skyColor(this.getContext().getSkyColor());
        ((BiomeSpecialEffectsBuilderForgeAccessor) builder).puzzleslib$setFoliageColorOverride(foliageColorOverride);
        this.getContext().getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.getContext().getGrassColorModifier());
        this.getContext().getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.getContext().getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.getContext().getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.getContext().getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.getContext().getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.consumer.accept(builder.build());
    }

    @Override
    public Optional<Integer> getFoliageColorOverride() {
        return this.getContext().getFoliageColorOverride();
    }

    @Override
    public void setGrassColorOverride(Optional<Integer> grassColorOverride) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.getContext().getFogColor()).waterColor(this.getContext().getWaterColor()).waterFogColor(this.getContext().getWaterFogColor()).skyColor(this.getContext().getSkyColor());
        this.getContext().getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        ((BiomeSpecialEffectsBuilderForgeAccessor) this.getContext()).puzzleslib$setGrassColorOverride(grassColorOverride);
        builder.grassColorModifier(this.getContext().getGrassColorModifier());
        this.getContext().getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.getContext().getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.getContext().getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.getContext().getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.getContext().getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.consumer.accept(builder.build());
    }

    @Override
    public Optional<Integer> getGrassColorOverride() {
        return this.getContext().getGrassColorOverride();
    }

    @Override
    public void setGrassColorModifier(@NotNull BiomeSpecialEffects.GrassColorModifier grassColorModifier) {
        Objects.requireNonNull(grassColorModifier);
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.getContext().getFogColor()).waterColor(this.getContext().getWaterColor()).waterFogColor(this.getContext().getWaterFogColor()).skyColor(this.getContext().getSkyColor());
        this.getContext().getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.getContext().getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(grassColorModifier);
        this.getContext().getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.getContext().getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.getContext().getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.getContext().getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.getContext().getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.consumer.accept(builder.build());
    }

    @Override
    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
        return this.getContext().getGrassColorModifier();
    }

    @Override
    public void setAmbientParticleSettings(Optional<AmbientParticleSettings> ambientParticleSettings) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.getContext().getFogColor()).waterColor(this.getContext().getWaterColor()).waterFogColor(this.getContext().getWaterFogColor()).skyColor(this.getContext().getSkyColor());
        this.getContext().getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.getContext().getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.getContext().getGrassColorModifier());
        ((BiomeSpecialEffectsBuilderForgeAccessor) builder).puzzleslib$setAmbientParticle(ambientParticleSettings);
        this.getContext().getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.getContext().getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.getContext().getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.getContext().getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.consumer.accept(builder.build());
    }

    @Override
    public Optional<AmbientParticleSettings> getAmbientParticleSettings() {
        return this.getContext().getAmbientParticleSettings();
    }

    @Override
    public void setAmbientLoopSoundEvent(Optional<SoundEvent> ambientLoopSoundEvent) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.getContext().getFogColor()).waterColor(this.getContext().getWaterColor()).waterFogColor(this.getContext().getWaterFogColor()).skyColor(this.getContext().getSkyColor());
        this.getContext().getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.getContext().getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.getContext().getGrassColorModifier());
        this.getContext().getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        ((BiomeSpecialEffectsBuilderForgeAccessor) builder).puzzleslib$setAmbientLoopSoundEvent(ambientLoopSoundEvent);
        this.getContext().getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.getContext().getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.getContext().getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.consumer.accept(builder.build());
    }

    @Override
    public Optional<SoundEvent> getAmbientLoopSoundEvent() {
        return this.getContext().getAmbientLoopSoundEvent();
    }

    @Override
    public void setAmbientMoodSettings(Optional<AmbientMoodSettings> ambientMoodSettings) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.getContext().getFogColor()).waterColor(this.getContext().getWaterColor()).waterFogColor(this.getContext().getWaterFogColor()).skyColor(this.getContext().getSkyColor());
        this.getContext().getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.getContext().getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.getContext().getGrassColorModifier());
        this.getContext().getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.getContext().getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        ((BiomeSpecialEffectsBuilderForgeAccessor) builder).puzzleslib$setAmbientMoodSettings(ambientMoodSettings);
        this.getContext().getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        this.getContext().getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.consumer.accept(builder.build());
    }

    @Override
    public Optional<AmbientMoodSettings> getAmbientMoodSettings() {
        return this.getContext().getAmbientMoodSettings();
    }

    @Override
    public void setAmbientAdditionsSettings(Optional<AmbientAdditionsSettings> ambientAdditionsSettings) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.getContext().getFogColor()).waterColor(this.getContext().getWaterColor()).waterFogColor(this.getContext().getWaterFogColor()).skyColor(this.getContext().getSkyColor());
        this.getContext().getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.getContext().getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.getContext().getGrassColorModifier());
        this.getContext().getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.getContext().getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.getContext().getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        ((BiomeSpecialEffectsBuilderForgeAccessor) builder).puzzleslib$setAmbientAdditionsSettings(ambientAdditionsSettings);
        this.getContext().getBackgroundMusic().ifPresent(builder::backgroundMusic);
        this.consumer.accept(builder.build());
    }

    @Override
    public Optional<AmbientAdditionsSettings> getAmbientAdditionsSettings() {
        return this.getContext().getAmbientAdditionsSettings();
    }

    @Override
    public void setBackgroundMusic(Optional<Music> backgroundMusic) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder().fogColor(this.getContext().getFogColor()).waterColor(this.getContext().getWaterColor()).waterFogColor(this.getContext().getWaterFogColor()).skyColor(this.getContext().getSkyColor());
        this.getContext().getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        this.getContext().getGrassColorOverride().ifPresent(builder::grassColorOverride);
        builder.grassColorModifier(this.getContext().getGrassColorModifier());
        this.getContext().getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        this.getContext().getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        this.getContext().getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        this.getContext().getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        ((BiomeSpecialEffectsBuilderForgeAccessor) builder).puzzleslib$setBackgroundMusic(backgroundMusic);
        this.consumer.accept(builder.build());
    }

    @Override
    public Optional<Music> getBackgroundMusic() {
        return this.getContext().getBackgroundMusic();
    }

    private BiomeSpecialEffects getContext() {
        return this.supplier.get();
    }
}
