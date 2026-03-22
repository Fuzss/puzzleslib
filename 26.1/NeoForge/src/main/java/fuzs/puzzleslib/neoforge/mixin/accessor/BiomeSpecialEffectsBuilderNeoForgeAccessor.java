package fuzs.puzzleslib.neoforge.mixin.accessor;

import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(BiomeSpecialEffects.Builder.class)
public interface BiomeSpecialEffectsBuilderNeoForgeAccessor {

    @Accessor("foliageColorOverride")
    void puzzleslib$setFoliageColorOverride(Optional<Integer> foliageColorOverride);

    @Accessor("dryFoliageColorOverride")
    void puzzleslib$setDryFoliageColorOverride(Optional<Integer> dryFoliageColorOverride);

    @Accessor("grassColorOverride")
    void puzzleslib$setGrassColorOverride(Optional<Integer> grassColorOverride);
}
