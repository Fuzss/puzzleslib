package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.api.client.renderer.v1.model.ModelLoadingHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.client.core.context.ResourceLoaderContextImpl;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
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

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class BlockStateResolverContextNeoForgeImpl implements BlockStateResolverContext {
    private final ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
    private final Function<Material, TextureAtlasSprite> textureGetter;
    private final UnbakedModel missingModel;
    private final Supplier<TextureAtlasSprite> missingSprite;
    private final Map<ResourceLocation, UnbakedModel> unbakedPlainModels;
    private final BiConsumer<ModelResourceLocation, BakedModel> blockStateModelOutput;

    public BlockStateResolverContextNeoForgeImpl(ModelEvent.ModifyBakingResult evt) {
        this.textureGetter = evt.getTextureGetter();
        this.missingModel = evt.getModelBakery().missingModel;
        this.missingSprite = Suppliers.memoize(() -> {
            Material material = new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());
            TextureAtlasSprite textureAtlasSprite = evt.getTextureGetter().apply(material);
            Objects.requireNonNull(textureAtlasSprite, "missing sprite is null");
            return textureAtlasSprite;
        });
        this.unbakedPlainModels = new HashMap<>(evt.getModelBakery().unbakedPlainModels);
        this.blockStateModelOutput = evt.getBakingResult().blockStateModels()::put;
    }

    @Override
    public void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, UnbakedBlockStateModel>> blockStateConsumer) {
        ModelDiscovery modelDiscovery = new ModelDiscovery(Collections.emptyMap(), this.missingModel) {
            @Override
            protected UnbakedModel loadBlockModel(ResourceLocation modelLocation) {
                return BlockStateResolverContextNeoForgeImpl.this.getBlockModel(modelLocation);
            }
        };
        Map<ModelResourceLocation, UnbakedBlockStateModel> unbakedBlockStateModels = new HashMap<>();
        blockStateConsumer.accept((BlockState blockState, UnbakedBlockStateModel unbakedBlockStateModel) -> {
            modelDiscovery.addRoot(unbakedBlockStateModel);
            unbakedBlockStateModels.put(BlockModelShaper.stateToModelLocation(blockState), unbakedBlockStateModel);
        });
        modelDiscovery.discoverDependencies();
        this.loadModels(unbakedBlockStateModels).blockStateModels().forEach(this.blockStateModelOutput);
    }

    UnbakedModel getBlockModel(ResourceLocation resourceLocation) {
        return this.unbakedPlainModels.computeIfAbsent(resourceLocation,
                (ResourceLocation resourceLocationX) -> ModelLoadingHelper.loadBlockModel(this.resourceManager,
                        resourceLocationX,
                        this.missingModel));
    }

    ModelBakery.BakingResult loadModels(Map<ModelResourceLocation, UnbakedBlockStateModel> unbakedBlockStateModels) {
        ModelBakery modelBakery = new ModelBakery(EntityModelSet.EMPTY,
                unbakedBlockStateModels,
                Collections.emptyMap(),
                this.unbakedPlainModels,
                this.missingModel,
                Collections.emptyMap());
        return loadModels(Profiler.get(), this.textureGetter, modelBakery, this.missingSprite);
    }

    @Override
    public <T> void registerBlockStateResolver(Block block, Function<BlockStateResolverContext.ResourceLoaderContext, CompletableFuture<T>> resourceLoader, BiConsumer<T, BiConsumer<BlockState, UnbakedBlockStateModel>> blockStateConsumer) {
        this.registerBlockStateResolver(block, (BiConsumer<BlockState, UnbakedBlockStateModel> consumer) -> {
            blockStateConsumer.accept(resourceLoader.apply(new ResourceLoaderContextImpl(this.resourceManager,
                    Util.backgroundExecutor(),
                    this.unbakedPlainModels)).join(), consumer);
        });
    }

    /**
     * Similar to
     * {@link ModelManager#loadModels(ProfilerFiller, Map, ModelBakery, Object2IntMap, EntityModelSet,
     * SpecialBlockModelRenderer)}.
     */
    static ModelBakery.BakingResult loadModels(ProfilerFiller profiler, Function<Material, TextureAtlasSprite> textureGetter, ModelBakery modelBakery, Supplier<TextureAtlasSprite> missingSprite) {
        profiler.push(PuzzlesLibMod.id("baking").toString());
        final Multimap<String, Material> multimap = HashMultimap.create();
        final Multimap<String, String> multimap1 = HashMultimap.create();
        ModelBakery.BakingResult bakingResult = modelBakery.bakeModels(new ModelBakery.TextureGetter() {
            @Override
            public TextureAtlasSprite get(ModelDebugName name, Material material) {
                TextureAtlasSprite textureAtlasSprite = textureGetter.apply(material);
                if (textureAtlasSprite != null) {
                    return textureAtlasSprite;
                } else {
                    multimap.put(name.get(), material);
                    return missingSprite.get();
                }
            }

            @Override
            public TextureAtlasSprite reportMissingReference(ModelDebugName name, String reference) {
                multimap1.put(name.get(), reference);
                return missingSprite.get();
            }
        });
        multimap.asMap()
                .forEach((String string, Collection<Material> collection) -> PuzzlesLib.LOGGER.warn(
                        "Missing textures in model {}:\n{}",
                        string,
                        collection.stream()
                                .sorted(Material.COMPARATOR)
                                .map((Material material) -> "    " + material.atlasLocation() + ":" +
                                        material.texture())
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
