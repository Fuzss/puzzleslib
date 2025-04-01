package fuzs.puzzleslib.api.container.v1;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.NonInteractiveResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Small helper class related to working with implementations of {@link AbstractContainerMenu}.
 */
public final class ContainerMenuHelper {

    private ContainerMenuHelper() {
        // NO-OP
    }

    /**
     * Opens a menu on both client and server while also providing additional data.
     *
     * @param serverPlayer the player opening the menu
     * @param menuProvider the menu factory
     * @param dataWriter   the additional data to be sent to the client
     */
    public static void openMenu(ServerPlayer serverPlayer, MenuProvider menuProvider, BiConsumer<ServerPlayer, RegistryFriendlyByteBuf> dataWriter) {
        Objects.requireNonNull(serverPlayer, "server player is null");
        Objects.requireNonNull(menuProvider, "menu provider is null");
        Objects.requireNonNull(dataWriter, "data writer is null");
        ProxyImpl.get().openMenu(serverPlayer, menuProvider, dataWriter);
    }

    /**
     * Replace the container slot representing the currently selected hotbar slot with a non-interactive variant.
     * <p>
     * Useful for handheld container items such as bags while their menu is open.
     *
     * @param containerMenu the container menu
     */
    public static void setSelectedSlotLocked(AbstractContainerMenu containerMenu) {
        for (int i = 0; i < containerMenu.slots.size(); i++) {
            Slot slot = containerMenu.slots.get(i);
            if (slot.container instanceof Inventory inventory &&
                    inventory.getSelectedSlot() == slot.getContainerSlot()) {
                NonInteractiveResultSlot newSlot = new NonInteractiveResultSlot(slot.container,
                        slot.getContainerSlot(),
                        slot.x,
                        slot.y) {
                    @Override
                    public boolean isFake() {
                        return false;
                    }
                };
                newSlot.index = slot.index;
                containerMenu.slots.set(i, newSlot);
                break;
            }
        }
    }

    /**
     * Adds the player inventory slots to an {@link AbstractContainerMenu}.
     *
     * @param containerMenu menu to add slots to
     * @param inventory     player inventory instance
     * @param offsetY       vertical offset
     */
    public static void addInventorySlots(AbstractContainerMenu containerMenu, Inventory inventory, int offsetY) {
        addInventorySlots(containerMenu, inventory, 8, offsetY);
    }

    /**
     * Adds the player inventory slots to an {@link AbstractContainerMenu}.
     *
     * @param containerMenu menu to add slots to
     * @param inventory     player inventory instance
     * @param offsetX       horizontal offset
     * @param offsetY       vertical offset
     */
    public static void addInventorySlots(AbstractContainerMenu containerMenu, Inventory inventory, int offsetX, int offsetY) {
        final int slotSize = 18;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                containerMenu.addSlot(new Slot(inventory, j + i * 9 + 9, offsetX + j * slotSize, offsetY));
            }
            offsetY += slotSize;
        }
        offsetY += 4;
        for (int i = 0; i < 9; ++i) {
            containerMenu.addSlot(new Slot(inventory, i, offsetX + i * slotSize, offsetY));
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

    /**
     * Writes contents from a list of items to a container.
     * <p>
     * Intended to be used with
     * {@link net.minecraft.world.level.block.entity.BaseContainerBlockEntity#setItems(NonNullList)} to be able to keep
     * the internal items list final.
     *
     * @param from the source item list
     * @param to   the target container
     */
    public static void copyItemsIntoContainer(NonNullList<ItemStack> from, Container to) {
        for (int i = 0; i < from.size(); i++) {
            if (i < to.getContainerSize()) {
                to.setItem(i, from.get(i));
            }
        }
    }

    /**
     * Writes contents from a list of items to another list of items.
     * <p>
     * Intended to be used with
     * {@link net.minecraft.world.level.block.entity.BaseContainerBlockEntity#setItems(NonNullList)} to be able to keep
     * the internal items list final.
     *
     * @param from the source item list
     * @param to   the target item list
     */
    public static void copyItemsIntoList(NonNullList<ItemStack> from, NonNullList<ItemStack> to) {
        for (int i = 0; i < from.size(); i++) {
            if (i < to.size()) {
                to.set(i, from.get(i));
            }
        }
    }
}
