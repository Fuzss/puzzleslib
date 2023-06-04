package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    public static BiomeLoadingContext create(ResourceLocation resourceLocation) {
        // Forge runs this very early and the minecraft server isn't even available at this point, so use built-in registries to allow the 1.19 implementation to still work
        RegistryAccess.Frozen registryAccess = RegistryAccess.BUILTIN.get();
        ResourceKey<Biome> resourceKey = ResourceKey.create(Registry.BIOME_REGISTRY, resourceLocation);
        Holder<Biome> holder = registryAccess.registry(Registry.BIOME_REGISTRY).flatMap(t -> t.getHolder(resourceKey)).orElse(null);
        if (holder != null) {
            return new BiomeLoadingContextForge(registryAccess, null, resourceKey, holder.value(), holder);
        }
        return null;
    }

    @Override
    public ResourceKey<Biome> getResourceKey() {
        return this.resourceKey;
    }

    @Override
    public Biome getBiome() {
        return this.biome;
    }

    @Override
    public Holder<Biome> holder() {
        return this.holder;
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

        return dimension.generator().getBiomeSource().possibleBiomes().stream().anyMatch(entry -> entry.value() == this.biome);
    }

    @Override
    public boolean is(TagKey<Biome> tag) {
        Registry<Biome> biomeRegistry = this.registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
        return biomeRegistry.getHolderOrThrow(this.getResourceKey()).is(tag);
    }
}
