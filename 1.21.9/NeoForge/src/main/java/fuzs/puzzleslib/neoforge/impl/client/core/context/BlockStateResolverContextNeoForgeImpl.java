package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.api.client.renderer.v1.model.ModelLoadingHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelLoader;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.*;
import java.util.stream.Collectors;

public final class BlockStateResolverContextNeoForgeImpl implements BlockStateResolverContext {
    private final ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
    private final Function<ResourceLocation, TextureAtlasSprite> textureGetter;
    private final ResolvedModel missingModel;
    private final Supplier<TextureAtlasSprite> missingSprite;
    private final Map<ResourceLocation, ResolvedModel> resolvedModels;
    private final BiConsumer<BlockState, BlockStateModel> blockStateModelOutput;
    private final Function<Map<BlockState, BlockStateModel.UnbakedRoot>, ModelBakery> modelBakeryFactory;

    public BlockStateResolverContextNeoForgeImpl(ModelEvent.ModifyBakingResult event) {
        this.textureGetter = event.getTextureGetter();
        this.missingModel = event.getModelBakery().missingModel;
        this.missingSprite = Suppliers.memoize(() -> {
            TextureAtlasSprite textureAtlasSprite = event.getTextureGetter()
                    .apply(MissingTextureAtlasSprite.getLocation());
            Objects.requireNonNull(textureAtlasSprite, "missing sprite is null");
            return textureAtlasSprite;
        });
        this.resolvedModels = new HashMap<>(event.getModelBakery().resolvedModels);
        this.blockStateModelOutput = event.getBakingResult().blockStateModels()::put;
        this.modelBakeryFactory = (Map<BlockState, BlockStateModel.UnbakedRoot> unbakedBlockStateModels) -> {
            return new ModelBakery(event.getModelBakery().entityModelSet,
                    event.getModelBakery().materials,
                    event.getModelBakery().playerSkinRenderCache,
                    unbakedBlockStateModels,
                    Collections.emptyMap(),
                    this.resolvedModels,
                    this.missingModel,
                    StandaloneModelLoader.LoadedModels.EMPTY);
        };
    }

    @Override
    public void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, BlockStateModel.UnbakedRoot>> blockStateConsumer) {
        ModelDiscovery modelDiscovery = new ModelDiscovery(Collections.emptyMap(), this.missingModel.wrapped());
        modelDiscovery.uncachedResolver = (Object object) -> {
            ResourceLocation resourcelocation = (ResourceLocation) object;
            ResolvedModel resolvedModel = this.resolvedModels.get(resourcelocation);
            if (resolvedModel instanceof ModelDiscovery.ModelWrapper modelWrapper) {
                return modelWrapper;
            } else {
                UnbakedModel unbakedmodel = ModelLoadingHelper.loadBlockModel(this.resourceManager, resourcelocation);
                if (unbakedmodel == null) {
                    PuzzlesLib.LOGGER.warn("Missing block model: {}", resourcelocation);
                    return (ModelDiscovery.ModelWrapper) this.missingModel;
                } else {
                    return modelDiscovery.createAndQueueWrapper(resourcelocation, unbakedmodel);
                }
            }
        };
        Map<BlockState, BlockStateModel.UnbakedRoot> unbakedBlockStateModels = new HashMap<>();
        blockStateConsumer.accept((BlockState blockState, BlockStateModel.UnbakedRoot unbakedBlockStateModel) -> {
            modelDiscovery.addRoot(unbakedBlockStateModel);
            unbakedBlockStateModels.put(blockState, unbakedBlockStateModel);
        });
        modelDiscovery.resolve().forEach(this.resolvedModels::putIfAbsent);
        this.loadModels(unbakedBlockStateModels).blockStateModels().forEach(this.blockStateModelOutput);
    }

    private ModelBakery.BakingResult loadModels(Map<BlockState, BlockStateModel.UnbakedRoot> unbakedBlockStateModels) {
        return loadModels(Profiler.get(),
                this.textureGetter,
                this.modelBakeryFactory.apply(unbakedBlockStateModels),
                this.missingSprite);
    }

    @Override
    public <T> void registerBlockStateResolver(Block block, BiFunction<ResourceManager, Executor, CompletableFuture<T>> resourceLoader, BiConsumer<T, BiConsumer<BlockState, BlockStateModel.UnbakedRoot>> blockStateConsumer) {
        this.registerBlockStateResolver(block, (BiConsumer<BlockState, BlockStateModel.UnbakedRoot> consumer) -> {
            blockStateConsumer.accept(resourceLoader.apply(this.resourceManager, Util.backgroundExecutor()).join(),
                    consumer);
        });
    }

    /**
     * Similar to
     * {@link ModelManager#loadModels(SpriteLoader.Preparations, ModelBakery, Object2IntMap, EntityModelSet,
     * SpecialBlockModelRenderer, Executor)}.
     */
    private static ModelBakery.BakingResult loadModels(ProfilerFiller profiler, Function<ResourceLocation, TextureAtlasSprite> textureGetter, ModelBakery modelBakery, Supplier<TextureAtlasSprite> missingSprite) {
        profiler.push(PuzzlesLibMod.id("baking").toString());
        final Multimap<String, Material> multimap = HashMultimap.create();
        final Multimap<String, String> multimap1 = HashMultimap.create();
        ModelBakery.BakingResult bakingResult = modelBakery.bakeModels(new SpriteGetter() {
            @Override
            public TextureAtlasSprite get(Material material, ModelDebugName name) {
                if (material.atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS)) {
                    TextureAtlasSprite textureAtlasSprite = textureGetter.apply(material.texture());
                    if (textureAtlasSprite != null) {
                        return textureAtlasSprite;
                    }
                }

                multimap.put(name.debugName(), material);
                return missingSprite.get();
            }

            @Override
            public TextureAtlasSprite reportMissingReference(String reference, ModelDebugName name) {
                multimap1.put(name.debugName(), reference);
                return missingSprite.get();
            }
        }, Util.backgroundExecutor()).join();
        multimap.asMap()
                .forEach((String string, Collection<Material> collection) -> PuzzlesLib.LOGGER.warn(
                        "Missing textures in model {}:\n{}",
                        string,
                        collection.stream()
                                .sorted(Material.COMPARATOR)
                                .map((Material material) -> "    " + material.atlasLocation() + ":"
                                        + material.texture())
                                .collect(Collectors.joining("\n"))));
        multimap1.asMap()
                .forEach((String string, Collection<String> collection) -> PuzzlesLib.LOGGER.warn(
                        "Missing texture references in model {}:\n{}",
                        string,
                        collection.stream()
                                .sorted()
                                .map((String stringx) -> "    " + stringx)
                                .collect(Collectors.joining("\n"))));
        profiler.pop();
        return bakingResult;
    }
}
