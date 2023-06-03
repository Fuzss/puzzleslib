package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.item.v2.DisplayItemsOutput;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;

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
    default void registerBuildListener(String modId, Consumer<DisplayItemsOutput> itemsGenerator) {
        this.registerBuildListener(new ResourceLocation(modId, "main"), itemsGenerator);
    }

    /**
     * Add items to a creative tab referenced by internal id.
     *
     * @param identifier     the creative mode tab to add items to
     * @param itemsGenerator context for adding items to the creative mode tab
     */
    void registerBuildListener(ResourceLocation identifier, Consumer<DisplayItemsOutput> itemsGenerator);

    /**
     * Add items to a creative tab referenced by instance.
     *
     * @param tab            the creative mode tab to add items to
     * @param itemsGenerator context for adding items to the creative mode tab
     */
    default void registerBuildListener(CreativeModeTab tab, Consumer<DisplayItemsOutput> itemsGenerator) {
        ResourceLocation identifier = tryCreateIdentifier(tab);
        if (identifier != null) {
            this.registerBuildListener(identifier, itemsGenerator);
        }
    }

    @Nullable
    static ResourceLocation tryCreateIdentifier(CreativeModeTab tab) {
        return tryParse(tab.getRecipeFolderName().toLowerCase(Locale.ROOT), '.');
    }

    @Nullable
    static ResourceLocation tryParse(String location, char separator) {
        try {
            return ResourceLocation.of(location, separator);
        } catch (ResourceLocationException var2) {
            return null;
        }
    }
}
