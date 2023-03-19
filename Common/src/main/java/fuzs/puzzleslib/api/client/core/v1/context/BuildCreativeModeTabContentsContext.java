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
     * @param modId                 the creative mode tab namespace to add items to, the default path 'main' will be used
     * @param displayItemsGenerator context for adding items to the creative mode tab
     */
    default void registerBuildListener(String modId, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
        this.registerBuildListener(new ResourceLocation(modId, "main"), displayItemsGenerator);
    }

    /**
     * Add items to a creative tab referenced by internal id.
     *
     * @param identifier            the creative mode tab to add items to
     * @param displayItemsGenerator context for adding items to the creative mode tab
     */
    void registerBuildListener(ResourceLocation identifier, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator);

    /**
     * Add items to a creative tab referenced by instance.
     *
     * @param creativeModeTab       the creative mode tab to add items to
     * @param displayItemsGenerator context for adding items to the creative mode tab
     */
    void registerBuildListener(CreativeModeTab creativeModeTab, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator);
}
