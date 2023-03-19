package fuzs.puzzleslib.api.item.v2;

import fuzs.puzzleslib.impl.item.CreativeModeTabConfiguratorImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * a builder for {@link CreativeModeTab}, most features are only available on Forge, on Fabric they'll simply do nothing
 */
public interface CreativeModeTabConfigurator {


    /**
     * creates a new creative mode tab, handles adding to the creative screen
     * use this when one tab is enough for the mod, <code>tabId</code> defaults to "main"
     *
     * @param modId             the mod this tab is used by
     * @param stackSupplier     the display stack
     * @return                  the creative mode tab
     */
    default CreativeModeTabConfigurator simple(String modId, Supplier<ItemStack> stackSupplier) {
        return of(modId).icon(stackSupplier);
    }

    /**
     * creates a builder for a new creative mode tab, the implementation handles adding to the creative screen
     * <p>use this when one tab is enough for the mod, <code>tabId</code> defaults to "main"
     *
     * @param modId the mod this tab is used by
     * @return builder instance
     */
    static CreativeModeTabConfigurator of(String modId) {
        return of(modId, "main");
    }

    /**
     * creates a builder for a new creative mode tab, the implementation handles adding to the creative screen
     *
     * @param modId the mod this tab is used by
     * @param tabId the key for this tab, useful when the mod has multiple
     * @return builder instance
     */
    static CreativeModeTabConfigurator of(String modId, String tabId) {
        return of(new ResourceLocation(modId, tabId));
    }

    /**
     * creates a builder for a new creative mode tab, the implementation handles adding to the creative screen
     *
     * @param identifier the tab identifier
     * @return builder instance
     */
    static CreativeModeTabConfigurator of(ResourceLocation identifier) {
        return new CreativeModeTabConfiguratorImpl(identifier);
    }

    /**
     * set an item stack to display as tab icon
     *
     * @param icon  the item stack displayed as tab icon
     * @return      builder instance
     */
    CreativeModeTabConfigurator icon(Supplier<ItemStack> icon);

    /**
     * Makes icons cycle every two seconds.
     * <p>Only supported on Forge!
     *
     * @param icons     multiple item stacks displayed as tab icon, they are cycled through
     * @return  builder instance
     */
    CreativeModeTabConfigurator icons(Supplier<ItemStack[]> icons);

    /**
     * fill this tab with custom items, fully overrides vanilla, useful for sorting purposes
     *
     * @return  builder instance
     */
    CreativeModeTabConfigurator displayItems(CreativeModeTab.DisplayItemsGenerator displayItemsGenerator);

    /**
     * show a search bar in this tab like vanilla's search tab
     * <p>Only supported on Forge!
     *
     * @return  builder instance
     */
    CreativeModeTabConfigurator withSearchBar();

    /**
     * Add all highest level enchantments in the form of enchanted books to this tab.
     * <p>Also add all potions in the form of drinkable potions, splash potions, lingering potions and tipped arrows to this tab.
     * <p>Vanilla will still add the books to search and the dedicated enchantment category tab, also potions to search and the dedicated potions tab (combat tab for tipped arrows).
     *
     * @return builder instance
     */
    CreativeModeTabConfigurator appendEnchantmentsAndPotions();
}
