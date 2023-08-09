package fuzs.puzzleslib.api.client.init.v1;

import fuzs.puzzleslib.impl.client.init.ItemModelOverridesImpl;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Allows for registering model overrides for items that have a different model in {@link net.minecraft.client.renderer.entity.ItemRenderer} depending on {@link ItemTransforms.TransformType}like {@link net.minecraft.world.item.Items#TRIDENT} and {@link net.minecraft.world.item.Items#SPYGLASS} in vanilla
 */
public interface ItemModelOverrides {
    /**
     * The singleton instance of the overrides registry.
     * Use this instance to call the methods in this interface.
     */
    ItemModelOverrides INSTANCE = new ItemModelOverridesImpl();

    /**
     * Register an item model override for an item.
     *
     * @param item the item to register overrides for
     * @param itemModel           the default item model location using the built-in item model template, usually used for the item's appearance in guis and when dropped on the ground
     * @param customModel         the custom model, usually used for in-hand rendering
     * @param itemModelTransforms   transform types to keep the default <code>itemModel</code> for
     */
    void register(Item item, ModelResourceLocation itemModel, ModelResourceLocation customModel, ItemTransforms.TransformType... itemModelTransforms);
}
