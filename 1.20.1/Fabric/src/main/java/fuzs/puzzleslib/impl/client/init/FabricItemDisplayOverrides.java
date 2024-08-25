package fuzs.puzzleslib.impl.client.init;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.client.event.v1.ModelEvents;
import fuzs.puzzleslib.api.core.v1.resources.FabricReloadListener;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class FabricItemDisplayOverrides extends ItemDisplayOverridesImpl {
    @Nullable
    private Map<BakedModel, Map<ItemDisplayContext, BakedModel>> overrideModels;

    {
        ModelEvents.BAKING_COMPLETED.register(
                (Supplier<ModelManager> modelManager, Map<ResourceLocation, BakedModel> models, Supplier<ModelBakery> modelBakery) -> {
                    this.overrideModels = Maps.newIdentityHashMap();
                    for (Map.Entry<ModelResourceLocation, Map<ItemDisplayContext, ModelResourceLocation>> overrideEntry : this.overrideLocations.entrySet()) {
                        BakedModel itemModel = models.get(overrideEntry.getKey());
                        Objects.requireNonNull(itemModel, "item model is null");
                        this.overrideModels.put(itemModel, overrideEntry.getValue()
                                .entrySet()
                                .stream()
                                .collect(Maps.toImmutableEnumMap(Map.Entry::getKey,
                                        (Map.Entry<ItemDisplayContext, ModelResourceLocation> entry) -> {
                                            BakedModel itemModelOverride = models.get(entry.getValue());
                                            Objects.requireNonNull(itemModelOverride, "item model override is null");
                                            return itemModelOverride;
                                        }
                                )));
                    }
                });
    }

    @Override
    public BakedModel getItemModelDisplayOverride(BakedModel itemModel, ItemDisplayContext itemDisplayContext) {
        // if this class was only loaded by the item renderer mixin because no overrides have been registered by any mod this will still be null
        if (this.overrideModels == null) return itemModel;
        Map<ItemDisplayContext, BakedModel> overrides = this.overrideModels.get(itemModel);
        return overrides != null ? overrides.getOrDefault(itemDisplayContext, itemModel) : itemModel;
    }
}
