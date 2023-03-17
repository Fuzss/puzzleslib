package fuzs.puzzleslib.api.container.v1;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * A simple {@code Inventory} implementation with only default methods + an item list getter.
 * <p>Originally by Juuz.
 */
@FunctionalInterface
public interface ContainerImpl extends Container {

    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    NonNullList<ItemStack> items();

    /**
     * Creates an inventory from the item list.
     */
    static ContainerImpl of(NonNullList<ItemStack> items) {
        return () -> items;
    }

    /**
     * Creates a new inventory with the specified size.
     */
    static ContainerImpl of(int size) {
        return of(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    /**
     * Returns the inventory size.
     */
    @Override
    default int getContainerSize() {
        return this.items().size();
    }

    /**
     * Checks if the inventory is empty.
     *
     * @return true if this inventory has only empty stacks, false otherwise.
     */
    @Override
    default boolean isEmpty() {
        for (ItemStack stack : this.items()) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves the item in the slot.
     */
    @Override
    default ItemStack getItem(int slot) {
        return slot >= 0 && slot < this.items().size() ? this.items().get(slot) : ItemStack.EMPTY;
    }

    /**
     * Removes items from an inventory slot.
     *
     * @param slot  The slot to remove from.
     * @param count How many items to remove. If there are less items in the slot than what are requested,
     *              takes all items in that slot.
     */
    @Override
    default ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(this.items(), slot, count);
        if (!result.isEmpty()) this.setChanged();
        return result;
    }

    /**
     * Removes all items from an inventory slot.
     *
     * @param slot The slot to remove from.
     */
    @Override
    default ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items(), slot);
    }

    /**
     * Replaces the current stack in an inventory slot with the provided stack.
     *
     * @param slot  The inventory slot of which to replace the itemstack.
     * @param stack The replacing itemstack. If the stack is too big for
     *              this inventory ({@link Container#getMaxStackSize()}),
     *              it gets resized to this inventory's maximum amount.
     */
    @Override
    default void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.items().size()) {
            if (this.items().set(slot, stack) != stack) {
                if (stack.getCount() > this.getMaxStackSize()) {
                    stack.setCount(this.getMaxStackSize());
                }
                this.setChanged();
            }
        }
    }

    /**
     * Clears the inventory.
     */
    @Override
    default void clearContent() {
        this.items().clear();
    }

    /**
     * Marks the state as dirty.
     * Must be called after changes in the inventory, so that the game can properly save
     * the inventory contents and notify neighboring blocks of inventory changes.
     */
    @Override
    default void setChanged() {
        // Override if you want behavior.
    }

    /**
     * @return true if the player can use the inventory, false otherwise.
     */
    @Override
    default boolean stillValid(Player player) {
        return true;
    }
}