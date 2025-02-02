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
     * Register vanilla item model overrides for an item.
     * <p>
     * All display contexts except {@link ItemDisplayContext#GUI GUI}, {@link ItemDisplayContext#GROUND GROUND}, and
     * {@link ItemDisplayContext#FIXED FIXED} will use the override model.
     *
     * @param itemModel         the vanilla item model location
     * @param itemModelOverride the custom item model location
     */
    void register(ModelResourceLocation itemModel, ModelResourceLocation itemModelOverride);

    /**
     * Register item model overrides for an item.
     *
     * @param itemModel           the vanilla item model location
     * @param itemModelOverride   the custom item model location
     * @param itemDisplayContexts the item display contexts to use the override model for
     */
    void register(ModelResourceLocation itemModel, ModelResourceLocation itemModelOverride, ItemDisplayContext... itemDisplayContexts);

    /**
     * Register vanilla item model overrides for an item.
     * <p>
     * All display contexts except {@link ItemDisplayContext#GUI GUI}, {@link ItemDisplayContext#GROUND GROUND}, and
     * {@link ItemDisplayContext#FIXED FIXED} will use the override model.
     *
     * @param itemModel         the vanilla item model location
     * @param itemModelOverride the custom item model location
     */
    void register(ModelResourceLocation itemModel, ResourceLocation itemModelOverride);

    /**
     * Register item model overrides for an item.
     *
     * @param itemModel           the vanilla item model location
     * @param itemModelOverride   the custom item model location
     * @param itemDisplayContexts the item display contexts to use the override model for
     */
    void register(ModelResourceLocation itemModel, ResourceLocation itemModelOverride, ItemDisplayContext... itemDisplayContexts);
}
