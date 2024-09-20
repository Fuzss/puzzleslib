package fuzs.puzzleslib.neoforge.impl.client.init;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

import java.util.Map;
import java.util.Objects;

public final class NeoForgeItemDisplayOverrides extends ItemDisplayOverridesImpl<NeoForgeItemDisplayOverrides.BakedModelKey> {

    @Override
    public void register(ModelResourceLocation itemModel, ModelResourceLocation itemModelOverride, ItemDisplayContext... defaultContexts) {
        Objects.requireNonNull(itemModelOverride, "item model override is null");
        this.register(itemModel, (BakedModelResolver modelResolver) -> modelResolver.getModel(itemModelOverride),
                defaultContexts
        );
    }

    @Override
    public void register(ModelResourceLocation itemModel, ResourceLocation itemModelOverride, ItemDisplayContext... defaultContexts) {
        Objects.requireNonNull(itemModelOverride, "item model override is null");
        this.register(itemModel, (BakedModelResolver modelResolver) -> modelResolver.getModel(
                ModelResourceLocation.standalone(itemModelOverride)), defaultContexts);
    }

    @Override
    protected BakedModelKey createOverrideModelKey(ModelResourceLocation modelResourceLocation, BakedModel itemModel) {
        return new BakedModelKey(modelResourceLocation, itemModel);
    }

    @Override
    protected void registerEventHandlers() {
        NeoForgeModContainerHelper.getOptionalModEventBus(PuzzlesLib.MOD_ID).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final ModelEvent.ModifyBakingResult evt) -> {
                BakedModel missingModel = evt.getModels().get(ModelBakery.MISSING_MODEL_VARIANT);
                Objects.requireNonNull(missingModel, "missing model is null");
                Map<BakedModelKey, Map<ItemDisplayContext, BakedModel>> overrideModels = this.computeOverrideModels(
                        new BakedModelResolver() {

                            @Override
                            public BakedModel getModel(ModelResourceLocation modelResourceLocation) {
                                return evt.getModels().getOrDefault(modelResourceLocation, missingModel);
                            }

                            @Override
                            public BakedModel getModel(ResourceLocation resourceLocation) {
                                return evt.getModels().getOrDefault(ModelResourceLocation.standalone(resourceLocation),
                                        missingModel
                                );
                            }
                        }, missingModel);
                for (Map.Entry<BakedModelKey, Map<ItemDisplayContext, BakedModel>> entry : overrideModels.entrySet()) {
                    evt.getModels().put(entry.getKey().modelResourceLocation(), new BakedModelWrapper<>(entry.getKey().bakedModel()) {

                        @Override
                        public BakedModel applyTransform(ItemDisplayContext itemDisplayContext, PoseStack poseStack, boolean applyLeftHandTransform) {
                            return entry.getValue().getOrDefault(itemDisplayContext, this.originalModel).applyTransform(
                                    itemDisplayContext, poseStack, applyLeftHandTransform);
                        }
                    });
                }
            });
        });
    }

    protected record BakedModelKey(ModelResourceLocation modelResourceLocation, BakedModel bakedModel) {

    }
}
