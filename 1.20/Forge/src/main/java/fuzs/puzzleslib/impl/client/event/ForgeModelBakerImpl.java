package fuzs.puzzleslib.impl.client.event;

import com.mojang.math.Transformation;
import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record ForgeModelBakerImpl(Map<ModelBakingKey, BakedModel> bakedCache, Function<ResourceLocation, UnbakedModel> unbakedModelGetter,
                                  Function<Material, TextureAtlasSprite> modelTextureGetter, BakedModel missingModel) implements ModelBaker {
    private static Map<ResourceLocation, AtlasSet.StitchResult> capturedAtlasPreparations;

    public ForgeModelBakerImpl(ResourceLocation modelLocation, Map<ModelBakingKey, BakedModel> bakedCache, Function<ResourceLocation, UnbakedModel> modelGetter, BiConsumer<ResourceLocation, Material> missingTextureConsumer, BakedModel missingModel) {
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
        // our cache works differently from vanilla as we use the unbaked model instance and not the current resource location
        // vanilla bakes most unbaked models many times despite the outcome being the same just because the resource location is different
        // haven't found an issue with this approach so far so the Forge implementation will stick with it for now
        return this.bakedCache.computeIfAbsent(new ModelBakingKey(unbakedModel, modelState.getRotation(), modelState.isUvLocked()), key -> {
            try {
                return key.unbakedModel().bake(this, modelTextureGetter, modelState, resourceLocation);
            } catch (Exception exception) {
                PuzzlesLib.LOGGER.warn("Unable to bake model: '{}': {}", resourceLocation, exception);
                return this.missingModel;
            }
        });
    }

    @Override
    public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
        return this.modelTextureGetter;
    }

    public record ModelBakingKey(UnbakedModel unbakedModel, Transformation rotation, boolean isUvLocked) {

    }
}
