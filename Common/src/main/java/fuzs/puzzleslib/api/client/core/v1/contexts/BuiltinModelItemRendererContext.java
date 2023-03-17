package fuzs.puzzleslib.api.client.core.v1.contexts;

import fuzs.puzzleslib.api.client.registration.v1.DynamicBuiltinModelItemRenderer;
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
     * @param object   the item to register for
     * @param objects  more items to register for
     */
    void registerItemRenderer(DynamicBuiltinModelItemRenderer renderer, ItemLike object, ItemLike... objects);
}
