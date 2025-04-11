package fuzs.puzzleslib.api.container.v1;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * A simple {@link Container} implementation with only default methods and an item list getter.
 */
@FunctionalInterface
public interface ListBackedContainer extends SimpleContainerImpl {

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
     */
    NonNullList<ItemStack> getContainerItems();

    @Override
    default int getContainerSize() {
        return this.getContainerItems().size();
    }

    @Override
    default void setChanged() {
        // NO-OP
    }

    @Override
    default boolean stillValid(Player player) {
        return true;
    }

    @Override
    default ItemStack getContainerItem(int slot) {
        return this.getContainerItems().get(slot);
    }

    @Override
    default ItemStack removeContainerItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.getContainerItems(), slot, amount);
    }

    @Override
    default ItemStack removeContainerItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.getContainerItems(), slot);
    }

    @Override
    default void setContainerItem(int slot, ItemStack itemStack) {
        this.getContainerItems().set(slot, itemStack);
    }
}
