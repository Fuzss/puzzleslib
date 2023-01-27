package fuzs.puzzleslib.impl.creativetab;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
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
                return !ForgeCreativeModeTabBuilder.this.cacheIcon ? this.makeIcon() : super.getIconItem();
            }

            @Override
            public ItemStack makeIcon() {
                return ForgeCreativeModeTabBuilder.this.getIcon();
            }

            @Override
            public boolean isAlignedRight() {
                return ForgeCreativeModeTabBuilder.this.alignRight || super.isAlignedRight();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> itemStacks) {
                if (ForgeCreativeModeTabBuilder.this.stacksForDisplay != null) {
                    ForgeCreativeModeTabBuilder.this.stacksForDisplay.accept(itemStacks, this);
                } else {
                    super.fillItemList(itemStacks);
                }
                ForgeCreativeModeTabBuilder.this.appendAdditionals(itemStacks);
            }

            @Override
            public boolean hasSearchBar() {
                return ForgeCreativeModeTabBuilder.this.showSearch || super.hasSearchBar();
            }
        };
        if (!this.showTitle) {
            tab.hideTitle();
        }
        if (!this.showScrollbar) {
            tab.hideScroll();
        }
        if (this.showSearch) {
            tab.setBackgroundImage(new ResourceLocation("textures/gui/container/creative_inventory/tab_item_search.png"));
        }
        return tab;
    }
}
