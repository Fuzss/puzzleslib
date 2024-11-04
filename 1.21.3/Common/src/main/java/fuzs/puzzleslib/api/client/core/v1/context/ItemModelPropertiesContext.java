package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

/**
 * register model predicates for custom item models
 */
public interface ItemModelPropertiesContext {

    /**
     * register a predicate for all items
     *
     * @param identifier predicate name
     * @param function   handler for this predicate
     */
    void registerGlobalProperty(ResourceLocation identifier, ClampedItemPropertyFunction function);

    /**
     * register a predicate for an <code>item</code>
     *
     * @param identifier predicate name
     * @param function   handler for this predicate
     * @param items      items to apply the model property to
     */
    void registerItemProperty(ResourceLocation identifier, ClampedItemPropertyFunction function, ItemLike... items);
}
