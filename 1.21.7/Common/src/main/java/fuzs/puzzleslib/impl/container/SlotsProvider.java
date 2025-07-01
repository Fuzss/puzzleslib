package fuzs.puzzleslib.impl.container;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface SlotsProvider {

    static SlotsProvider of(Container container) {
        return new SlotsProvider() {
            @Override
            public int getContainerSize() {
                return container.getContainerSize();
            }

            @Override
            public ItemStack getItem(int slot) {
                return container.getItem(slot);
            }

            @Override
            public void setItem(int slot, ItemStack itemStack) {
                container.setItem(slot, itemStack);
            }

            @Override
            public void clearContent() {
                container.clearContent();
            }
        };
    }

    static SlotsProvider of(NonNullList<ItemStack> items) {
        return new SlotsProvider() {
            @Override
            public int getContainerSize() {
                return items.size();
            }

            @Override
            public ItemStack getItem(int slot) {
                return items.get(slot);
            }

            @Override
            public void setItem(int slot, ItemStack itemStack) {
                items.set(slot, itemStack);
            }

            @Override
            public void clearContent() {
                items.clear();
            }
        };
    }

    int getContainerSize();

    ItemStack getItem(int slot);

    void setItem(int slot, ItemStack itemStack);

    void clearContent();
}
