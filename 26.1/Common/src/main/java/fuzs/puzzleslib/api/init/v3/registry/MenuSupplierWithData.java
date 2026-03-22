package fuzs.puzzleslib.api.init.v3.registry;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * An extended implementation of {@link net.minecraft.world.inventory.MenuType.MenuSupplier}, that allows for sending
 * additional data from the server.
 *
 * @param <T> the type of menu
 */
@FunctionalInterface
public interface MenuSupplierWithData<T extends AbstractContainerMenu, S> {

    /**
     * Create the container menu.
     *
     * @param containerId the menu id
     * @param inventory   the player inventory
     * @param data        the additional data to send to the client
     * @return the container menu
     */
    T create(int containerId, Inventory inventory, S data);
}
