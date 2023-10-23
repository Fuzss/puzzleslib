package fuzs.puzzleslib.impl.client.event;

import com.mojang.math.Transformation;
import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.mixin.client.accessor.ModelBakeryAccessor;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record ForgeModelBakerImpl(Map<BakedCacheKey, BakedModel> bakedCache, Function<ResourceLocation, UnbakedModel> unbakedModelGetter,
                                  Function<Material, TextureAtlasSprite> modelTextureGetter, BakedModel missingModel) implements ModelBaker {
    private static Map<ResourceLocation, AtlasSet.StitchResult> capturedAtlasPreparations;

    public ForgeModelBakerImpl(ResourceLocation modelLocation, Map<BakedCacheKey, BakedModel> bakedCache, Function<ResourceLocation, UnbakedModel> modelGetter, BiConsumer<ResourceLocation, Material> missingTextureConsumer, BakedModel missingModel) {
        this(bakedCache, modelGetter, (Material material) -> {
            Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations = capturedAtlasPreparations;
            Objects.requireNonNull(atlasPreparations, "atlas preparations is null");
            AtlasSet.StitchResult stitchResult = atlasPreparations.get(material.atlasLocation());
            TextureAtlasSprite textureatlassprite = stitchResult.getSprite(material.texture());
            if (textureatlassprite != null) {
                return textureatlassprite;
            } else {
                missingTextureConsumer.accept(modelLocation, material);
                return stitchResult.missing();
            }
        }, missingModel);
    }

    public static void setAtlasPreparations(Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations) {
        ForgeModelBakerImpl.capturedAtlasPreparations = atlasPreparations;
    }

    static {
        ModContainerHelper.getModEventBus(PuzzlesLib.MOD_ID).addListener((final ModelEvent.BakingCompleted evt) -> {
            capturedAtlasPreparations = null;
        });
    }

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
    public @Nullable BakedModel bake(ResourceLocation resourceLocation, ModelState modelState, Function<Material, TextureAtlasSprite> modelTextureGetter) {
        return this.bake(this.getModel(resourceLocation), resourceLocation, modelState, modelTextureGetter);
    }

    public BakedModel bake(UnbakedModel unbakedModel, ResourceLocation resourceLocation) {
        return this.bake(unbakedModel, resourceLocation, BlockModelRotation.X0_Y0, this.modelTextureGetter);
    }

    private BakedModel bake(UnbakedModel unbakedModel, ResourceLocation resourceLocation, ModelState modelState, Function<Material, TextureAtlasSprite> modelTextureGetter) {
        // implementation is pretty much the same as the vanilla model baker in the model bakery
        // do not use Map::computeIfAbsent, it will throw ConcurrentModificationException due to the map potentially being modified in the provided Function
        BakedCacheKey key = new BakedCacheKey(resourceLocation, modelState.getRotation(), modelState.isUvLocked());
        BakedModel bakedModel = this.bakedCache.get(key);
        if (bakedModel == null) {
            if (unbakedModel instanceof BlockModel blockModel && blockModel.getRootModel() == ModelBakery.GENERATION_MARKER) {
                return ModelBakeryAccessor.puzzleslib$getItemModelGenerator().generateBlockModel(modelTextureGetter, blockModel).bake(this, blockModel, modelTextureGetter, modelState, resourceLocation, false);
            }
            try {
                bakedModel = unbakedModel.bake(this, modelTextureGetter, modelState, resourceLocation);
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

    /**
     * Copied from {@link ModelBakery.BakedCacheKey}.
     */
    public record BakedCacheKey(ResourceLocation resourceLocation, Transformation rotation, boolean isUvLocked) {

    }
}
