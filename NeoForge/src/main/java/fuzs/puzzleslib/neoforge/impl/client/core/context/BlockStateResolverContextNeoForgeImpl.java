package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.api.client.renderer.v1.model.ModelLoadingHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.neoforge.impl.client.renderer.ModelDiscovery;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.*;
import java.util.stream.Collectors;

public final class BlockStateResolverContextNeoForgeImpl implements BlockStateResolverContext {
    private final ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
    private final Function<Material, TextureAtlasSprite> textureResolver;
    private final UnbakedModel missingModel;
    private final Supplier<TextureAtlasSprite> missingSprite;
    private final Function<ResourceLocation, BlockModel> modelResolver;
    private final Map<ResourceLocation, UnbakedModel> modelCache = new HashMap<>();
    private final BiConsumer<ModelResourceLocation, BakedModel> blockStateModelOutput;

    public BlockStateResolverContextNeoForgeImpl(ModelEvent.ModifyBakingResult event) {
        this.textureResolver = event.getTextureGetter();
        this.missingModel = event.getModelBakery().missingModel;
        this.missingSprite = Suppliers.memoize(() -> {
            Material material = new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());
            TextureAtlasSprite textureAtlasSprite = event.getTextureGetter().apply(material);
            Objects.requireNonNull(textureAtlasSprite, "missing sprite is null");
            return textureAtlasSprite;
        });
        Map<ResourceLocation, BlockModel> modelResources = new HashMap<>();
        Function<ResourceLocation, BlockModel> modelResolver = this::getBlockModel;
        this.modelResolver = (ResourceLocation blockId) -> {
            return modelResources.computeIfAbsent(blockId, modelResolver);
        };
        this.blockStateModelOutput = event.getModels()::put;
    }

    private BlockModel getBlockModel(ResourceLocation blockId) {
        BlockModel blockModel = ModelLoadingHelper.loadBlockModel(this.resourceManager, blockId);
        if (blockModel == null) {
            PuzzlesLib.LOGGER.warn("Missing block model: {}", blockId);
            return (BlockModel) this.missingModel;
        } else {
            return blockModel;
        }
    }

    @Override
    public void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, UnbakedModel>> blockStateConsumer) {
        ModelDiscovery discovery = new ModelDiscovery(this.modelResolver, this.modelCache, this.missingModel);
        blockStateConsumer.accept((BlockState state, UnbakedModel model) -> {
            discovery.registerModelAndLoadDependencies(BlockModelShaper.stateToModelLocation(state), model);
        });
        this.loadModels(discovery).forEach(this.blockStateModelOutput);
    }

    private Map<ModelResourceLocation, BakedModel> loadModels(ModelDiscovery discovery) {
        return loadModels(InactiveProfiler.INSTANCE, this.textureResolver, discovery, this.missingSprite);
    }

    @Override
    public <T> void registerBlockStateResolver(Block block, BiFunction<ResourceManager, Executor, CompletableFuture<T>> resourceLoader, BiConsumer<T, BiConsumer<BlockState, UnbakedModel>> blockStateConsumer) {
        this.registerBlockStateResolver(block, (BiConsumer<BlockState, UnbakedModel> consumer) -> {
            blockStateConsumer.accept(resourceLoader.apply(this.resourceManager, Util.backgroundExecutor()).join(),
                    consumer);
        });
    }

    /**
     * @see ModelManager#loadModels(ProfilerFiller, Map, ModelBakery)
     */
    private static Map<ModelResourceLocation, BakedModel> loadModels(ProfilerFiller profiler, Function<Material, TextureAtlasSprite> textureResolver, ModelDiscovery discovery, Supplier<TextureAtlasSprite> missingSprite) {
        profiler.push(PuzzlesLibMod.id("baking").toString());
        Multimap<String, Material> missingSprites = Multimaps.synchronizedMultimap(HashMultimap.create());
        discovery.bakeModels((ModelResourceLocation modelId, Material material) -> {
            TextureAtlasSprite sprite = textureResolver.apply(material);
            if (sprite != null) {
                return sprite;
            } else {
                missingSprites.put(modelId.toString(), material);
                return missingSprite.get();
            }
        });
        missingSprites.asMap()
                .forEach((String string, Collection<Material> collection) -> PuzzlesLib.LOGGER.warn(
                        "Missing textures in model {}:\n{}",
                        string,
                        collection.stream()
                                .sorted(Material.COMPARATOR)
                                .map((Material material) -> "    " + material.atlasLocation() + ":"
                                        + material.texture())
                                .collect(Collectors.joining("\n"))));
        profiler.pop();
        return discovery.getBakedTopLevelModels();
    }
}
