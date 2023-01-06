package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

public class BiomeLoadingContextFabric implements BiomeLoadingContext {
    private final BiomeSelectionContext context;

    private BiomeLoadingContextFabric(BiomeSelectionContext context) {
        this.context = context;
    }

    public static BiomeLoadingContext create(BiomeSelectionContext context) {
        return new BiomeLoadingContextFabric(context);
    }

    @Override
    public ResourceKey<Biome> getResourceKey() {
        return this.context.getBiomeKey();
    }

    @Override
    public Biome getBiome() {
        return this.context.getBiome();
    }

    @Override
    public Holder<Biome> holder() {
        return this.context.getBiomeRegistryEntry();
    }

    @Override
    public Optional<ResourceKey<ConfiguredFeature<?, ?>>> getFeatureKey(ConfiguredFeature<?, ?> configuredFeature) {
        return this.context.getFeatureKey(configuredFeature);
    }

    @Override
    public Optional<ResourceKey<PlacedFeature>> getPlacedFeatureKey(PlacedFeature placedFeature) {
        return this.context.getPlacedFeatureKey(placedFeature);
    }

    @Override
    public boolean validForStructure(ResourceKey<Structure> key) {
        return this.context.validForStructure(key);
    }

    @Override
    public Optional<ResourceKey<Structure>> getStructureKey(Structure structureFeature) {
        return this.context.getStructureKey(structureFeature);
    }

    @Override
    public boolean canGenerateIn(ResourceKey<LevelStem> dimensionKey) {
        return this.context.canGenerateIn(dimensionKey);
    }

    @Override
    public boolean hasTag(TagKey<Biome> tag) {
        return this.context.hasTag(tag);
    }
}
