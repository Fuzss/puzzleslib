package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import fuzs.puzzleslib.common.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.common.api.client.renderer.v1.model.ModelLoadingHelper;
import fuzs.puzzleslib.common.impl.PuzzlesLib;
import fuzs.puzzleslib.common.impl.PuzzlesLibMod;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.block.LoadedBlockModels;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.util.thread.ParallelMapTransform;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.*;
import java.util.stream.Collectors;

public final class BlockStateResolverContextNeoForgeImpl implements BlockStateResolverContext {
    private final ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
    private final Function<Identifier, TextureAtlasSprite> textureResolver;
    private final ResolvedModel missingModel;
    private final Supplier<Material.Baked> missingSprite;
    private final Function<Identifier, @Nullable ResolvedModel> modelResolver;
    private final BiConsumer<Identifier, ResolvedModel> modelCache;
    private final BiConsumer<BlockState, BlockStateModel> blockStateModelOutput;
    private final Function<MaterialBaker, ModelBaker> modelBakerFactory = this::createModelBaker;

    public BlockStateResolverContextNeoForgeImpl(ModelEvent.ModifyBakingResult event) {
        this.textureResolver = event.getTextureGetter();
        ModelBakery bakery = event.getModelBakery();
        this.missingModel = bakery.missingModel;
        this.missingSprite = Suppliers.memoize(() -> {
            TextureAtlasSprite sprite = event.getTextureGetter().apply(MissingTextureAtlasSprite.getLocation());
            Objects.requireNonNull(sprite, "missing sprite is null");
            return new Material.Baked(sprite, false);
        });
        Map<Identifier, ResolvedModel> resolvedModels = new HashMap<>();
        this.modelResolver = (Identifier id) -> {
            // The resolved models map from the bakery is unmodifiable when the ModernFix mod is installed.
            return bakery.resolvedModels.containsKey(id) ? bakery.resolvedModels.get(id) : resolvedModels.get(id);
        };
        this.modelCache = resolvedModels::putIfAbsent;
        this.blockStateModelOutput = event.getBakingResult().blockStateModels()::put;
    }

    private ModelBaker createModelBaker(MaterialBaker materials) {
        ModelBaker.Interner interner = new ModelBakery.InternerImpl();
        ModelBakery.MissingModels missingModels = ModelBakery.MissingModels.bake(this.missingModel,
                materials,
                interner);
        return new ModelBakerImpl(materials, interner, missingModels);
    }

