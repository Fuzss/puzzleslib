package fuzs.puzzleslib.api.container.v1;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * A simple {@link Container} implementation taking care of slot range checks and container updates.
 */
public interface SimpleContainerImpl extends Container {

    @Override
    default boolean isEmpty() {
        for (int i = 0; i < this.getContainerSize(); i++) {
            if (!this.getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    default ItemStack getItem(int slot) {
        return slot >= 0 && slot < this.getContainerSize() ? this.getContainerItem(slot) : ItemStack.EMPTY;
    }

    /**
     * Raw implementation for returning an item from the container without additional checks.
     *
     * @param slot the slot index
     * @return the item stack
     */
    ItemStack getContainerItem(int slot);

    @Override
    default ItemStack removeItem(int slot, int amount) {
        if (slot >= 0 && slot < this.getContainerSize() && !this.getContainerItem(slot).isEmpty() && amount > 0) {
            ItemStack itemStack = this.removeContainerItem(slot, amount);
            if (!itemStack.isEmpty()) this.setChanged();
            return itemStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Raw implementation for removing a specific item amount from the container without additional checks.
     *
     * @param slot   the slot index
     * @param amount the item amount to remove
     * @return the removed item stack
     */
    ItemStack removeContainerItem(int slot, int amount);

    @Override
    default ItemStack removeItemNoUpdate(int slot) {
        return slot >= 0 && slot < this.getContainerSize() ? this.removeContainerItemNoUpdate(slot) : ItemStack.EMPTY;
    }

    /**
     * Raw implementation for removing an item from the container without additional checks.
     *
     * @param slot the slot index
     * @return the removed item stack
     */
    ItemStack removeContainerItemNoUpdate(int slot);

    @Override
    default void setItem(int slot, ItemStack itemStack) {
        if (slot >= 0 && slot < this.getContainerSize()) {
            this.setContainerItem(slot, itemStack);
            if (!itemStack.isEmpty() && itemStack.getCount() > this.getMaxStackSize()) {
                itemStack.setCount(this.getMaxStackSize());
            }
            this.setChanged();
        }
    }

    /**
     * Raw implementation for placing an item to the container without additional checks.
     *
     * @param slot      the slot index
     * @param itemStack the item stack
     */
    void setContainerItem(int slot, ItemStack itemStack);

    @Override
    default void clearContent() {
        for (int i = 0; i < this.getContainerSize(); i++) {
            this.setContainerItem(i, ItemStack.EMPTY);
        }
        this.setChanged();
    }
}
