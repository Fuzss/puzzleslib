package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.init.v1.DynamicBuiltinItemRenderer;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerImpl;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Objects;

public record BuiltinModelItemRendererContextFabricImpl(String modId, List<PreparableReloadListener> dynamicRenderers) implements BuiltinModelItemRendererContext {

    @Override
    public void registerItemRenderer(DynamicBuiltinItemRenderer renderer, ItemLike... items) {
        Objects.requireNonNull(renderer, "renderer is null");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkPositionIndex(1, items.length, "items is empty");
        for (ItemLike item : items) {
            Objects.requireNonNull(item, "item is null");
            BuiltinItemRendererRegistry.INSTANCE.register(item, renderer::renderByItem);
        }
        // store this to enable listening to resource reloads
        String itemName = BuiltInRegistries.ITEM.getKey(items[0].asItem()).getPath();
        ResourceLocation identifier = new ResourceLocation(this.modId, itemName + "_built_in_model_renderer");
        this.dynamicRenderers.add(new ForwardingReloadListenerImpl(identifier, renderer));
    }
}
