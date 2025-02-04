package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.api.client.util.v1.ModelLoadingHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
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
    private final Map<ModelResourceLocation, UnbakedBlockStateModel> unbakedBlockStateModels;
    private final Map<ResourceLocation, UnbakedModel> unbakedPlainModels;
    private final ModelBakery modelBakery;
    private final BiConsumer<ModelResourceLocation, BakedModel> blockStateModelOutput;

    public BlockStateResolverContextNeoForgeImpl(ModelEvent.ModifyBakingResult evt) {
        this.textureGetter = evt.getTextureGetter();
        this.missingModel = MissingBlockModel.missingModel();
        this.missingSprite = Suppliers.memoize(() -> {
            Material material = new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());
            TextureAtlasSprite textureAtlasSprite = evt.getTextureGetter().apply(material);
            Objects.requireNonNull(textureAtlasSprite, "missing sprite is null");
            return textureAtlasSprite;
        });
        this.unbakedBlockStateModels = new HashMap<>();
        this.unbakedPlainModels = new HashMap<>(evt.getModelBakery().unbakedPlainModels);
        this.modelBakery = new ModelBakery(EntityModelSet.EMPTY,
                this.unbakedBlockStateModels,
                Collections.emptyMap(),
                this.unbakedPlainModels,
                this.missingModel,
                Collections.emptyMap());
        this.blockStateModelOutput = evt.getBakingResult().blockStateModels()::put;
    }

    @Override
    public void registerBlockStateResolver(Block block, Consumer<BiConsumer<BlockState, UnbakedBlockStateModel>> blockStateConsumer) {
        ResolvableModel.Resolver resolver = new ModelLoadingResolver();
        blockStateConsumer.accept((BlockState blockState, UnbakedBlockStateModel unbakedBlockStateModel) -> {
            unbakedBlockStateModel.resolveDependencies(resolver);
            this.unbakedBlockStateModels.put(BlockModelShaper.stateToModelLocation(blockState), unbakedBlockStateModel);
        });
        ModelBakery.BakingResult bakingResult = loadModels(Profiler.get(),
                this.textureGetter,
                this.modelBakery,
                this.missingSprite);
        bakingResult.blockStateModels().forEach(this.blockStateModelOutput);
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
                .forEach((string, collection) -> PuzzlesLib.LOGGER.warn("Missing textures in model {}:\n{}",
                        string,
                        collection.stream()
                                .sorted(Material.COMPARATOR)
                                .map(arg -> "    " + arg.atlasLocation() + ":" + arg.texture())
                                .collect(Collectors.joining("\n"))));
        multimap1.asMap()
                .forEach((string, collection) -> PuzzlesLib.LOGGER.warn("Missing texture references in model {}:\n{}",
                        string,
                        collection.stream()
                                .sorted()
                                .map(stringx -> "    " + stringx)
                                .collect(Collectors.joining("\n"))));
        profiler.pop();
        return bakingResult;
    }

    /**
     * Mostly copied from {@link ModelDiscovery.ResolverImpl}.
     */
    class ModelLoadingResolver implements ResolvableModel.Resolver {
        private final List<ResourceLocation> stack = new ArrayList<>();
        private final Map<ResourceLocation, UnbakedModel> resolvedModels = BlockStateResolverContextNeoForgeImpl.this.unbakedPlainModels;

        @Override
        public UnbakedModel resolve(ResourceLocation resourceLocation) {
            if (this.stack.contains(resourceLocation)) {
                PuzzlesLib.LOGGER.warn("Detected model loading loop: {}->{}",
                        this.stacktraceToString(),
                        resourceLocation);
                return BlockStateResolverContextNeoForgeImpl.this.missingModel;
            } else {
                UnbakedModel unbakedModel = ModelLoadingHelper.loadBlockModel(BlockStateResolverContextNeoForgeImpl.this.resourceManager,
                        resourceLocation);
                if (this.resolvedModels.putIfAbsent(resourceLocation, unbakedModel) == null) {
                    this.stack.add(resourceLocation);
                    unbakedModel.resolveDependencies(this);
                    this.stack.remove(resourceLocation);
                }

                return unbakedModel;
            }
        }

        private String stacktraceToString() {
            return this.stack.stream().map(ResourceLocation::toString).collect(Collectors.joining("->"));
        }
    }
}
