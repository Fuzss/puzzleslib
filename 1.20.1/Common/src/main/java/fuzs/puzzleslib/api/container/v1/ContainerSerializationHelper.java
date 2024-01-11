package fuzs.puzzleslib.api.container.v1;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * An adjusted version of {@link net.minecraft.world.ContainerHelper} that allows for using {@link Container} instead of {@link net.minecraft.core.NonNullList}.
 */
public class ContainerSerializationHelper {

    /**
     * Saves a container to a given tag.
     *
     * @param tag       tag to save to
     * @param container container to save
     * @return the instance passed as <code>tag</code>
     */
    public static CompoundTag saveAllItems(CompoundTag tag, Container container) {
        return saveAllItems(tag, container, true);
    }

    /**
     * Saves a container to a given tag.
     *
     * @param tag       tag to save to
     * @param container container to save
     * @param saveEmpty save to tag if completely empty
     * @return the instance passed as <code>tag</code>
     */
    public static CompoundTag saveAllItems(CompoundTag tag, Container container, boolean saveEmpty) {
        ListTag listTag = new ListTag();
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte) i);
                itemStack.save(compoundTag);
                listTag.add(compoundTag);
            }
        }
        if (!listTag.isEmpty() || saveEmpty) {
            tag.put("Items", listTag);
        }
        return tag;
    }

    /**
     * Read a container from a given tag.
     *
     * @param tag       tag to read from
     * @param container container to fill from the given tag
     */
    public static void loadAllItems(CompoundTag tag, Container container) {
        ListTag listTag = tag.getList("Items", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 255;
            if (j < container.getContainerSize()) {
                container.setItem(j, ItemStack.of(compoundTag));
            }
        }
    }
}
