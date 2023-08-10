package fuzs.puzzleslib.impl.client.init;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.event.v1.ModelEvents;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.BakedModelWrapper;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class ForgeItemDisplayOverrides extends ItemDisplayOverridesImpl {

    {
        ModelEvents.modifyBakingResult(PuzzlesLib.MOD_ID).register((Map<ResourceLocation, BakedModel> models, Supplier<ModelBakery> modelBakery) -> {
            for (Map.Entry<ModelResourceLocation, Map<ItemTransforms.TransformType, ModelResourceLocation>> entry : this.overrideLocations.entrySet()) {
                BakedModel itemModel = models.get(entry.getKey());
                Objects.requireNonNull(itemModel, "item model is null");
                models.put(entry.getKey(), new BakedModelWrapper<>(itemModel) {
                    private final Map<ItemTransforms.TransformType, BakedModel> overrides = Stream.of(ItemTransforms.TransformType.values()).collect(Maps.<ItemTransforms.TransformType, ItemTransforms.TransformType, BakedModel>toImmutableEnumMap(Function.identity(), context -> {
                        if (entry.getValue().containsKey(context)) {
                            BakedModel itemModelOverride = models.get(entry.getValue().get(context));
                            Objects.requireNonNull(itemModelOverride, "item model override is null");
                            return itemModelOverride;
                        }
                        return itemModel;
                    }));

                    @Override
                    public BakedModel handlePerspective(ItemTransforms.TransformType itemDisplayContext, PoseStack poseStack) {
                        return this.overrides.get(itemDisplayContext).handlePerspective(itemDisplayContext, poseStack);
                    }
                });
            }
        });
    }
}
