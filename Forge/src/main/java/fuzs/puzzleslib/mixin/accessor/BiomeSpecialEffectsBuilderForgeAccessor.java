package fuzs.puzzleslib.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(BiomeSpecialEffects.Builder.class)
public interface BiomeSpecialEffectsBuilderForgeAccessor {

    @Accessor("foliageColorOverride")
    void puzzleslib$setFoliageColorOverride(Optional<Integer> foliageColorOverride);

    @Accessor("grassColorOverride")
    void puzzleslib$setGrassColorOverride(Optional<Integer> grassColorOverride);

    @Accessor("ambientParticle")
    void puzzleslib$setAmbientParticle(Optional<AmbientParticleSettings> ambientParticle);

    @Accessor("ambientLoopSoundEvent")
    void puzzleslib$setAmbientLoopSoundEvent(Optional<Holder<SoundEvent>> ambientLoopSoundEvent);

    @Accessor("ambientMoodSettings")
    void puzzleslib$setAmbientMoodSettings(Optional<AmbientMoodSettings> ambientMoodSettings);

    @Accessor("ambientAdditionsSettings")
    void puzzleslib$setAmbientAdditionsSettings(Optional<AmbientAdditionsSettings> ambientAdditionsSettings);

    @Accessor("backgroundMusic")
    void puzzleslib$setBackgroundMusic(Optional<Music> backgroundMusic);
}
