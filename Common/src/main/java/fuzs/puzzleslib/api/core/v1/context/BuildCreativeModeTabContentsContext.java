package fuzs.puzzleslib.api.core.v1.context;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

/**
 * Add items to a creative tab.
 */
@Deprecated(forRemoval = true)
@FunctionalInterface
public interface BuildCreativeModeTabContentsContext {

    /**
     * Add items to a creative tab referenced by internal id.
     *
     * @param modId        the creative mode tab namespace to add items to, the default path 'main' will be used
     * @param displayItems context for adding items to the creative mode tab
     */
    default void registerBuildListener(String modId, CreativeModeTab.DisplayItemsGenerator displayItems) {
        ResourceLocation resourceLocation = ResourceLocationHelper.fromNamespaceAndPath(modId, "main");
        this.registerBuildListener(resourceLocation, displayItems);
    }

    /**
     * Add items to a creative tab referenced by internal id.
     *
     * @param resourceLocation the creative mode tab to add items to
     * @param displayItems     context for adding items to the creative mode tab
     */
    default void registerBuildListener(ResourceLocation resourceLocation, CreativeModeTab.DisplayItemsGenerator displayItems) {
        ResourceKey<CreativeModeTab> resourceKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, resourceLocation);
        this.registerBuildListener(resourceKey, displayItems);
    }

    /**
     * Add items to a creative tab referenced by instance.
     *
     * @param resourceKey  the creative mode tab to add items to
     * @param displayItems context for adding items to the creative mode tab
     */
    void registerBuildListener(ResourceKey<CreativeModeTab> resourceKey, CreativeModeTab.DisplayItemsGenerator displayItems);
}
