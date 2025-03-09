package fuzs.puzzleslib.api.container.v1;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;

/**
 * A simple {@link Container} implementation with only default methods and an item list getter.
 */
@FunctionalInterface
public interface ListBackedContainer extends Container {

    /**
     * Creates a container backed by the item list.
     */
    static ListBackedContainer of(NonNullList<ItemStack> items) {
        return () -> items;
    }

    /**
     * Creates a new empty container with the specified size.
     */
    static ListBackedContainer of(int size) {
        return of(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    /**
     * Retrieves the item list backing this inventory.
     * <p>
     * Must not be called <code>getItems</code> as it clashes with {@link BaseContainerBlockEntity#getItems()} causing
     * remapping issues.
     */
    NonNullList<ItemStack> getContainerItems();

    @Override
    default int getContainerSize() {
        return this.getContainerItems().size();
    }

    @Override
    default boolean isEmpty() {
        for (ItemStack stack : this.getContainerItems()) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    default ItemStack getItem(int slot) {
        return slot >= 0 && slot < this.getContainerSize() ? this.getContainerItems().get(slot) : ItemStack.EMPTY;
    }

    @Override
    default ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(this.getContainerItems(), slot, count);
        if (!result.isEmpty()) this.setChanged();
        return result;
    }

    @Override
    default ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.getContainerItems(), slot);
    }

    @Override
    default void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.getContainerSize()) {
            this.getContainerItems().set(slot, stack);
            if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
                stack.setCount(this.getMaxStackSize());
            }
            this.setChanged();
        }
    }

    @Override
    default void clearContent() {
        this.getContainerItems().clear();
        this.setChanged();
    }

    @Override
    default void setChanged() {
        // NO-OP
    }

    @Override
    default boolean stillValid(Player player) {
        return true;
    }
}