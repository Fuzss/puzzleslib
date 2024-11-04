package fuzs.puzzleslib.neoforge.impl.client.event;

import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.event.ModelLoadingHelper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public record NeoForgeModelBakerImpl(Map<ModelBakery.BakedCacheKey, BakedModel> bakedCache,
                                     Function<ResourceLocation, UnbakedModel> unbakedModelGetter,
                                     Function<ModelResourceLocation, UnbakedModel> unbakedTopLevelGetter,
                                     Function<Material, TextureAtlasSprite> modelTextureGetter,
                                     Supplier<BakedModel> missingModel) implements ModelBaker {

    public static NeoForgeModelBakerImpl create(ModelEvent.ModifyBakingResult evt, BakedModel missingModel) {
        return create(evt, missingModel, Collections.emptyMap());
    }

    public static NeoForgeModelBakerImpl create(ModelEvent.ModifyBakingResult evt, BakedModel missingModel, Map<ModelResourceLocation, UnbakedModel> models) {
        // using a universal cache seems to be broken for some reason, wrong models are consistently retrieved
        // so at least use a map cache per model baker
        return new NeoForgeModelBakerImpl(new HashMap<>(), (ResourceLocation resourceLocation) -> {
            if (!models.isEmpty()) {
                ModelResourceLocation modelResourceLocation = ModelResourceLocation.standalone(resourceLocation);
                if (models.containsKey(modelResourceLocation)) {
                    return models.get(modelResourceLocation);
                }
            }
            return evt.getModelBakery().getModel(resourceLocation);
        }, ModelLoadingHelper.getUnbakedTopLevelModel(evt.getModelBakery()), evt.getTextureGetter(),
                () -> missingModel
        );
    }

    @Override
    public UnbakedModel getModel(ResourceLocation resourceLocation) {
        return this.unbakedModelGetter.apply(resourceLocation);
    }

    @Nullable
    @Override
    public BakedModel bake(ResourceLocation resourceLocation, ModelState modelState) {
        return this.bake(resourceLocation, modelState, this.getModelTextureGetter());
    }

    @Override
    public @Nullable UnbakedModel getTopLevelModel(ModelResourceLocation modelLocation) {
        return this.unbakedTopLevelGetter.apply(modelLocation);
    }

    @Override
    public @Nullable BakedModel bake(ResourceLocation resourceLocation, ModelState modelState, Function<Material, TextureAtlasSprite> modelTextureGetter) {
        return this.bake(this.getModel(resourceLocation), resourceLocation, modelState, modelTextureGetter);
    }

    @Override
    public @Nullable BakedModel bakeUncached(UnbakedModel unbakedModel, ModelState modelState, Function<Material, TextureAtlasSprite> modelTextureGetter) {
        if (unbakedModel instanceof BlockModel blockModel &&
                blockModel.getRootModel() == ModelBakery.GENERATION_MARKER) {
            return ModelBakery.ITEM_MODEL_GENERATOR.generateBlockModel(modelTextureGetter, blockModel)
                    .bake(this, blockModel, modelTextureGetter, modelState, false);
        } else {
            return unbakedModel.bake(this, modelTextureGetter, modelState);
        }
    }

    public BakedModel bake(UnbakedModel unbakedModel, ResourceLocation resourceLocation) {
        return this.bake(unbakedModel, resourceLocation, BlockModelRotation.X0_Y0, this.getModelTextureGetter());
    }

    private BakedModel bake(UnbakedModel unbakedModel, ResourceLocation resourceLocation, ModelState modelState, Function<Material, TextureAtlasSprite> modelTextureGetter) {
        // implementation is pretty much the same as the vanilla model baker in the model bakery
        // do not use Map::computeIfAbsent, it will throw ConcurrentModificationException due to the map potentially being modified in the provided Function
        ModelBakery.BakedCacheKey key = new ModelBakery.BakedCacheKey(resourceLocation, modelState.getRotation(),
                modelState.isUvLocked()
        );
        BakedModel bakedModel = this.bakedCache.get(key);
        if (bakedModel == null) {
            try {
                bakedModel = this.bakeUncached(unbakedModel, modelState, modelTextureGetter);
            } catch (Exception exception) {
                PuzzlesLib.LOGGER.warn("Unable to bake model: '{}': {}", resourceLocation, exception);
                bakedModel = this.missingModel.get();
            }
            this.bakedCache.put(key, bakedModel);
        }

        return bakedModel;
    }

    @Override
    public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
        return this.modelTextureGetter;
    }
}
