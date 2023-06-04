package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.storage.PrimaryLevelData;

import java.util.Optional;

public class BiomeLoadingContextForge implements BiomeLoadingContext {
    private final RegistryAccess registryAccess;
    private final PrimaryLevelData levelData;
    private final ResourceKey<Biome> resourceKey;
    private final Biome biome;
    private final Holder<Biome> holder;

    private BiomeLoadingContextForge(RegistryAccess registryAccess, PrimaryLevelData levelData, ResourceKey<Biome> resourceKey, Biome biome, Holder<Biome> holder) {
        this.registryAccess = registryAccess;
        this.levelData = levelData;
        this.resourceKey = resourceKey;
        this.biome = biome;
        this.holder = holder;
    }

    public static BiomeLoadingContext create(RegistryAccess.Frozen registryAccess, ResourceKey<Biome> resourceKey) {
        // Forge runs this very early and the minecraft server isn't even available at this point, so use custom built-in registries without biomes
        // (as we are constructing the built-in values for those at this very moment) to allow the 1.19 implementation to still work
        return new BiomeLoadingContextForge(registryAccess, null, resourceKey, null, null);
    }

    @Override
    public ResourceKey<Biome> getResourceKey() {
        return this.resourceKey;
    }

    @Override
    public Biome getBiome() {
        throw new UnsupportedOperationException("biome is null");
    }

    @Override
    public Holder<Biome> holder() {
        throw new UnsupportedOperationException("biome holder is null");
    }

    @Override
    public Optional<ResourceKey<ConfiguredFeature<?, ?>>> getFeatureKey(ConfiguredFeature<?, ?> configuredFeature) {
        Registry<ConfiguredFeature<?, ?>> registry = this.registryAccess.registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
        return registry.getResourceKey(configuredFeature);
    }

    @Override
    public Optional<ResourceKey<PlacedFeature>> getPlacedFeatureKey(PlacedFeature placedFeature) {
        Registry<PlacedFeature> registry = this.registryAccess.registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
        return registry.getResourceKey(placedFeature);
    }

    @Override
    public boolean validForStructure(ResourceKey<ConfiguredStructureFeature<?, ?>> key) {
        ConfiguredStructureFeature<?, ?> instance = this.registryAccess.registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).get(key);

        if (instance == null) {
            return false;
        }

        return instance.biomes().contains(this.holder());
    }

    @Override
    public Optional<ResourceKey<ConfiguredStructureFeature<?, ?>>> getStructureKey(ConfiguredStructureFeature<?, ?> structure) {
        Registry<ConfiguredStructureFeature<?, ?>> registry = this.registryAccess.registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        return registry.getResourceKey(structure);
    }

    @Override
    public boolean canGenerateIn(ResourceKey<LevelStem> dimensionKey) {
        LevelStem dimension = this.registryAccess.registryOrThrow(Registry.LEVEL_STEM_REGISTRY).get(dimensionKey);

        if (dimension == null) {
            return false;
        }

        return dimension.generator().getBiomeSource().possibleBiomes().stream().anyMatch(entry -> entry.value() == this.getBiome());
    }

    @Override
    public boolean is(TagKey<Biome> tag) {
        Registry<Biome> biomeRegistry = this.registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
        return biomeRegistry.getHolderOrThrow(this.getResourceKey()).is(tag);
    }
}
