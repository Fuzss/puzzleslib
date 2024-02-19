package fuzs.puzzleslib.fabric.impl.client.init;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.fabric.api.core.v1.resources.FabricReloadListener;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public final class FabricItemDisplayOverrides extends ItemDisplayOverridesImpl {
    @Nullable
    private Map<BakedModel, Map<ItemDisplayContext, BakedModel>> overrideModels;

    {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricReloadListener(PuzzlesLib.id("item_display_overrides"), (ResourceManager resourceManager) -> {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            this.overrideModels = Maps.newIdentityHashMap();
            for (Map.Entry<ModelResourceLocation, Map<ItemDisplayContext, ModelResourceLocation>> entry : this.overrideLocations.entrySet()) {
                BakedModel itemModel = modelManager.getModel(entry.getKey());
                Objects.requireNonNull(itemModel, "item model is null");
                this.overrideModels.put(itemModel, entry.getValue().entrySet().stream().collect(Maps.toImmutableEnumMap(Map.Entry::getKey, t -> {
                    BakedModel itemModelOverride = modelManager.getModel(t.getValue());
                    Objects.requireNonNull(itemModelOverride, "item model override is null");
                    return itemModelOverride;
                })));
            }
        }));
    }

    public BakedModel getItemModelDisplayOverride(BakedModel itemModel, ItemDisplayContext itemDisplayContext) {
        // if this class was only loaded by the item renderer mixin because no overrides have been registered by any mod this will still be null
        if (this.overrideModels == null) return itemModel;
        Map<ItemDisplayContext, BakedModel> overrides = this.overrideModels.get(itemModel);
        return overrides != null ? overrides.getOrDefault(itemDisplayContext, itemModel) : itemModel;
    }
}
