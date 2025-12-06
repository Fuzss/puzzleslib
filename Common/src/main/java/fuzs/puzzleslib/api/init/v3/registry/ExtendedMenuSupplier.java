package fuzs.puzzleslib.api.init.v3.registry;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * A mod loader independent implementation of {@link net.minecraft.world.inventory.MenuType.MenuSupplier}, allowing for
 * additional data being sent from the server in form of a {@link RegistryFriendlyByteBuf}.
 *
 * @param <T> type of menu
 */
@Deprecated
@FunctionalInterface
public interface ExtendedMenuSupplier<T extends AbstractContainerMenu> {

    /**
     * Creates the supplied container menu.
     *
     * @param containerId             menu id
     * @param inventory               player inventory
     * @param registryFriendlyByteBuf additional data to be sent to client
     * @return the container menu
     */
    T create(int containerId, Inventory inventory, RegistryFriendlyByteBuf registryFriendlyByteBuf);
}
