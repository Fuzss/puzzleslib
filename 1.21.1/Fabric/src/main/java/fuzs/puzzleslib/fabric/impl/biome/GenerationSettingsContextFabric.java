package fuzs.puzzleslib.fabric.impl.biome;

import com.google.common.collect.Iterables;
import fuzs.puzzleslib.api.biome.v1.GenerationSettingsContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public record GenerationSettingsContextFabric(BiomeGenerationSettings generationSettings, BiomeModificationContext.GenerationSettingsContext context) implements GenerationSettingsContext {

    @Override
    public boolean removeFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey) {
        return this.context.removeFeature(step, featureKey);
    }

    @Override
    public void addFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey) {
        this.context.addFeature(step, featureKey);
    }

    @Override
    public void addCarver(GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> carverKey) {
        this.context.addCarver(step, carverKey);
    }

    @Override
    public boolean removeCarver(GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> carverKey) {
        return this.context.removeCarver(step, carverKey);
    }

    @Override
    public Iterable<Holder<PlacedFeature>> getFeatures(GenerationStep.Decoration stage) {
        List<HolderSet<PlacedFeature>> featureSteps = this.generationSettings.features();
        if (stage.ordinal() >= featureSteps.size()) return List.of();
        return Iterables.unmodifiableIterable(featureSteps.get(stage.ordinal()));
    }

    @Override
    public Iterable<Holder<ConfiguredWorldCarver<?>>> getCarvers(GenerationStep.Carving stage) {
        return Iterables.unmodifiableIterable(this.generationSettings.getCarvers(stage));
    }
}
