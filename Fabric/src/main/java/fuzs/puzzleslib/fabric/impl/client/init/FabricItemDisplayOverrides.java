package fuzs.puzzleslib.fabric.impl.client.init;

import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class FabricItemDisplayOverrides extends ItemDisplayOverridesImpl<BakedModel> {
    private Map<BakedModel, Map<ItemDisplayContext, BakedModel>> overrideModels = Collections.emptyMap();

    @Override
    public void register(ModelResourceLocation itemModel, ModelResourceLocation itemModelOverride, ItemDisplayContext... itemDisplayContexts) {
        Objects.requireNonNull(itemModelOverride, "item model override is null");
        this.register(itemModel, (BakedModelResolver modelResolver) -> modelResolver.getModel(itemModelOverride),
                itemDisplayContexts
        );
    }

    @Override
    public void register(ModelResourceLocation itemModel, ResourceLocation itemModelOverride, ItemDisplayContext... itemDisplayContexts) {
        Objects.requireNonNull(itemModelOverride, "item model override is null");
        this.register(itemModel, (BakedModelResolver modelResolver) -> modelResolver.getModel(itemModelOverride),
                itemDisplayContexts
        );
    }

    @Override
    protected BakedModel createOverrideModelKey(ModelResourceLocation modelResourceLocation, BakedModel itemModel) {
        return itemModel;
    }

    @Override
    protected void registerEventHandlers() {
        // we cannot use Fabric's ForwardingBakedModel as it does not include the current ItemDisplayContext
        FabricClientEvents.COMPLETE_MODEL_LOADING.register(
                (Supplier<ModelManager> modelManagerSupplier, Supplier<ModelBakery> modelBakerySupplier) -> {
                    BakedModel missingModel = modelManagerSupplier.get().getModel(MissingBlockModel.VARIANT);
                    Objects.requireNonNull(missingModel, "missing model is null");
                    this.overrideModels = this.computeOverrideModels(new BakedModelResolver() {
                        @Override
                        public BakedModel getModel(ModelResourceLocation modelResourceLocation) {
                            return modelManagerSupplier.get().getModel(modelResourceLocation);
                        }

                        @Override
                        public BakedModel getModel(ResourceLocation resourceLocation) {
                            return modelManagerSupplier.get().getModel(resourceLocation);
                        }
                    }, missingModel);
                });
    }

    public BakedModel getItemModelDisplayOverride(BakedModel itemModel, ItemDisplayContext itemDisplayContext) {
        return this.overrideModels.getOrDefault(itemModel, Collections.emptyMap()).getOrDefault(itemDisplayContext,
                itemModel
        );
    }
}
