package fuzs.puzzleslib.api.client.init.v1;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Allows for registering model overrides for items that have a different model in {@link net.minecraft.client.renderer.entity.ItemRenderer} depending on {@link ItemDisplayContext} like {@link net.minecraft.world.item.Items#TRIDENT} and {@link net.minecraft.world.item.Items#SPYGLASS} in vanilla.
 */
@Deprecated(forRemoval = true)
public interface ItemModelOverrides {
    ItemModelOverrides INSTANCE = (Item item, ModelResourceLocation itemModel, ModelResourceLocation itemModelOverride, ItemTransforms.TransformType... defaultContexts) -> {
        // not so sure if turning this method default would result in a binary change when calling the method, so just leaving it like this until it is removed
        ItemModelDisplayOverrides.INSTANCE.register(itemModel, itemModelOverride, defaultContexts);
    };

    /**
     * Register an item model override for an item.
     *
     * @param item              the item to register overrides for
     * @param itemModel         the default item model location using the built-in item model template, usually used for the item's appearance in guis and when dropped on the ground
     * @param itemModelOverride the custom model, usually used for in-hand rendering
     * @param defaultContexts   item display contexts to keep the default <code>itemModel</code> for
     */
    void register(Item item, ModelResourceLocation itemModel, ModelResourceLocation itemModelOverride, ItemTransforms.TransformType... defaultContexts);
}
