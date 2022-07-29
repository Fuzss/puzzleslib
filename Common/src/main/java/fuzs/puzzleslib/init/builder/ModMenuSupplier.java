package fuzs.puzzleslib.init.builder;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * a copy of MenuSupplier in {@link net.minecraft.world.inventory.MenuType} which is private,
 * but both Forge and Fabric use an access transformer to set the interface to public,
 * we basically wrap the interface to make it usable on common
 * @param <T> type of menu
 */
@FunctionalInterface
public interface ModMenuSupplier<T extends AbstractContainerMenu> {

    /**
     * container menu factory method
     * @param containerId menu id
     * @param inventory player inventory
     * @return the container menu
     */
    T create(int containerId, Inventory inventory);
}
