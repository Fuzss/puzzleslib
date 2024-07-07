package fuzs.puzzleslib.neoforge.impl.client.event;

import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.neoforge.mixin.client.accessor.ModelBakeryNeoForgeAccessor;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public record NeoForgeModelBakerImpl(Map<ModelBakery.BakedCacheKey, BakedModel> bakedCache,
                                     Function<ResourceLocation, UnbakedModel> unbakedModelGetter,
                                     Function<ModelResourceLocation, UnbakedModel> unbakedTopLevelGetter,
                                     Function<Material, TextureAtlasSprite> modelTextureGetter,
                                     BakedModel missingModel) implements ModelBaker {

    @Override
    public UnbakedModel getModel(ResourceLocation resourceLocation) {
        return this.unbakedModelGetter.apply(resourceLocation);
    }

    @Nullable
    @Override
    public BakedModel bake(ResourceLocation resourceLocation, ModelState modelState) {
        return this.bake(resourceLocation, modelState, this.modelTextureGetter);
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
            return ModelBakeryNeoForgeAccessor.puzzleslib$getItemModelGenerator()
                    .generateBlockModel(modelTextureGetter, blockModel)
                    .bake(this, blockModel, modelTextureGetter, modelState, false);
        } else {
            return unbakedModel.bake(this, modelTextureGetter, modelState);
        }
    }

    public BakedModel bake(UnbakedModel unbakedModel, ResourceLocation resourceLocation) {
        return this.bake(unbakedModel, resourceLocation, BlockModelRotation.X0_Y0, this.modelTextureGetter);
    }

    private BakedModel bake(UnbakedModel unbakedModel, ResourceLocation resourceLocation, ModelState modelState, Function<Material, TextureAtlasSprite> modelTextureGetter) {
        // implementation is pretty much the same as the vanilla model baker in the model bakery
        // do not use Map::computeIfAbsent, it will throw ConcurrentModificationException due to the map potentially being modified in the provided Function
        ModelBakery.BakedCacheKey key = new ModelBakery.BakedCacheKey(resourceLocation, modelState.getRotation(), modelState.isUvLocked());
        BakedModel bakedModel = this.bakedCache.get(key);
        if (bakedModel == null) {
            try {
                bakedModel = this.bakeUncached(unbakedModel, modelState, modelTextureGetter);
            } catch (Exception exception) {
                PuzzlesLib.LOGGER.warn("Unable to bake model: '{}': {}", resourceLocation, exception);
                bakedModel = this.missingModel;
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
