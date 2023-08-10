package fuzs.puzzleslib.impl.client.init;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.client.event.v1.ModelEvents;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class FabricItemDisplayOverrides extends ItemDisplayOverridesImpl {
    @Nullable
    private Map<BakedModel, Map<ItemTransforms.TransformType, BakedModel>> overrideModels;

    {
        ModelEvents.modifyBakingResult(PuzzlesLib.MOD_ID).register((Map<ResourceLocation, BakedModel> models, Supplier<ModelBakery> modelBakery) -> {
            this.overrideModels = Maps.newIdentityHashMap();
            for (Map.Entry<ModelResourceLocation, Map<ItemTransforms.TransformType, ModelResourceLocation>> entry : super.overrideLocations.entrySet()) {
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

    public BakedModel getItemModelDisplayOverride(BakedModel itemModel, ItemTransforms.TransformType itemDisplayContext) {
        // if this class was only loaded by the item renderer mixin because no overrides have been registered by any mod this will still be null
        if (this.overrideModels == null) return itemModel;
        Map<ItemTransforms.TransformType, BakedModel> overrides = this.overrideModels.get(itemModel);
        return overrides != null ? overrides.getOrDefault(itemDisplayContext, itemModel) : itemModel;
    }
}
