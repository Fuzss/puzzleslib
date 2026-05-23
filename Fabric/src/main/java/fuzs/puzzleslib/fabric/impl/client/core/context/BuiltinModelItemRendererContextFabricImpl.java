package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.init.v1.BuiltinItemRenderer;
import fuzs.puzzleslib.api.client.init.v1.ReloadingBuiltInItemRenderer;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Objects;

public record BuiltinModelItemRendererContextFabricImpl(String modId,
                                                        List<ResourceManagerReloadListener> dynamicRenderers) implements BuiltinModelItemRendererContext {

    @Override
    public void registerItemRenderer(Item item, BuiltinItemRenderer itemRenderer) {
        Objects.requireNonNull(item, "item is null");
        Objects.requireNonNull(itemRenderer, "renderer is null");
        BuiltinItemRendererRegistry.INSTANCE.register(item, itemRenderer::renderByItem);
    }

    @Override
    public void registerItemRenderer(Item item, ReloadingBuiltInItemRenderer itemRenderer) {
        this.registerItemRenderer(item, (BuiltinItemRenderer) itemRenderer);
        // store this to enable listening to resource reloads
        String itemName = BuiltInRegistries.ITEM.getKey(item).getPath();
        ResourceLocation resourceLocation = ResourceLocationHelper.fromNamespaceAndPath(this.modId,
                itemName + "_built_in_model_renderer");
        this.dynamicRenderers.add(ForwardingReloadListenerHelper.fromResourceManagerReloadListener(resourceLocation,
                itemRenderer));
    }
}
