package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(modId, "main");
        this.registerBuildListener(resourceLocation, itemsGenerator);
    }

    /**
     * Add items to a creative tab referenced by internal id.
     *
     * @param identifier     the creative mode tab to add items to
     * @param itemsGenerator context for adding items to the creative mode tab
     */
    default void registerBuildListener(ResourceLocation identifier, CreativeModeTab.DisplayItemsGenerator itemsGenerator) {
        ResourceKey<CreativeModeTab> resourceKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, identifier);
        this.registerBuildListener(resourceKey, itemsGenerator);
    }

    /**
     * Add items to a creative tab referenced by instance.
     *
     * @param resourceKey    the creative mode tab to add items to
     * @param itemsGenerator context for adding items to the creative mode tab
     */
    void registerBuildListener(ResourceKey<CreativeModeTab> resourceKey, CreativeModeTab.DisplayItemsGenerator itemsGenerator);
}
