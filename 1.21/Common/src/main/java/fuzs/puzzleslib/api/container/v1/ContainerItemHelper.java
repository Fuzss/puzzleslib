package fuzs.puzzleslib.api.container.v1;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * Small helper class for working with implementations of {@link Container}.
 *
 * @deprecated moved to {@link ContainerMenuHelper}
 */
@Deprecated(forRemoval = true)
public final class ContainerItemHelper {

    private ContainerItemHelper() {
        // NO-OP
    }

    /**
     * Writes contents from a list of items to a container.
     * <p>
     * Intended to be used with
     * {@link net.minecraft.world.level.block.entity.BaseContainerBlockEntity#setItems(NonNullList)} to be able to keep
     * the internal items list final.
     *
     * @param container the target container
     * @param items     the source item list
     */
    public static void copyItemsToContainer(Container container, NonNullList<ItemStack> items) {
        ContainerMenuHelper.copyItemsIntoContainer(items, container);
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
    public static void copyItemList(NonNullList<ItemStack> from, NonNullList<ItemStack> to) {
        ContainerMenuHelper.copyItemsIntoList(from, to);
    }
}
