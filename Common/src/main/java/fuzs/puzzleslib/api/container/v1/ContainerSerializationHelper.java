package fuzs.puzzleslib.api.container.v1;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;

/**
 * An adjusted version of {@link net.minecraft.world.ContainerHelper} that allows for using {@link Container} instead of
 * {@link net.minecraft.core.NonNullList}.
 * <p>
 * Also allows for changing the nbt key which is usually <code>Items</code>.
 */
public final class ContainerSerializationHelper extends ContainerHelper {
    public static final String TAG_ITEMS = "Items";
    public static final String TAG_SLOT = "Slot";

    private ContainerSerializationHelper() {

    }

    /**
     * Save items to a given tag, reading from an item list.
     * <p>
     * Equivalent to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList)}.
     *
     * @param tag   tag to save to
     * @param items item list to save
     * @return the original tag
     */
    public static CompoundTag saveAllItems(CompoundTag tag, NonNullList<ItemStack> items) {
        return ContainerHelper.saveAllItems(tag, items);
    }

    /**
     * Save items to a given tag, reading from a container.
     * <p>
     * Similar to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList)}.
     *
     * @param tag       tag to save to
     * @param container container to save
     * @return the original tag
     */
    public static CompoundTag saveAllItems(CompoundTag tag, Container container) {
        return saveAllItems(tag, container, true);
    }

    /**
     * Save items to a given tag, reading from an item list.
     * <p>
     * Similar to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList)}.
     *
     * @param tagKey nbt tag key
     * @param tag    tag to save to
     * @param items  item list to save
     * @return the original tag
     */
    public static CompoundTag saveAllItems(String tagKey, CompoundTag tag, NonNullList<ItemStack> items) {
        return saveAllItems(tagKey, tag, items, true);
    }

    /**
     * Save items to a given tag, reading from a container.
     * <p>
     * Similar to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList, boolean)}.
     *
     * @param tag       tag to save to
     * @param container container to save
     * @param saveEmpty save to tag if completely empty
     * @return the original tag
     */
    public static CompoundTag saveAllItems(CompoundTag tag, Container container, boolean saveEmpty) {
        return saveAllItems(TAG_ITEMS, tag, container.getContainerSize(), container::getItem, saveEmpty);
    }

    /**
     * Save items to a given tag, reading from an item list.
     * <p>
     * Equivalent to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList, boolean)}.
     *
     * @param tag       tag to save to
     * @param items     item list to save
     * @param saveEmpty save to tag if completely empty
     * @return the original tag
     */
    public static CompoundTag saveAllItems(CompoundTag tag, NonNullList<ItemStack> items, boolean saveEmpty) {
        return ContainerHelper.saveAllItems(tag, items, saveEmpty);
    }

    /**
     * Save items to a given tag, reading from an item list.
     * <p>
     * Similar to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList, boolean)}.
     *
     * @param tagKey    nbt tag key
     * @param tag       tag to save to
     * @param items     item list to save
     * @param saveEmpty save to tag if completely empty
     * @return the original tag
     */
    public static CompoundTag saveAllItems(String tagKey, CompoundTag tag, NonNullList<ItemStack> items, boolean saveEmpty) {
        return saveAllItems(tagKey, tag, items.size(), items::get, saveEmpty);
    }

    /**
     * Save items to a given tag, reading from a provider.
     * <p>
     * Similar to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList, boolean)}.
     *
     * @param tagKey     nbt tag key
     * @param tag        tag to save to
     * @param size       item provider size
     * @param itemGetter get items from the provider
     * @param saveEmpty  save to tag if completely empty
     * @return the original tag
     */
    public static CompoundTag saveAllItems(String tagKey, CompoundTag tag, int size, IntFunction<ItemStack> itemGetter, boolean saveEmpty) {
        ListTag listTag = createTag(size, itemGetter);
        if (!listTag.isEmpty() || saveEmpty) {
            tag.put(tagKey, listTag);
        }

        return tag;
    }

    /**
     * Create a list tag with all items from the provider. The tag is not saved anywhere yet.
     * <p>
     * Equivalent to {@link SimpleContainer#createTag()}.
     *
     * @param size       item provider size
     * @param itemGetter get items from the provider
     * @return the list tag
     */
    public static ListTag createTag(int size, IntFunction<ItemStack> itemGetter) {
        ListTag listTag = new ListTag();
        for (int i = 0; i < size; ++i) {
            ItemStack itemStack = itemGetter.apply(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte(TAG_SLOT, (byte) i);
                itemStack.save(compoundTag);
                listTag.add(compoundTag);
            }
        }
        return listTag;
    }

    /**
     * Read items from a given tag, saving them to an item list.
     * <p>
     * Equivalent to {@link ContainerHelper#loadAllItems(CompoundTag, NonNullList)}.
     *
     * @param tag   tag to read from
     * @param items item list to fill
     */
    public static void loadAllItems(CompoundTag tag, NonNullList<ItemStack> items) {
        ContainerHelper.loadAllItems(tag, items);
    }

    /**
     * Read items from a given tag, saving them to a container.
     * <p>
     * Similar to {@link ContainerHelper#loadAllItems(CompoundTag, NonNullList)}.
     *
     * @param tag       tag to read from
     * @param container container to fill
     */
    public static void loadAllItems(CompoundTag tag, Container container) {
        loadAllItems(TAG_ITEMS, tag, container.getContainerSize(), (ItemStack stack, int value) -> {
            container.setItem(value, stack);
        });
    }

    /**
     * Read items from a given tag, saving them to an item list.
     * <p>
     * Similar to {@link ContainerHelper#loadAllItems(CompoundTag, NonNullList)}.
     *
     * @param tagKey nbt tag key
     * @param tag    tag to read from
     * @param items  item list to fill
     */
    public static void loadAllItems(String tagKey, CompoundTag tag, NonNullList<ItemStack> items) {
        loadAllItems(tagKey, tag, items.size(), (ItemStack stack, int value) -> {
            items.set(value, stack);
        });
    }

    /**
     * Read items from a given tag, saving to a provider.
     * <p>
     * Similar to {@link ContainerHelper#loadAllItems(CompoundTag, NonNullList)}.
     *
     * @param tagKey     nbt tag key
     * @param tag        tag to read from
     * @param size       item provider size
     * @param itemSetter set items to the provider
     */
    public static void loadAllItems(String tagKey, CompoundTag tag, int size, ObjIntConsumer<ItemStack> itemSetter) {
        ListTag listTag = tag.getList(tagKey, Tag.TAG_COMPOUND);
        fromTag(listTag, size, itemSetter);
    }

    /**
     * Read items from a given tag, saving to a provider.
     * <p>
     * Equivalent to {@link net.minecraft.world.SimpleContainer#fromTag(ListTag)}.
     *
     * @param listTag    the list tag
     * @param size       item provider size
     * @param itemSetter set items to the provider
     */
    public static void fromTag(ListTag listTag, int size, ObjIntConsumer<ItemStack> itemSetter) {
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int slot = compoundTag.getByte(TAG_SLOT) & 255;
            if (slot < size) {
                itemSetter.accept(ItemStack.of(compoundTag), slot);
            }
        }
    }
}
