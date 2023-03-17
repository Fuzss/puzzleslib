package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.registration.v1.DynamicBuiltinModelItemRenderer;
import fuzs.puzzleslib.api.core.v1.contexts.MultiRegistrationContext;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public record BuiltinModelItemRendererContextFabricImpl(List<ResourceManagerReloadListener> dynamicBuiltinModelItemRenderers) implements BuiltinModelItemRendererContext, MultiRegistrationContext<ItemLike, DynamicBuiltinModelItemRenderer> {

    @Override
    public void registerItemRenderer(DynamicBuiltinModelItemRenderer renderer, ItemLike object, ItemLike... objects) {
        this.register(renderer, object, objects);
        this.dynamicBuiltinModelItemRenderers.add(renderer);
    }

    @Override
    public void register(ItemLike object, DynamicBuiltinModelItemRenderer type) {
        BuiltinItemRendererRegistry.INSTANCE.register(object, type::renderByItem);
    }
}
