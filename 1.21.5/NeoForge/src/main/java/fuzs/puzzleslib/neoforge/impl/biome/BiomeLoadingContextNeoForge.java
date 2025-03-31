package fuzs.puzzleslib.neoforge.impl.biome;

import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

public record BiomeLoadingContextNeoForge(RegistryAccess registryAccess,
                                          Holder<Biome> holder) implements BiomeLoadingContext {

    public BiomeLoadingContextNeoForge(RegistryAccess registryAccess, Holder<Biome> holder) {
        this.registryAccess = registryAccess;
        this.holder = holder;
    }

    @Override
    public ResourceKey<Biome> getResourceKey() {
        return this.holder.unwrapKey().orElseThrow();
    }

    @Override
    public Biome getBiome() {
        return this.holder.value();
    }

    @Override
    public Optional<ResourceKey<ConfiguredFeature<?, ?>>> getFeatureKey(ConfiguredFeature<?, ?> configuredFeature) {
        Registry<ConfiguredFeature<?, ?>> registry = this.registryAccess.lookupOrThrow(Registries.CONFIGURED_FEATURE);
        return registry.getResourceKey(configuredFeature);
    }

    @Override
    public Optional<ResourceKey<PlacedFeature>> getPlacedFeatureKey(PlacedFeature placedFeature) {
        Registry<PlacedFeature> registry = this.registryAccess.lookupOrThrow(Registries.PLACED_FEATURE);
        return registry.getResourceKey(placedFeature);
    }

    @Override
    public boolean validForStructure(ResourceKey<Structure> key) {
        Structure structure = this.registryAccess.lookupOrThrow(Registries.STRUCTURE).getValue(key);
        return structure != null && structure.biomes().contains(this.holder());
    }

    @Override
    public Optional<ResourceKey<Structure>> getStructureKey(Structure structure) {
        Registry<Structure> registry = this.registryAccess.lookupOrThrow(Registries.STRUCTURE);
        return registry.getResourceKey(structure);
    }

    @Override
    public boolean canGenerateIn(ResourceKey<LevelStem> dimensionKey) {
        LevelStem levelStem = this.registryAccess.lookupOrThrow(Registries.LEVEL_STEM).getValue(dimensionKey);
        return levelStem != null && levelStem.generator()
                .getBiomeSource()
                .possibleBiomes()
                .stream()
                .anyMatch(entry -> entry.value() == this.getBiome());
    }

    @Override
    public boolean is(TagKey<Biome> tag) {
        Registry<Biome> registry = this.registryAccess.lookupOrThrow(Registries.BIOME);
        return registry.getOrThrow(this.getResourceKey()).is(tag);
    }
}
