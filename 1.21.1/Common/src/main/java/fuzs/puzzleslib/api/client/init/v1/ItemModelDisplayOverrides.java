package fuzs.puzzleslib.api.client.init.v1;

import fuzs.puzzleslib.impl.client.core.ClientFactories;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Allows for registering model overrides for items that have a different model in
 * {@link net.minecraft.client.renderer.entity.ItemRenderer} depending on {@link ItemDisplayContext}. Vanilla examples:
 * <ul>
 *     <li>{@link net.minecraft.world.item.Items#TRIDENT}</li>
 *     <li>{@link net.minecraft.world.item.Items#SPYGLASS}</li>
 * </ul>
 */
public interface ItemModelDisplayOverrides {
    ItemModelDisplayOverrides INSTANCE = ClientFactories.INSTANCE.getItemModelDisplayOverrides();

    /**
     * Register an item model override for an item.
     *
     * @param itemModel         the default item model location using the built-in item model template, usually used for
     *                          the item's appearance in guis and when dropped on the ground
     * @param itemModelOverride the custom model, usually used for in-hand rendering
     * @param defaultContexts   item display contexts to keep the default item model for
     */
    void register(ModelResourceLocation itemModel, ModelResourceLocation itemModelOverride, ItemDisplayContext... defaultContexts);

    /**
     * Register an item model override for an item.
     *
     * @param itemModel         the default item model location using the built-in item model template, usually used for
     *                          the item's appearance in guis and when dropped on the ground
     * @param itemModelOverride the custom model, usually used for in-hand rendering
     * @param defaultContexts   item display contexts to keep the default item model for
     */
    void register(ModelResourceLocation itemModel, ResourceLocation itemModelOverride, ItemDisplayContext... defaultContexts);
}
