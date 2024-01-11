package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.init.v1.DynamicBuiltinItemRenderer;
import net.minecraft.world.level.ItemLike;

/**
 * register a custom inventory renderer for an item belonging to a block entity
 */
@FunctionalInterface
public interface BuiltinModelItemRendererContext {

    /**
     * register a <code>renderer</code> for an <code>item</code>
     *
     * @param renderer dynamic implementation of {@link net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer}
     * @param items    items to register for
     */
    void registerItemRenderer(DynamicBuiltinItemRenderer renderer, ItemLike... items);
}
