package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

/**
 * Add items to a creative tab.
 */
public interface BuildCreativeModeTabContentsContext {

    /**
     * Add items to a creative tab referenced by internal id.
     *
     * @param modId          the creative mode tab namespace to add items to, the default path 'main' will be used
     * @param itemsGenerator context for adding items to the creative mode tab
     */
    default void registerBuildListener(String modId, CreativeModeTab.DisplayItemsGenerator itemsGenerator) {
        this.registerBuildListener(new ResourceLocation(modId, "main"), itemsGenerator);
    }

    /**
     * Add items to a creative tab referenced by internal id.
     *
     * @param identifier     the creative mode tab to add items to
     * @param itemsGenerator context for adding items to the creative mode tab
     */
    void registerBuildListener(ResourceLocation identifier, CreativeModeTab.DisplayItemsGenerator itemsGenerator);

    /**
     * Add items to a creative tab referenced by instance.
     *
     * @param tab            the creative mode tab to add items to
     * @param itemsGenerator context for adding items to the creative mode tab
     */
    void registerBuildListener(CreativeModeTab tab, CreativeModeTab.DisplayItemsGenerator itemsGenerator);
}
