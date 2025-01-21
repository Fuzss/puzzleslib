package fuzs.puzzleslib.api.item.v2;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.impl.item.CreativeModeTabConfiguratorImpl;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

/**
 * A builder for {@link CreativeModeTab}.
 * <p>
 * Some features are only available on NeoForge, on Fabric they'll simply do nothing.
 */
@Deprecated(forRemoval = true)
public interface CreativeModeTabConfigurator {

    /**
     * Creates a new creative mode tab, handles adding to the creative screen.
     * <p>
     * The tab id defaults to {@code main}.
     *
     * @param modId the mod this tab is used by
     * @param icon  the item stack displayed as tab icon
     * @return the creative mode tab
     */
    static CreativeModeTabConfigurator from(String modId, Holder<? extends ItemLike> icon) {
        return from(modId).icon(icon);
    }

    /**
     * Creates a new creative mode tab, handles adding to the creative screen.
     * <p>
     * The tab id defaults to {@code main}.
     *
     * @param modId the mod this tab is used by
     * @return builder instance
     */
    static CreativeModeTabConfigurator from(String modId) {
        return from(modId, "main");
    }

    /**
     * Creates a new creative mode tab, handles adding to the creative screen.
     *
     * @param modId the mod this tab is used by
     * @param tabId the key for this tab, useful when the mod has multiple tabs
     * @return builder instance
     */
    static CreativeModeTabConfigurator from(String modId, String tabId) {
        return from(ResourceLocationHelper.fromNamespaceAndPath(modId, tabId));
    }

    /**
     * Creates a new creative mode tab, handles adding to the creative screen.
     *
     * @param resourceLocation the tab resource location
     * @return builder instance
     */
    static CreativeModeTabConfigurator from(ResourceLocation resourceLocation) {
        return new CreativeModeTabConfiguratorImpl(resourceLocation);
    }

    /**
     * Set an item stack to display as tab icon.
     * <p>
     * On NeoForge this property is overridden when setting {@link #icons(Supplier)}.
     *
     * @param icon the item stack displayed as tab icon
     * @return builder instance
     */
    default CreativeModeTabConfigurator icon(Holder<? extends ItemLike> icon) {
        return this.icon(() -> new ItemStack(icon.value()));
    }

    /**
     * Set an item stack to display as tab icon.
     * <p>
     * On NeoForge this property is overridden when setting {@link #icons(Supplier)}.
     *
     * @param icon the item stack displayed as tab icon
     * @return builder instance
     */
    CreativeModeTabConfigurator icon(Supplier<ItemStack> icon);

    /**
     * Set multiple item stacks to display as tab icon that cycle every two seconds.
     * <p>
     * Only supported on NeoForge.
     * <p>
     * On Fabric the item stack at index zero in the supplied array is used. Additionally, set an icon in
     * {@link #icon(Supplier)} to override this behavior.
     *
     * @param icons multiple item stacks displayed as tab icon, they are cycled through
     * @return builder instance
     */
    CreativeModeTabConfigurator icons(Supplier<ItemStack[]> icons);

    /**
     * fill this tab with custom items, fully overrides vanilla, useful for sorting purposes
     *
     * @return builder instance
     */
    CreativeModeTabConfigurator displayItems(CreativeModeTab.DisplayItemsGenerator generator);

    /**
     * Show a search bar in this tab like vanilla's search tab.
     * <p>
     * Only supported on NeoForge!
     *
     * @return builder instance
     */
    CreativeModeTabConfigurator withSearchBar();

    /**
     * Add all highest level enchantments in the form of enchanted books to this tab.
     * <p>
     * Also add all potions in the form of drinkable potions, splash potions, lingering potions and tipped arrows to
     * this tab.
     * <p>
     * Vanilla will still add the books to search and the dedicated enchantment category tab, also potions to search and
     * the dedicated potions tab (combat tab for tipped arrows).
     *
     * @return builder instance
     */
    CreativeModeTabConfigurator appendEnchantmentsAndPotions();
}
