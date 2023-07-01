package fuzs.puzzleslib.impl.client.init;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.client.event.v1.ModelEvents;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.Map;
import java.util.Objects;

public final class FabricItemDisplayOverrides extends ItemDisplayOverridesImpl {
    private Map<BakedModel, Map<ItemDisplayContext, BakedModel>> overrideModels;

    {
        ModelEvents.MODIFY_BAKING_RESULT.register((Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) -> {
            this.overrideModels = Maps.newIdentityHashMap();
            for (Map.Entry<ModelResourceLocation, Map<ItemDisplayContext, ModelResourceLocation>> entry : super.overrideLocations.entrySet()) {
                BakedModel itemModel = models.get(entry.getKey());
                Objects.requireNonNull(itemModel, "item model is null");
                this.overrideModels.put(itemModel, entry.getValue().entrySet().stream().collect(Maps.toImmutableEnumMap(Map.Entry::getKey, t -> {
                    BakedModel itemModelOverride = models.get(t.getValue());
                    Objects.requireNonNull(itemModelOverride, "item model override is null");
                    return itemModelOverride;
                })));
            }
        });
    }

    public BakedModel getItemModelDisplayOverride(BakedModel itemModel, ItemDisplayContext itemDisplayContext) {
        Objects.requireNonNull(this.overrideModels, "overrides is null");
        Map<ItemDisplayContext, BakedModel> overrides = this.overrideModels.get(itemModel);
        return overrides != null ? overrides.getOrDefault(itemDisplayContext, itemModel) : itemModel;
    }
}
