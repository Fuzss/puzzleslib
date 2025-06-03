package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.init.v1.ItemStackDecorator;
import net.minecraft.world.level.ItemLike;

/**
 * Register additional renderers to run after stack count and durability have been drawn for an item stack.
 */
@FunctionalInterface
public interface ItemDecorationsContext {

    /**
     * Register a {@link ItemStackDecorator} for an item.
     *
     * @param itemLike           the item to draw for
     * @param itemStackDecorator the renderer implementation
     */
    void registerItemStackDecorator(ItemLike itemLike, ItemStackDecorator itemStackDecorator);
}
