package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.init.v1.DynamicBuiltinItemRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Objects;

public record BuiltinModelItemRendererContextFabricImpl(List<ResourceManagerReloadListener> dynamicBuiltinModelItemRenderers) implements BuiltinModelItemRendererContext {

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
        this.dynamicBuiltinModelItemRenderers.add(renderer);
    }
}
