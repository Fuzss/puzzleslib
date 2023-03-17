package fuzs.puzzleslib.impl.client.core.contexts;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.api.client.core.v1.contexts.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.registration.v1.DynamicBuiltinModelItemRenderer;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.core.FabricResourceReloadListener;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Objects;

public record BuiltinModelItemRendererContextFabricImpl(String modId,
                                                        List<ResourceManagerReloadListener> dynamicBuiltinModelItemRenderers) implements BuiltinModelItemRendererContext {

    public BuiltinModelItemRendererContextFabricImpl(String modId) {
        this(modId, Lists.newArrayList());
    }

    @Override
    public void registerItemRenderer(DynamicBuiltinModelItemRenderer renderer, ItemLike object, ItemLike... objects) {
        Objects.requireNonNull(renderer, "renderer is null");
        this.dynamicBuiltinModelItemRenderers.add(renderer);
        Objects.requireNonNull(object, "item is null");
        BuiltinItemRendererRegistry.INSTANCE.register(object, renderer::renderByItem);
        Objects.requireNonNull(objects, "items is null");
        for (ItemLike item : objects) {
            Objects.requireNonNull(item, "item is null");
            BuiltinItemRendererRegistry.INSTANCE.register(item, renderer::renderByItem);
        }
    }

    public void apply() {
        if (this.dynamicBuiltinModelItemRenderers.isEmpty()) return;
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricResourceReloadListener(this.modId, "built_in_model_item_renderers", (ResourceManagerReloadListener) (ResourceManager resourceManager) -> {
            for (ResourceManagerReloadListener listener : this.dynamicBuiltinModelItemRenderers) {
                try {
                    listener.onResourceManagerReload(resourceManager);
                } catch (Exception e) {
                    PuzzlesLib.LOGGER.error("Unable to execute dynamic built-in model item renderers reload provided by {}", this.modId, e);
                }
            }
        }));
    }
}
