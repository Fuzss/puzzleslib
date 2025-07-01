package fuzs.puzzleslib.api.container.v1;

import fuzs.puzzleslib.impl.container.SlotsProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * An adjusted version of {@link net.minecraft.world.ContainerHelper} that allows for using {@link Container} instead of
 * {@link net.minecraft.core.NonNullList}, as well as a custom tag key.
 */
public final class ContainerSerializationHelper {

    private ContainerSerializationHelper() {
        // NO-OP
    }

    /**
     * Write item stacks to an output.
     *
     * @param valueOutput the value output
     * @param itemStacks  the item list
     * @see net.minecraft.world.ContainerHelper#saveAllItems(ValueOutput, NonNullList)
     */
    public static void saveAllItems(ValueOutput valueOutput, NonNullList<ItemStack> itemStacks) {
        storeAsSlots(itemStacks, valueOutput.list("Items", ItemStackWithSlot.CODEC));
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
     * @param itemStacks      the item list
     * @param typedOutputList the output list
     * @see net.minecraft.world.inventory.PlayerEnderChestContainer#storeAsSlots(ValueOutput.TypedOutputList)
     */
    public static void storeAsSlots(NonNullList<ItemStack> itemStacks, ValueOutput.TypedOutputList<ItemStackWithSlot> typedOutputList) {
        storeAsSlots(SlotsProvider.of(itemStacks), typedOutputList);
    }

    /**
     * Write item stacks to an output.
     *
     * @param container       the container
     * @param typedOutputList the output list
     * @see net.minecraft.world.inventory.PlayerEnderChestContainer#storeAsSlots(ValueOutput.TypedOutputList)
     */
    public static void storeAsSlots(Container container, ValueOutput.TypedOutputList<ItemStackWithSlot> typedOutputList) {
        storeAsSlots(SlotsProvider.of(container), typedOutputList);
    }

    private static void storeAsSlots(SlotsProvider slotsProvider, ValueOutput.TypedOutputList<ItemStackWithSlot> typedOutputList) {
        for (int i = 0; i < slotsProvider.getContainerSize(); i++) {
            ItemStack itemStack = slotsProvider.getItem(i);
            if (!itemStack.isEmpty()) {
                typedOutputList.add(new ItemStackWithSlot(i, itemStack));
            }
        }
    }

    /**
     * Read item stacks from an input.
     *
     * @param valueInput the value input
     * @param itemStacks the item list
     * @see net.minecraft.world.ContainerHelper#loadAllItems(ValueInput, NonNullList)
     */
    public static void loadAllItems(ValueInput valueInput, NonNullList<ItemStack> itemStacks) {
        fromSlots(itemStacks, valueInput.listOrEmpty("Items", ItemStackWithSlot.CODEC));
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
     * @param itemStacks     the item list
     * @param typedInputList the input list
     * @see net.minecraft.world.inventory.PlayerEnderChestContainer#fromSlots(ValueInput.TypedInputList)
     */
    public static void fromSlots(NonNullList<ItemStack> itemStacks, ValueInput.TypedInputList<ItemStackWithSlot> typedInputList) {
        fromSlots(SlotsProvider.of(itemStacks), typedInputList);
    }

    /**
     * Read item stacks from an input.
     *
     * @param container      the container
     * @param typedInputList the input list
     * @see net.minecraft.world.inventory.PlayerEnderChestContainer#fromSlots(ValueInput.TypedInputList)
     */
    public static void fromSlots(Container container, ValueInput.TypedInputList<ItemStackWithSlot> typedInputList) {
        fromSlots(SlotsProvider.of(container), typedInputList);
    }

    private static void fromSlots(SlotsProvider slotsProvider, ValueInput.TypedInputList<ItemStackWithSlot> typedInputList) {
        slotsProvider.clearContent();
        for (ItemStackWithSlot itemStackWithSlot : typedInputList) {
            if (itemStackWithSlot.isValidInContainer(slotsProvider.getContainerSize())) {
                slotsProvider.setItem(itemStackWithSlot.slot(), itemStackWithSlot.stack());
            }
        }
    }
}
