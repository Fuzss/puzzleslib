package fuzs.puzzleslib.neoforge.impl.client.renderer;

import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @see ModelBakery
 */
public class ModelDiscovery {
    private final Function<ResourceLocation, BlockModel> modelResolver;
    private final Set<ResourceLocation> loadingStack = new HashSet<>();
    private final Map<ResourceLocation, UnbakedModel> unbakedCache;
    final Map<ModelBakery.BakedCacheKey, BakedModel> bakedCache = new HashMap<>();
    private final Map<ModelResourceLocation, UnbakedModel> topLevelModels = new HashMap<>();
    private final Map<ModelResourceLocation, BakedModel> bakedTopLevelModels = new HashMap<>();
    private final UnbakedModel missingModel;

    public ModelDiscovery(Function<ResourceLocation, BlockModel> modelResolver, Map<ResourceLocation, UnbakedModel> unbakedCache, UnbakedModel missingModel) {
        this.modelResolver = modelResolver;
        this.unbakedCache = unbakedCache;
        this.missingModel = missingModel;
    }

    public Map<ModelResourceLocation, BakedModel> getBakedTopLevelModels() {
        return this.bakedTopLevelModels;
    }

    public void bakeModels(ModelBakery.TextureGetter textureGetter) {
        this.topLevelModels.forEach((ModelResourceLocation modelId, UnbakedModel unbakedModel) -> {
            BakedModel bakedModel = null;

            try {
                bakedModel = new ModelBakerImpl(textureGetter, modelId).bakeUncached(unbakedModel,
                        BlockModelRotation.X0_Y0);
            } catch (Exception exception) {
                PuzzlesLib.LOGGER.warn("Unable to bake model: '{}': {}", modelId, exception);
            }

            if (bakedModel != null) {
                this.bakedTopLevelModels.put(modelId, bakedModel);
            }
        });
    }

    public UnbakedModel getModel(ResourceLocation modelId) {
        if (this.unbakedCache.containsKey(modelId)) {
            return this.unbakedCache.get(modelId);
        } else if (this.loadingStack.contains(modelId)) {
            throw new IllegalStateException("Circular reference while loading " + modelId);
        } else {
            this.loadingStack.add(modelId);

            while (!this.loadingStack.isEmpty()) {
                ResourceLocation id = this.loadingStack.iterator().next();

                try {
                    if (!this.unbakedCache.containsKey(id)) {
                        UnbakedModel model = this.loadBlockModel(id);
                        this.unbakedCache.put(id, model);
                        this.loadingStack.addAll(model.getDependencies());
                    }
                } catch (Exception exception) {
                    PuzzlesLib.LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}",
                            id,
                            modelId,
                            exception);
                    this.unbakedCache.put(id, this.missingModel);
                } finally {
                    this.loadingStack.remove(id);
                }
            }

            return this.unbakedCache.getOrDefault(modelId, this.missingModel);
        }
    }

    public void registerModelAndLoadDependencies(ModelResourceLocation modelId, UnbakedModel model) {
        for (ResourceLocation dependencyId : model.getDependencies()) {
            this.getModel(dependencyId);
        }

        this.registerModel(modelId, model);
    }

    private void registerModel(ModelResourceLocation modelId, UnbakedModel model) {
        this.topLevelModels.put(modelId, model);
    }

    private BlockModel loadBlockModel(ResourceLocation modelId) throws IOException {
        String path = modelId.getPath();
        if ("builtin/generated".equals(path)) {
            return ModelBakery.GENERATION_MARKER;
        } else if ("builtin/entity".equals(path)) {
            return ModelBakery.BLOCK_ENTITY_MARKER;
        } else {
            ResourceLocation fileId = ModelBakery.MODEL_LISTER.idToFile(modelId);
            BlockModel blockmodel = this.modelResolver.apply(fileId);
            if (blockmodel == null) {
                throw new FileNotFoundException(fileId.toString());
            } else {
                blockmodel.name = modelId.toString();
                return blockmodel;
            }
        }
    }

    /**
     * @see ModelBakery.ModelBakerImpl
     */
    private class ModelBakerImpl implements ModelBaker {
        private final Function<Material, TextureAtlasSprite> modelTextureGetter;

        ModelBakerImpl(ModelBakery.TextureGetter textureGetter, ModelResourceLocation modelId) {
            this.modelTextureGetter = p_351691_ -> textureGetter.get(modelId, p_351691_);
        }

        @Override
        public UnbakedModel getModel(ResourceLocation modelId) {
            return ModelDiscovery.this.getModel(modelId);
        }

        @Override
        @Nullable
        public UnbakedModel getTopLevelModel(ModelResourceLocation modelId) {
            return ModelDiscovery.this.topLevelModels.get(modelId);
        }

        @Override
        public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
            return this.modelTextureGetter;
        }

        @Override
        public BakedModel bake(ResourceLocation modelId, ModelState transform) {
            return this.bake(modelId, transform, this.modelTextureGetter);
        }

        @Override
        public BakedModel bake(ResourceLocation modelId, ModelState transform, Function<Material, TextureAtlasSprite> sprites) {
            ModelBakery.BakedCacheKey key = new ModelBakery.BakedCacheKey(modelId,
                    transform.getRotation(),
                    transform.isUvLocked());
            BakedModel cachedModel = ModelDiscovery.this.bakedCache.get(key);
            if (cachedModel != null) {
                return cachedModel;
            } else {
                UnbakedModel unbakedModel = this.getModel(modelId);
                BakedModel bakedModel = this.bakeUncached(unbakedModel, transform, sprites);
                ModelDiscovery.this.bakedCache.put(key, bakedModel);
                return bakedModel;
            }
        }

        @Nullable
        BakedModel bakeUncached(UnbakedModel model, ModelState state) {
            return this.bakeUncached(model, state, this.modelTextureGetter);
        }

        @Override
        @Nullable
        public BakedModel bakeUncached(UnbakedModel model, ModelState state, Function<Material, TextureAtlasSprite> sprites) {
            if (model instanceof BlockModel blockmodel && blockmodel.getRootModel() == ModelBakery.GENERATION_MARKER) {
                return ModelBakery.ITEM_MODEL_GENERATOR.generateBlockModel(sprites, blockmodel)
                        .bake(this, blockmodel, sprites, state, false);
            }

            return model.bake(this, sprites, state);
        }
    }
}
