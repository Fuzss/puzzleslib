package fuzs.puzzleslib.api.core.v1.context;

import fuzs.puzzleslib.api.item.v2.DisplayItemsOutput;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Add items to a creative tab.
 */
public interface BuildCreativeModeTabContentsContext {

    @Nullable
    static ResourceLocation tryCreateIdentifier(CreativeModeTab tab) {
        String identifier = ((TranslatableComponent) tab.getDisplayName()).getKey().substring("itemGroup.".length()).replace(".", ":");
        return ResourceLocation.tryParse(identifier.toLowerCase(Locale.ROOT));
    }

    @Nullable
    static CreativeModeTab findFromIdentifier(ResourceLocation identifier) {
        for (CreativeModeTab tab : CreativeModeTab.TABS) {
            if (identifier.equals(tryCreateIdentifier(tab))) {
                return tab;
            }
        }
        return null;
    }

    /**
     * Add items to a creative tab referenced by internal id.
     *
     * @param modId          the creative mode tab namespace to add items to, the default path 'main' will be used
     * @param itemsGenerator context for adding items to the creative mode tab
     */
    default void registerBuildListener(String modId, Consumer<DisplayItemsOutput> itemsGenerator) {
        ResourceLocation identifier = new ResourceLocation(modId, "main");
        this.registerBuildListener(identifier, itemsGenerator);
    }

    /**
     * Add items to a creative tab referenced by internal id.
     *
     * @param identifier     the creative mode tab to add items to
     * @param itemsGenerator context for adding items to the creative mode tab
     */
    default void registerBuildListener(ResourceLocation identifier, Consumer<DisplayItemsOutput> itemsGenerator) {
        CreativeModeTab tab = findFromIdentifier(identifier);
        if (tab != null) this.registerBuildListener(tab, itemsGenerator);
    }

    /**
     * Add items to a creative tab referenced by instance.
     *
     * @param tab            the creative mode tab to add items to
     * @param itemsGenerator context for adding items to the creative mode tab
     */
    default void registerBuildListener(CreativeModeTab tab, Consumer<DisplayItemsOutput> itemsGenerator) {
        Objects.requireNonNull(tab, "creative mode tab is null");
        Objects.requireNonNull(itemsGenerator, "display items generator is null");
        itemsGenerator.accept(DisplayItemsOutput.setItemCategory(tab));
    }
}
