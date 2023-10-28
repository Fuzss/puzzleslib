package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.GenerationSettingsContext;
import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;

import java.util.Collections;

public record GenerationSettingsContextForge(Registry<ConfiguredWorldCarver<?>> carvers, Registry<PlacedFeature> features, BiomeGenerationSettingsBuilder context) implements GenerationSettingsContext {

    public GenerationSettingsContextForge(RegistryAccess registryAccess, BiomeGenerationSettingsBuilder context) {
        this(registryAccess.registryOrThrow(Registries.CONFIGURED_CARVER), registryAccess.registryOrThrow(Registries.PLACED_FEATURE), context);
    }

    public GenerationSettingsContextForge(BiomeGenerationSettingsBuilder context) {
        this(CommonAbstractions.INSTANCE.getMinecraftServer().registryAccess(), context);
    }

    @Override
    public boolean removeFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey) {
        PlacedFeature feature = this.features.getOrThrow(featureKey);
        return this.context.getFeatures(step).removeIf(featureHolder -> featureHolder.value() == feature);
    }

    @Override
    public void addFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey) {
        this.context.addFeature(step, this.features.getHolder(featureKey).orElseThrow());
    }

    @Override
    public void addCarver(GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> carverKey) {
        this.context.addCarver(step, this.carvers.getHolder(carverKey).orElseThrow());
    }

    @Override
    public boolean removeCarver(GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> carverKey) {
        ConfiguredWorldCarver<?> carver = this.carvers.getOrThrow(carverKey);
        return this.context.getCarvers(step).removeIf(carverHolder -> carverHolder.value() == carver);
    }

    @Override
    public Iterable<Holder<PlacedFeature>> getFeatures(GenerationStep.Decoration stage) {
        // immutable just as Fabric, as biome modifications happen after biomes are built over there where everything is already immutable
        return Collections.unmodifiableList(this.context.getFeatures(stage));
    }

    @Override
    public Iterable<Holder<ConfiguredWorldCarver<?>>> getCarvers(GenerationStep.Carving stage) {
        // immutable just as Fabric, as biome modifications happen after biomes are built over there where everything is already immutable
        return Collections.unmodifiableList(this.context.getCarvers(stage));
    }
}
