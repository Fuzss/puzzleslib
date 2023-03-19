package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.init.v1.DynamicBuiltinItemRenderer;
import fuzs.puzzleslib.api.core.v1.context.MultiRegistrationContext;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public record BuiltinModelItemRendererContextFabricImpl(List<ResourceManagerReloadListener> dynamicBuiltinModelItemRenderers) implements BuiltinModelItemRendererContext, MultiRegistrationContext<ItemLike, DynamicBuiltinItemRenderer> {

    @Override
    public void registerItemRenderer(DynamicBuiltinItemRenderer renderer, ItemLike object, ItemLike... objects) {
        this.register(renderer, object, objects);
        this.dynamicBuiltinModelItemRenderers.add(renderer);
    }

    @Override
    public void register(ItemLike object, DynamicBuiltinItemRenderer type) {
        BuiltinItemRendererRegistry.INSTANCE.register(object, type::renderByItem);
    }
}
