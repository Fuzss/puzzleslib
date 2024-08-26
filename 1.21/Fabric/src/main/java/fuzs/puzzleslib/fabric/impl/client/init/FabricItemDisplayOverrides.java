package fuzs.puzzleslib.fabric.impl.client.init;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class FabricItemDisplayOverrides extends ItemDisplayOverridesImpl {
    @Nullable
    private Map<BakedModel, Map<ItemDisplayContext, BakedModel>> overrideModels;

    {
        FabricClientEvents.COMPLETE_MODEL_LOADING.register(
                (Supplier<ModelManager> modelManager, Supplier<ModelBakery> modelBakery) -> {
                    this.overrideModels = new IdentityHashMap<>();
                    for (Map.Entry<ModelResourceLocation, Map<ItemDisplayContext, ModelResourceLocation>> overrideEntry : this.overrideLocations.entrySet()) {
                        BakedModel itemModel = modelManager.get().getModel(overrideEntry.getKey());
                        Objects.requireNonNull(itemModel, "item model is null");
                        this.overrideModels.put(itemModel, overrideEntry.getValue()
                                .entrySet()
                                .stream()
                                .collect(Maps.toImmutableEnumMap(Map.Entry::getKey,
                                        (Map.Entry<ItemDisplayContext, ModelResourceLocation> entry) -> {
                                            BakedModel itemModelOverride = modelManager.get().getModel(
                                                    entry.getValue());
                                            Objects.requireNonNull(itemModelOverride,
                                                    "item model override is null"
                                            );
                                            return itemModelOverride;
                                        }
                                )));
                    }
        });
    }

    public BakedModel getItemModelDisplayOverride(BakedModel itemModel, ItemDisplayContext itemDisplayContext) {
        // if this class was only loaded by the item renderer mixin because no overrides have been registered by any mod this will still be null
        if (this.overrideModels == null) return itemModel;
        Map<ItemDisplayContext, BakedModel> overrides = this.overrideModels.get(itemModel);
        return overrides != null ? overrides.getOrDefault(itemDisplayContext, itemModel) : itemModel;
    }
}
