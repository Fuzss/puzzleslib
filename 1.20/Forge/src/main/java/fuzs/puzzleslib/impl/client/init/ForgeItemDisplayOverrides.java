package fuzs.puzzleslib.impl.client.init;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public final class ForgeItemDisplayOverrides extends ItemDisplayOverridesImpl {

    {
        IEventBus eventBus = ModContainerHelper.findModEventBus(PuzzlesLib.MOD_ID).orElseThrow();
        eventBus.addListener(this::onModifyBakingResult);
    }

    private void onModifyBakingResult(final ModelEvent.ModifyBakingResult evt) {
        for (Map.Entry<ModelResourceLocation, Map<ItemDisplayContext, ModelResourceLocation>> entry : this.overrideLocations.entrySet()) {
            BakedModel itemModel = evt.getModels().get(entry.getKey());
            Objects.requireNonNull(itemModel, "item model is null");
            evt.getModels().put(entry.getKey(), new BakedModelWrapper<>(itemModel) {
                private final Map<ItemDisplayContext, BakedModel> overrides = Stream.of(ItemDisplayContext.values()).collect(Maps.<ItemDisplayContext, ItemDisplayContext, BakedModel>toImmutableEnumMap(Function.identity(), context -> {
                    if (entry.getValue().containsKey(context)) {
                        BakedModel itemModelOverride = evt.getModels().get(entry.getValue().get(context));
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
    }
}
