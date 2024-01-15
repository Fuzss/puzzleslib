package fuzs.puzzleslib.neoforge.impl.client.init;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public final class NeoForgeItemDisplayOverrides extends ItemDisplayOverridesImpl {

    {
        NeoForge.EVENT_BUS.addListener((final ModelEvent.ModifyBakingResult evt) -> {
            Map<ResourceLocation, BakedModel> models = evt.getModels();
            for (Map.Entry<ModelResourceLocation, Map<ItemDisplayContext, ModelResourceLocation>> entry : this.overrideLocations.entrySet()) {
                BakedModel itemModel = models.get(entry.getKey());
                Objects.requireNonNull(itemModel, "item model is null");
                models.put(entry.getKey(), new BakedModelWrapper<>(itemModel) {
                    private final Map<ItemDisplayContext, BakedModel> overrides = Stream.of(ItemDisplayContext.values()).collect(Maps.<ItemDisplayContext, ItemDisplayContext, BakedModel>toImmutableEnumMap(Function.identity(), context -> {
                        if (entry.getValue().containsKey(context)) {
                            BakedModel itemModelOverride = models.get(entry.getValue().get(context));
                            Objects.requireNonNull(itemModelOverride, "item model override is null");
                            return itemModelOverride;
                        }
                        return itemModel;
                    }));

                    @Override
                    public BakedModel applyTransform(ItemDisplayContext itemDisplayContext, PoseStack poseStack, boolean applyLeftHandTransform) {
                        return this.overrides.get(itemDisplayContext).applyTransform(itemDisplayContext, poseStack, applyLeftHandTransform);
                    }
                });
            }
        });
    }
}
