package fuzs.puzzleslib.impl.biome;

import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.proxy.Proxy;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;

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

    public static BiomeLoadingContext create(Holder<Biome> holder) {
        MinecraftServer server = Proxy.INSTANCE.getGameServer();
        WorldData worldData = server.getWorldData();
        if (!(worldData instanceof PrimaryLevelData primaryLevelData)) {
            throw new RuntimeException("Incompatible SaveProperties passed to MinecraftServer: " + worldData);
        }
        ResourceKey<Biome> resourceKey = holder.unwrapKey().orElseThrow();
        return new BiomeLoadingContextForge(server.registryAccess(), primaryLevelData, resourceKey, holder.value(), holder);
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
    public boolean validForStructure(ResourceKey<Structure> key) {
        Structure instance = this.registryAccess.registryOrThrow(Registry.STRUCTURE_REGISTRY).get(key);

        if (instance == null) {
            return false;
        }

        return instance.biomes().contains(this.holder());
    }

    @Override
    public Optional<ResourceKey<Structure>> getStructureKey(Structure structure) {
        Registry<Structure> registry = this.registryAccess.registryOrThrow(Registry.STRUCTURE_REGISTRY);
        return registry.getResourceKey(structure);
    }

    @Override
    public boolean canGenerateIn(ResourceKey<LevelStem> dimensionKey) {
        LevelStem dimension = this.levelData.worldGenSettings().dimensions().get(dimensionKey);

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
