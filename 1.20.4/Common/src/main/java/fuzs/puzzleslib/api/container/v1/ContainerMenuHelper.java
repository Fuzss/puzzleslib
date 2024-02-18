package fuzs.puzzleslib.api.container.v1;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Small helper class related to working with implementations of {@link AbstractContainerMenu}.
 */
public final class ContainerMenuHelper {

    private ContainerMenuHelper() {

    }

    /**
     * Adds the player inventory slots to an {@link AbstractContainerMenu}.
     *
     * @param abstractContainerMenu menu to add slots to
     * @param inventory             player inventory instance
     * @param offsetY               vertical offset
     */
    public static void addInventorySlots(AbstractContainerMenu abstractContainerMenu, Inventory inventory, int offsetY) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                abstractContainerMenu.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, i * 18 + offsetY));
            }
        }
        for (int i = 0; i < 9; ++i) {
            abstractContainerMenu.addSlot(new Slot(inventory, i, 8 + i * 18, 3 * 18 + offsetY + 4));
        }
    }

    /**
     * Allows for creating a container backed by a list of items.
     * <p>
     * Useful for items that are stored on e.g. a block entity, but not accessible via a container, but should be
     * accessible via a menu implementation.
     * <p>
     * An example would be a slot for setting a filter item that is kept separate from the main container inventory.
     *
     * @param items    list of items
     * @param listener listener for changes to the item list
     * @return the created container
     */
    public static SimpleContainer createListBackedContainer(NonNullList<ItemStack> items, @Nullable Container listener) {
        return createListBackedContainer(items, listener != null ? $ -> listener.setChanged() : null);
    }

    /**
     * Allows for creating a container backed by a list of items.
     * <p>
     * Useful for items that are stored on e.g. a block entity, but not accessible via a container, but should be
     * accessible via a menu implementation.
     * <p>
     * An example would be a slot for setting a filter item that is kept separate from the main container inventory.
     *
     * @param items    list of items
     * @param listener listener for changes to the item list
     * @return the created container
     */
    public static SimpleContainer createListBackedContainer(NonNullList<ItemStack> items, @Nullable ContainerListener listener) {
        SimpleContainer simpleContainer = new SimpleContainer();
        simpleContainer.size = items.size();
        simpleContainer.items = items;
        if (listener != null) {
            simpleContainer.addListener(listener);
        }
        return simpleContainer;
    }
}
