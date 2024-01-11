package fuzs.puzzleslib.fabric.api.client.event.v1;

import fuzs.puzzleslib.api.client.init.v1.DynamicItemDecorator;
import fuzs.puzzleslib.fabric.impl.client.event.ItemDecoratorRegistryImpl;
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
