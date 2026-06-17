package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Suppliers;
import fuzs.puzzleslib.common.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.common.api.client.renderer.v1.model.ModelLoadingHelper;
import fuzs.puzzleslib.common.impl.PuzzlesLib;
import fuzs.puzzleslib.common.impl.PuzzlesLibMod;
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
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.*;

public final class BlockStateResolverContextNeoForgeImpl implements BlockStateResolverContext {
    private final ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
    private final Function<Identifier, TextureAtlasSprite> textureGetter;
    private final ResolvedModel missingModel;
    private final Supplier<TextureAtlasSprite> missingSprite;
    private final Map<Identifier, ResolvedModel> resolvedModels;
    private final BiConsumer<BlockState, BlockStateModel> blockStateModelOutput;
    private final Function<Map<BlockState, BlockStateModel.UnbakedRoot>, ModelBakery> modelBakeryFactory;

    public BlockStateResolverContextNeoForgeImpl(ModelEvent.ModifyBakingResult event) {
        this.textureGetter = event.getTextureGetter();
        this.missingModel = event.getModelBakery().missingModel;
        this.missingSprite = Suppliers.memoize(() -> {
            TextureAtlasSprite missingSprite = event.getTextureGetter().apply(MissingTextureAtlasSprite.getLocation());
            Objects.requireNonNull(missingSprite, "missing sprite is null");
            return missingSprite;
        });
        this.resolvedModels = new HashMap<>(event.getModelBakery().resolvedModels);
        this.blockStateModelOutput = event.getBakingResult().blockStateModels()::put;
        this.modelBakeryFactory = (Map<BlockState, BlockStateModel.UnbakedRoot> unbakedBlockStateModels) -> {
            // Use the vanilla constructor, as there are other mods, which run some setup in it via Mixin which we shouldn't skip.
            return new ModelBakery(event.getModelBakery().entityModelSet,
                    event.getModelBakery().sprites,
                    event.getModelBakery().playerSkinRenderCache,
                    unbakedBlockStateModels,
                    new HashMap<>(),
                    this.resolvedModels,
                    this.missingModel);
        };
    }

    @Override
    public void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, BlockStateModel.UnbakedRoot>> blockStateConsumer) {
        ModelDiscovery modelDiscovery = new ModelDiscovery(new HashMap<>(), this.missingModel.wrapped());
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
        return loadModels(this.textureGetter,
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
    private static ModelBakery.BakingResult loadModels(Function<Identifier, TextureAtlasSprite> textureGetter, ModelBakery modelBakery, Supplier<TextureAtlasSprite> missingSprite) {
        try (Zone ignored = Profiler.get().zone(PuzzlesLibMod.id("baking")::toString)) {
            MaterialBaker materialBaker = new MaterialBaker(missingSprite.get()) {
                @Override
                protected Material.@Nullable Baked bake(Material material) {
                    TextureAtlasSprite sprite = textureGetter.apply(material.sprite());
                    return sprite != null ? new Material.Baked(sprite, material.forceTranslucent()) : null;
                }
            };
            return modelBakery.bakeModels(materialBaker, Util.backgroundExecutor())
                    .whenComplete((ModelBakery.BakingResult bakingResult, Throwable throwable) -> {
                        materialBaker.logMissingTextures();
                    })
                    .join();
        }
    }
}
