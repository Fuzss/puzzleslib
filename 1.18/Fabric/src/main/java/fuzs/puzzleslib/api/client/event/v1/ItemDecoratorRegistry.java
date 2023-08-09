package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.impl.client.event.ItemDecoratorRegistryImpl;
import fuzs.puzzleslib.api.client.init.v1.DynamicItemDecorator;
import net.minecraft.world.level.ItemLike;

/**
 * This registry holds {@linkplain DynamicItemDecorator item decorators}.
 */
public interface ItemDecoratorRegistry {
    /**
     * The singleton instance of the decorator registry.
     * Use this instance to call the methods in this interface.
     */
    ItemDecoratorRegistry INSTANCE = new ItemDecoratorRegistryImpl();

    /**
     * register a {@link DynamicItemDecorator} for an <code>item</code>
     *
     * @param item              the item to draw for
     * @param itemDecorator     renderer implementation
     */
    void register(ItemLike item, DynamicItemDecorator itemDecorator);
}
