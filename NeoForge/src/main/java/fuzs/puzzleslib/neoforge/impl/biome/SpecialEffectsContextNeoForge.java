package fuzs.puzzleslib.neoforge.impl.biome;

import fuzs.puzzleslib.api.biome.v1.SpecialEffectsContext;
import fuzs.puzzleslib.neoforge.mixin.accessor.BiomeSpecialEffectsBuilderNeoForgeAccessor;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.neoforged.neoforge.common.world.BiomeSpecialEffectsBuilder;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * This implementation uses an accessor mixin to directly set {@link Optional Optional} to valid fields to allow for
 * clearing certain options that are already present. Resetting a value to an empty optional would otherwise not be
 * possible.
 */
public record SpecialEffectsContextNeoForge(BiomeSpecialEffectsBuilder context) implements SpecialEffectsContext {

    @Override
    public void setWaterColor(int waterColor) {
        this.context.waterColor(waterColor);
    }

    @Override
    public int getWaterColor() {
        return this.context.waterColor();
    }

    @Override
    public void setFoliageColorOverride(Optional<Integer> foliageColorOverride) {
        ((BiomeSpecialEffectsBuilderNeoForgeAccessor) this.context).puzzleslib$setFoliageColorOverride(
                foliageColorOverride);
    }

    @Override
    public Optional<Integer> getFoliageColorOverride() {
        return this.context.getFoliageColorOverride();
    }

    @Override
    public void setDryFoliageColorOverride(Optional<Integer> dryFoliageColorOverride) {
        ((BiomeSpecialEffectsBuilderNeoForgeAccessor) this.context).puzzleslib$setDryFoliageColorOverride(
                dryFoliageColorOverride);
    }

    @Override
    public Optional<Integer> getDryFoliageColorOverride() {
        return this.context.getDryFoliageColorOverride();
    }

    @Override
    public void setGrassColorOverride(Optional<Integer> grassColorOverride) {
        ((BiomeSpecialEffectsBuilderNeoForgeAccessor) this.context).puzzleslib$setGrassColorOverride(grassColorOverride);
    }

    @Override
    public Optional<Integer> getGrassColorOverride() {
        return this.context.getGrassColorOverride();
    }

    @Override
    public void setGrassColorModifier(BiomeSpecialEffects.@NonNull GrassColorModifier grassColorModifier) {
        Objects.requireNonNull(grassColorModifier, "grass color modifier is null");
        this.context.grassColorModifier(grassColorModifier);
    }

    @Override
    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
        return this.context.getGrassColorModifier();
    }
}
