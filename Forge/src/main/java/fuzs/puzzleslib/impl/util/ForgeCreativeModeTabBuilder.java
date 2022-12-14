package fuzs.puzzleslib.impl.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ForgeCreativeModeTabBuilder extends CreativeModeTabBuilderImpl {

    public ForgeCreativeModeTabBuilder(String modId, String tabId) {
        super(modId, tabId);
    }

    @Override
    public CreativeModeTab build() {
        CreativeModeTab tab = new CreativeModeTab(String.format("%s.%s", this.identifier.getNamespace(), this.identifier.getPath())) {

            @Override
            public ItemStack getIconItem() {
                if (ForgeCreativeModeTabBuilder.this.cacheIcon) {
                    return super.getIconItem();
                }
                return this.makeIcon();
            }

            @Override
            public ItemStack makeIcon() {
                return this.getIconItem();
            }

            @Override
            public boolean isAlignedRight() {
                if (ForgeCreativeModeTabBuilder.this.alignRight) return true;
                return super.isAlignedRight();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> items) {
                if (ForgeCreativeModeTabBuilder.this.stacksForDisplay == null) {
                    super.fillItemList(items);
                } else {
                    ForgeCreativeModeTabBuilder.this.stacksForDisplay.accept(items, this);
                }
            }

            @Override
            public boolean hasSearchBar() {
                if (ForgeCreativeModeTabBuilder.this.showSearch) return true;
                return super.hasSearchBar();
            }

            @Override
            public int getSearchbarWidth() {
                if (ForgeCreativeModeTabBuilder.this.searchWidth != -1) return ForgeCreativeModeTabBuilder.this.searchWidth;
                return super.getSearchbarWidth();
            }
        };
        if (!this.showTitle) tab.hideTitle();
        if (!this.showScrollbar) tab.hideScroll();
        return tab;
    }
}
