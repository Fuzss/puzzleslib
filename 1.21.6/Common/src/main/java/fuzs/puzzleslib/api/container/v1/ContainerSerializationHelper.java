package fuzs.puzzleslib.api.container.v1;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * An adjusted version of {@link net.minecraft.world.ContainerHelper} that allows for using {@link Container} instead of
 * {@link net.minecraft.core.NonNullList}.
 */
public final class ContainerSerializationHelper {

    private ContainerSerializationHelper() {
        // NO-OP
    }

    /**
     * Write item stacks to an output.
     *
     * @param valueOutput the value output
     * @param container   the container
     * @see net.minecraft.world.ContainerHelper#saveAllItems(ValueOutput, NonNullList)
     */
    public static void saveAllItems(ValueOutput valueOutput, Container container) {
        storeAsSlots(container, valueOutput.list("Items", ItemStackWithSlot.CODEC));
    }

    /**
     * Write item stacks to an output.
     *
     * @param container       the container
     * @param typedOutputList the output list
     * @see net.minecraft.world.inventory.PlayerEnderChestContainer#storeAsSlots(ValueOutput.TypedOutputList)
     */
    public static void storeAsSlots(Container container, ValueOutput.TypedOutputList<ItemStackWithSlot> typedOutputList) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (!itemStack.isEmpty()) {
                typedOutputList.add(new ItemStackWithSlot(i, itemStack));
            }
        }
    }

    /**
     * Read item stacks from an input.
     *
     * @param valueInput the value input
     * @param container  the container
     * @see net.minecraft.world.ContainerHelper#loadAllItems(ValueInput, NonNullList)
     */
    public static void loadAllItems(ValueInput valueInput, Container container) {
        fromSlots(container, valueInput.listOrEmpty("Items", ItemStackWithSlot.CODEC));
    }

    /**
     * Read item stacks from an input.
     *
     * @param container      the container
     * @param typedInputList the input list
     * @see net.minecraft.world.inventory.PlayerEnderChestContainer#fromSlots(ValueInput.TypedInputList)
     */
    public static void fromSlots(Container container, ValueInput.TypedInputList<ItemStackWithSlot> typedInputList) {
        container.clearContent();
        for (ItemStackWithSlot itemStackWithSlot : typedInputList) {
            if (itemStackWithSlot.isValidInContainer(container.getContainerSize())) {
                container.setItem(itemStackWithSlot.slot(), itemStackWithSlot.stack());
            }
        }
    }
}
