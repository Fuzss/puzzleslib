package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.init.v1.DynamicItemDecorator;
import net.minecraft.world.level.ItemLike;

/**
 * register additional renders to run after stack count and durability have been drawn for an item stack
 */
@FunctionalInterface
public interface ItemDecorationContext {

    /**
     * register a {@link DynamicItemDecorator} for an <code>item</code>
     *
     * @param decorator renderer implementation
     * @param items   items to draw for
     */
    void registerItemDecorator(DynamicItemDecorator decorator, ItemLike... items);
}