    /**
     * @see ModelDiscovery#ModelDiscovery(Map, UnbakedModel)
     * @see ModelManager#discoverModelDependencies(Map, BlockStateModelLoader.LoadedModels,
     *         ClientItemInfoLoader.LoadedClientInfos)
     */
    @Override
    public void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, BlockStateModel.UnbakedRoot>> blockStateConsumer) {
        ModelDiscovery discovery = new ModelDiscovery(new HashMap<>(), this.missingModel.wrapped());
        discovery.uncachedResolver = (Object object) -> {
            Identifier id = (Identifier) object;
            ResolvedModel model = this.modelResolver.apply(id);
            if (model instanceof ModelDiscovery.ModelWrapper wrapper) {
                return wrapper;
            } else {
                UnbakedModel blockModel = ModelLoadingHelper.loadBlockModel(this.resourceManager, id);
                if (blockModel == null) {
                    PuzzlesLib.LOGGER.warn("Missing block model: {}", id);
                    return (ModelDiscovery.ModelWrapper) this.missingModel;
                } else {
                    return discovery.createAndQueueWrapper(id, blockModel);
                }
            }
        };
        Map<BlockState, BlockStateModel.UnbakedRoot> models = new HashMap<>();
        blockStateConsumer.accept((BlockState blockState, BlockStateModel.UnbakedRoot unbakedBlockStateModel) -> {
            discovery.addRoot(unbakedBlockStateModel);
            models.put(blockState, unbakedBlockStateModel);
        });
        discovery.resolve().forEach(this.modelCache);
        this.loadModels(models).forEach(this.blockStateModelOutput);
    }

    private Map<BlockState, BlockStateModel> loadModels(Map<BlockState, BlockStateModel.UnbakedRoot> models) {
        return loadModels(models, this.textureResolver, this.modelBakerFactory, this.missingSprite);
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
     * {@link ModelManager#loadModels(SpriteLoader.Preparations, SpriteLoader.Preparations, ModelBakery,
     * LoadedBlockModels, Object2IntMap, EntityModelSet, Executor)}.
     */
    private static Map<BlockState, BlockStateModel> loadModels(Map<BlockState, BlockStateModel.UnbakedRoot> models, Function<Identifier, TextureAtlasSprite> textureResolver, Function<MaterialBaker, ModelBaker> bakerFactory, Supplier<Material.Baked> missingSprite) {
        try (Zone _ = Profiler.get().zone(PuzzlesLibMod.id("baking")::toString)) {
            Multimap<String, Identifier> missingSprites = Multimaps.synchronizedMultimap(HashMultimap.create());
            Multimap<String, String> missingReferences = Multimaps.synchronizedMultimap(HashMultimap.create());
            MaterialBaker materials = new MaterialBaker() {
                @Override
                public Material.Baked get(Material material, ModelDebugName name) {
                    if (missingSprites.containsEntry(name.debugName(), material.sprite())) {
                        return missingSprite.get();
                    } else {
                        TextureAtlasSprite sprite = textureResolver.apply(material.sprite());
                        if (Objects.equals(sprite.contents().name(), MissingTextureAtlasSprite.getLocation())) {
                            missingSprites.put(name.debugName(), material.sprite());
                            return missingSprite.get();
                        } else {
                            return new Material.Baked(sprite, material.forceTranslucent());
                        }
                    }
                }

                @Override
                public Material.Baked reportMissingReference(String reference, ModelDebugName name) {
                    missingReferences.put(name.debugName(), reference);
                    return missingSprite.get();
                }
            };
            return bakeModels(models,
                    bakerFactory.apply(materials),
                    Util.backgroundExecutor()).whenComplete((Map<BlockState, BlockStateModel> _, Throwable _) -> {
                missingSprites.asMap()
                        .forEach((String string, Collection<Identifier> collection) -> PuzzlesLib.LOGGER.warn(
                                "Missing textures in model {}:\n{}",
                                string,
                                collection.stream()
                                        .map((Identifier sprite) -> "    " + sprite)
                                        .collect(Collectors.joining("\n"))));
                missingReferences.asMap()
                        .forEach((String string, Collection<String> collection) -> PuzzlesLib.LOGGER.warn(
                                "Missing texture references in model {}:\n{}",
                                string,
                                collection.stream()
                                        .sorted()
                                        .map((String message) -> "    " + message)
                                        .collect(Collectors.joining("\n"))));
            }).join();
        }
    }

    /**
     * @see ModelBakery#bakeModels(MaterialBaker, Executor)
     */
    private static CompletableFuture<Map<BlockState, BlockStateModel>> bakeModels(Map<BlockState, BlockStateModel.UnbakedRoot> models, ModelBaker baker, Executor taskExecutor) {
        return ParallelMapTransform.schedule(models, (BlockState blockState, BlockStateModel.UnbakedRoot model) -> {
            try {
                return model.bake(blockState, baker);
            } catch (Exception exception) {
                PuzzlesLib.LOGGER.warn("Unable to bake model: '{}': {}", blockState, exception);
                return null;
            }
        }, taskExecutor);
    }

    /**
     * @see ModelBakery.ModelBakerImpl
     */
    private class ModelBakerImpl implements ModelBaker {
        private final MaterialBaker materials;
        private final ModelBaker.Interner interner;
        private final ModelBakery.MissingModels missingModels;
        private final Map<ModelBaker.SharedOperationKey<?>, Object> operationCache;
        private final Function<ModelBaker.SharedOperationKey<?>, Object> cacheComputeFunction;

        private ModelBakerImpl(MaterialBaker materials, ModelBaker.Interner interner, ModelBakery.MissingModels missingModels) {
            this.operationCache = new ConcurrentHashMap<>();
            this.cacheComputeFunction = (SharedOperationKey<?> key) -> key.compute(this);
            this.materials = materials;
            this.interner = interner;
            this.missingModels = missingModels;
        }

        @Override
        public BlockStateModelPart missingBlockModelPart() {
            return this.missingModels.blockPart();
        }

        @Override
        public MaterialBaker materials() {
            return this.materials;
        }

        @Override
        public ModelBaker.Interner interner() {
            return this.interner;
        }

        @Override
        public ResolvedModel getModel(Identifier id) {
            ResolvedModel model = BlockStateResolverContextNeoForgeImpl.this.modelResolver.apply(id);
            if (model == null) {
                PuzzlesLib.LOGGER.warn("Requested a model that was not discovered previously: {}", id);
                return BlockStateResolverContextNeoForgeImpl.this.missingModel;
            } else {
                return model;
            }
        }

        @Override
        public <T> T compute(ModelBaker.SharedOperationKey<T> key) {
            return (T) this.operationCache.computeIfAbsent(key, this.cacheComputeFunction);
        }
    }
}
