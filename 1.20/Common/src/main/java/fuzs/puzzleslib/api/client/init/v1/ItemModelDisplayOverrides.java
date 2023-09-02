package fuzs.puzzleslib.api.client.init.v1;

import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Allows for registering model overrides for items that have a different model in {@link net.minecraft.client.renderer.entity.ItemRenderer}
 * depending on {@link ItemDisplayContext} like {@link net.minecraft.world.item.Items#TRIDENT} and {@link net.minecraft.world.item.Items#SPYGLASS} in vanilla.
 */
public interface ItemModelDisplayOverrides {
    ItemModelDisplayOverrides INSTANCE = ItemDisplayOverridesImpl.INSTANCE;

    /**
     * Register an item model override for an item.
     *
     * @param itemModel         the default item model location using the built-in item model template, usually used for the item's appearance in guis and when dropped on the ground
     * @param itemModelOverride the custom model, usually used for in-hand rendering
     * @param defaultContexts   item display contexts to keep the default <code>itemModel</code> for
     */
    void register(ModelResourceLocation itemModel, ModelResourceLocation itemModelOverride, ItemDisplayContext... defaultContexts);
}
