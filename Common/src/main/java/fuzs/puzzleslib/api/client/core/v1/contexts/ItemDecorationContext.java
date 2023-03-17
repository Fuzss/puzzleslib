package fuzs.puzzleslib.api.client.core.v1.contexts;

import fuzs.puzzleslib.api.client.registration.v1.DynamicItemDecorator;
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
     * @param object    the item to draw for
     * @param objects   more items to draw for
     */
    void registerItemDecorator(DynamicItemDecorator decorator, ItemLike object, ItemLike... objects);
}
