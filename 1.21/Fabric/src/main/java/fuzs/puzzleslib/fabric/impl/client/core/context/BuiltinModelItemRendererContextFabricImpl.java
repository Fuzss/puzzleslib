package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.init.v1.BuiltinItemRenderer;
import fuzs.puzzleslib.api.client.init.v1.ReloadingBuiltInItemRenderer;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Objects;

public record BuiltinModelItemRendererContextFabricImpl(String modId, List<ResourceManagerReloadListener> dynamicRenderers) implements BuiltinModelItemRendererContext {

    @Override
    public void registerItemRenderer(BuiltinItemRenderer renderer, ItemLike... items) {
        Objects.requireNonNull(renderer, "renderer is null");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkState(items.length > 0, "items is empty");
        for (ItemLike item : items) {
            Objects.requireNonNull(item, "item is null");
            BuiltinItemRendererRegistry.INSTANCE.register(item, renderer::renderByItem);
        }
    }

    @Override
    public void registerItemRenderer(ReloadingBuiltInItemRenderer renderer, ItemLike... items) {
        this.registerItemRenderer((BuiltinItemRenderer) renderer, items);
        // store this to enable listening to resource reloads
        String itemName = BuiltInRegistries.ITEM.getKey(items[0].asItem()).getPath();
        ResourceLocation resourceLocation = ResourceLocationHelper.fromNamespaceAndPath(this.modId, itemName + "_built_in_model_renderer");
        this.dynamicRenderers.add(ForwardingReloadListenerHelper.fromResourceManagerReloadListener(resourceLocation, renderer));
    }
}
