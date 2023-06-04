package fuzs.puzzleslib.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.impl.client.core.event.CreativeModeTabContentsEvent;
import fuzs.puzzleslib.impl.item.CreativeModeTabConfiguratorImpl;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record CreativeModeTabContextForgeImpl() implements CreativeModeTabContext {

    @Override
    public void registerCreativeModeTab(CreativeModeTabConfigurator configurator) {
        CreativeModeTabConfiguratorImpl impl = (CreativeModeTabConfiguratorImpl) configurator;
        CreativeModeTab tab = new CreativeModeTab(String.format("%s.%s", impl.getIdentifier().getNamespace(), impl.getIdentifier().getPath())) {
            @Nullable
            private ItemStack[] icons;

            @Override
            public ItemStack getIconItem() {
                if (impl.getIcons() != null) {
                    // stolen from XFactHD, thanks :)
                    if (this.icons == null) {
                        this.icons = impl.getIcons().get();
                        Preconditions.checkPositionIndex(1, this.icons.length, "icons is empty");
                    }
                    int index = (int) (System.currentTimeMillis() / 2000) % this.icons.length;
                    return this.icons[index];
                } else {
                    return super.getIconItem();
                }
            }

            @Override
            public ItemStack makeIcon() {
                return impl.getIcon().get();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> itemStacks) {
                // this is handled by the event below to properly support the way the search tab is implemented (it fires for every possible tab instead of the search tab itself)
                // since there is no way to differentiate between the search tab and this tab being populated fillItemList on the tab itself is skipped
            }

            @Override
            public boolean hasSearchBar() {
                return impl.isHasSearchBar() || super.hasSearchBar();
            }
        };
        if (impl.isHasSearchBar()) {
            tab.setBackgroundImage(new ResourceLocation("textures/gui/container/creative_inventory/tab_item_search.png"));
        }
        ModContainerHelper.findModEventBus(impl.getIdentifier().getNamespace()).ifPresent(modEventBus -> {
            modEventBus.addListener((final CreativeModeTabContentsEvent evt) -> {
                if (evt.getTab() == tab) impl.getDisplayItemsGenerator().accept(evt.getOutput());
            });
        });
    }
}
