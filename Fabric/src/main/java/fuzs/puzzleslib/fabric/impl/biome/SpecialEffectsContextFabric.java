package fuzs.puzzleslib.fabric.impl.biome;

import fuzs.puzzleslib.api.biome.v1.SpecialEffectsContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

public record SpecialEffectsContextFabric(BiomeSpecialEffects specialEffects,
                                          BiomeModificationContext.EffectsContext context) implements SpecialEffectsContext {

    @Override
    public void setWaterColor(int waterColor) {
        this.context.setWaterColor(waterColor);
    }

    @Override
    public int getWaterColor() {
        return this.specialEffects.waterColor();
    }

    @Override
    public void setFoliageColorOverride(Optional<Integer> foliageColorOverride) {
        this.context.setFoliageColor(foliageColorOverride);
    }

    @Override
    public Optional<Integer> getFoliageColorOverride() {
        return this.specialEffects.foliageColorOverride();
    }

    @Override
    public void setDryFoliageColorOverride(Optional<Integer> dryFoliageColorOverride) {
        this.context.setDryFoliageColor(dryFoliageColorOverride);
    }

    @Override
    public Optional<Integer> getDryFoliageColorOverride() {
        return this.specialEffects.dryFoliageColorOverride();
    }

    @Override
    public void setGrassColorOverride(Optional<Integer> grassColorOverride) {
        this.context.setGrassColor(grassColorOverride);
    }

    @Override
    public Optional<Integer> getGrassColorOverride() {
        return this.specialEffects.grassColorOverride();
    }

    @Override
    public void setGrassColorModifier(BiomeSpecialEffects.@NonNull GrassColorModifier grassColorModifier) {
        Objects.requireNonNull(grassColorModifier, "grass color modifier is null");
        this.context.setGrassColorModifier(grassColorModifier);
    }

    @Override
    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
        return this.specialEffects.grassColorModifier();
    }
}
