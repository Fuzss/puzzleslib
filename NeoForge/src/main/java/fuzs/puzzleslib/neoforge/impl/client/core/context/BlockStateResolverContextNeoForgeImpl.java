package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.api.client.renderer.v1.model.ModelLoadingHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.block.LoadedBlockModels;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
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
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.*;
import java.util.stream.Collectors;

public final class BlockStateResolverContextNeoForgeImpl implements BlockStateResolverContext {
    private final ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
    private final Function<Identifier, TextureAtlasSprite> textureGetter;
    private final ResolvedModel missingModel;
    private final Supplier<Material.Baked> missingSprite;
    private final Map<Identifier, ResolvedModel> resolvedModels;
    private final BiConsumer<BlockState, BlockStateModel> blockStateModelOutput;
    private final Function<Map<BlockState, BlockStateModel.UnbakedRoot>, ModelBakery> modelBakeryFactory;

    public BlockStateResolverContextNeoForgeImpl(ModelEvent.ModifyBakingResult event) {
        this.textureGetter = event.getTextureGetter();
        this.missingModel = event.getModelBakery().missingModel;
        this.missingSprite = Suppliers.memoize(() -> {
            TextureAtlasSprite textureAtlasSprite = event.getTextureGetter()
                    .apply(MissingTextureAtlasSprite.getLocation());
            Objects.requireNonNull(textureAtlasSprite, "missing sprite is null");
            return new Material.Baked(textureAtlasSprite, false);
        });
        this.resolvedModels = new HashMap<>(event.getModelBakery().resolvedModels);
        this.blockStateModelOutput = event.getBakingResult().blockStateModels()::put;
        this.modelBakeryFactory = (Map<BlockState, BlockStateModel.UnbakedRoot> unbakedBlockStateModels) -> {
            // Use the vanilla constructor, as there are other mods which run some setup in it via Mixin which we shouldn't skip.
            return new ModelBakery(event.getModelBakery().entityModelSet,
                    event.getModelBakery().sprites,
                    event.getModelBakery().playerSkinRenderCache,
                    unbakedBlockStateModels,
                    Collections.emptyMap(),
                    this.resolvedModels,
                    this.missingModel);
        };
    }

    @Override
    public void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, BlockStateModel.UnbakedRoot>> blockStateConsumer) {
        ModelDiscovery modelDiscovery = new ModelDiscovery(Collections.emptyMap(), this.missingModel.wrapped());
        modelDiscovery.uncachedResolver = (Object object) -> {
            Identifier resourcelocation = (Identifier) object;
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
     * {@link ModelManager#loadModels(SpriteLoader.Preparations, SpriteLoader.Preparations, ModelBakery,
     * LoadedBlockModels, Object2IntMap, EntityModelSet, Executor)}.
     */
    private static ModelBakery.BakingResult loadModels(ProfilerFiller profiler, Function<Identifier, TextureAtlasSprite> textureGetter, ModelBakery modelBakery, Supplier<Material.Baked> missingSprite) {
        profiler.push(PuzzlesLibMod.id("baking").toString());
        final Multimap<String, Identifier> missingSprites = Multimaps.synchronizedMultimap(HashMultimap.create());
        final Multimap<String, String> missingReferences = Multimaps.synchronizedMultimap(HashMultimap.create());
        ModelBakery.BakingResult bakingResult = modelBakery.bakeModels(new MaterialBaker() {
            @Override
            public Material.Baked get(Material material, ModelDebugName name) {
                if (missingSprites.containsEntry(name.debugName(), material.sprite())) {
                    return missingSprite.get();
                } else {
                    TextureAtlasSprite textureAtlasSprite = textureGetter.apply(material.sprite());
                    if (Objects.equals(textureAtlasSprite.contents().name(), MissingTextureAtlasSprite.getLocation())) {
                        missingSprites.put(name.debugName(), material.sprite());
                        return missingSprite.get();
                    } else {
                        return new Material.Baked(textureAtlasSprite, material.forceTranslucent());
                    }
                }
            }

            @Override
            public Material.Baked reportMissingReference(String reference, ModelDebugName name) {
                missingReferences.put(name.debugName(), reference);
                return missingSprite.get();
            }
        }, Util.backgroundExecutor()).join();
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
                                .map((String stringx) -> "    " + stringx)
                                .collect(Collectors.joining("\n"))));
        profiler.pop();
        return bakingResult;
    }
}
