package fuzs.puzzleslib.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * a builder for {@link CreativeModeTab}, most features are only available on Forge, on Fabric they'll simply do nothing
 */
public interface CreativeModeTabBuilder {

    /**
     * set an item stack to display as tab icon
     *
     * @param icon  the item stack displayed as tab icon
     * @return      builder instance
     */
    CreativeModeTabBuilder setIcon(Supplier<ItemStack> icon);

    /**
     * disable item stack icon set in {@link CreativeModeTabBuilder#setIcon} from being cached, allowing for a cycling icon if the provided supplier supports that
     * <p>only supported on Forge
     *
     * @return  builder instance
     */
    CreativeModeTabBuilder disableIconCache();

    /**
     * hide item group title, used for inventory tab in vanilla
     * <p>only supported on Forge
     *
     * @return  builder instance
     */
    CreativeModeTabBuilder hideTitle();

    /**
     * hide item group scrollbar on the right side, used for inventory tab in vanilla
     * <p>only supported on Forge
     *
     * @return  builder instance
     */
    CreativeModeTabBuilder hideScroll();

    /**
     * align tab to the right in the creative menu, used for saved hotbars tab in vanilla
     * <p>only supported on Forge
     *
     * @return  builder instance
     */
    CreativeModeTabBuilder alignRight();

    /**
     * fill this tab with custom items, fully overrides vanilla, useful for sorting purposes
     *
     * @return  builder instance
     *
     * @deprecated migrate to {@link #appendItemsV2(BiConsumer)}
     */
    @Deprecated(forRemoval = true)
    default CreativeModeTabBuilder appendItems(BiConsumer<List<ItemStack>, CreativeModeTab> itemStacks) {
        return this.appendItemsV2(itemStacks::accept);
    }

    /**
     * fill this tab with custom items, fully overrides vanilla, useful for sorting purposes
     *
     * @return  builder instance
     */
    CreativeModeTabBuilder appendItemsV2(BiConsumer<NonNullList<ItemStack>, CreativeModeTab> itemStacks);

    /**
     * Add all highest level enchantments in the form of enchanted books to this tab.
     * <p>Vanilla will still add the books to search and the dedicated enchantment category tab.
     *
     * @return builder instance
     */
    CreativeModeTabBuilder appendAllEnchantments();

    /**
     * Add all potions in the form of drinkable potions, splash potions, lingering potions and tipped arrows to this tab.
     * <p>Vanilla will still add the potions to search and the dedicated potions tab (combat tab for tipped arrows).
     *
     * @return builder instance
     */
    CreativeModeTabBuilder appendAllPotions();

    /**
     * show a search bar in this tab like vanilla's search tab
     * <p>only supported on Forge
     *
     * @return  builder instance
     */
    CreativeModeTabBuilder showSearch();

    /**
     * finishes this builder by building the creative mode tab
     *
     * @return  the built creative mode tab
     */
    CreativeModeTab build();
}
