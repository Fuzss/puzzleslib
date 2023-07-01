package fuzs.puzzleslib.api.client.init.v1;

import fuzs.puzzleslib.impl.client.core.ClientFactories;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Allows for registering model overrides for items that have a different model in {@link net.minecraft.client.renderer.entity.ItemRenderer} depending on {@link ItemDisplayContext} like {@link net.minecraft.world.item.Items#TRIDENT} and {@link net.minecraft.world.item.Items#SPYGLASS} in vanilla.
 * <p>TODO rename to ItemModelDisplayOverrides
 */
public interface ItemModelOverrides {
    ItemModelOverrides INSTANCE = ClientFactories.INSTANCE.getItemModelDisplayOverrides();

    /**
     * Register an item model override for an item.
     *
     * @param item                the item to register overrides for
     * @param itemModel           the default item model location using the built-in item model template, usually used for the item's appearance in guis and when dropped on the ground
     * @param itemModelOverride   the custom model, usually used for in-hand rendering
     * @param itemDisplayContexts transform types to keep the default <code>itemModel</code> for
     */
    @Deprecated(forRemoval = true)
    default void register(Item item, ModelResourceLocation itemModel, ModelResourceLocation itemModelOverride, ItemDisplayContext... itemDisplayContexts) {
        this.register(itemModel, itemModelOverride, itemDisplayContexts);
    }

    /**
     * Register an item model override for an item.
     *
     * @param itemModel         the default item model location using the built-in item model template, usually used for the item's appearance in guis and when dropped on the ground
     * @param itemModelOverride the custom model, usually used for in-hand rendering
     * @param contexts          transform types to keep the default <code>itemModel</code> for
     */
    void register(ModelResourceLocation itemModel, ModelResourceLocation itemModelOverride, ItemDisplayContext... contexts);
}
