package fuzs.puzzleslib.api.client.renderer;

import fuzs.puzzleslib.impl.client.renderer.ItemDecoratorRegistryImpl;
import fuzs.puzzleslib.client.renderer.entity.DynamicItemDecorator;
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
