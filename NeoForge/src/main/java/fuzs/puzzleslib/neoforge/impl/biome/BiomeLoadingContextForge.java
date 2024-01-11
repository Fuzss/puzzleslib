package fuzs.puzzleslib.neoforge.impl.biome;

import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
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

public record BiomeLoadingContextForge(RegistryAccess registryAccess, Holder<Biome> holder) implements BiomeLoadingContext {

    public BiomeLoadingContextForge(RegistryAccess registryAccess, Holder<Biome> holder) {
        this.registryAccess = registryAccess;
        this.holder = holder;
    }

    public BiomeLoadingContextForge(Holder<Biome> holder) {
        this(CommonAbstractions.INSTANCE.getMinecraftServer().registryAccess(), holder);
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
        Registry<ConfiguredFeature<?, ?>> registry = this.registryAccess.registryOrThrow(Registries.CONFIGURED_FEATURE);
        return registry.getResourceKey(configuredFeature);
    }

    @Override
    public Optional<ResourceKey<PlacedFeature>> getPlacedFeatureKey(PlacedFeature placedFeature) {
        Registry<PlacedFeature> registry = this.registryAccess.registryOrThrow(Registries.PLACED_FEATURE);
        return registry.getResourceKey(placedFeature);
    }

    @Override
    public boolean validForStructure(ResourceKey<Structure> key) {
        Structure instance = this.registryAccess.registryOrThrow(Registries.STRUCTURE).get(key);
        return instance != null && instance.biomes().contains(this.holder());
    }

    @Override
    public Optional<ResourceKey<Structure>> getStructureKey(Structure structure) {
        Registry<Structure> registry = this.registryAccess.registryOrThrow(Registries.STRUCTURE);
        return registry.getResourceKey(structure);
    }

    @Override
    public boolean canGenerateIn(ResourceKey<LevelStem> dimensionKey) {
        LevelStem dimension = this.registryAccess.registryOrThrow(Registries.LEVEL_STEM).get(dimensionKey);
        return dimension != null && dimension.generator().getBiomeSource().possibleBiomes().stream().anyMatch(entry -> entry.value() == this.getBiome());
    }

    @Override
    public boolean is(TagKey<Biome> tag) {
        Registry<Biome> biomeRegistry = this.registryAccess.registryOrThrow(Registries.BIOME);
        return biomeRegistry.getHolderOrThrow(this.getResourceKey()).is(tag);
    }
}
