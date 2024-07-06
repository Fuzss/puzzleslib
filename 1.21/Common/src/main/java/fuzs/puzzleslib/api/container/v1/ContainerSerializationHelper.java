package fuzs.puzzleslib.api.container.v1;

import net.minecraft.core.HolderLookup;
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
        // NO-OP
    }

    /**
     * Save items to a given tag, reading from an item list.
     * <p>
     * Equivalent to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList, HolderLookup.Provider)}.
     *
     * @param tag   tag to save to
     * @param items item list to save
     * @return the original tag
     */
    public static CompoundTag saveAllItems(CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider lookupProvider) {
        return ContainerHelper.saveAllItems(tag, items, lookupProvider);
    }

    /**
     * Save items to a given tag, reading from a container.
     * <p>
     * Similar to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList, HolderLookup.Provider)}.
     *
     * @param tag       tag to save to
     * @param container container to save
     * @return the original tag
     */
    public static CompoundTag saveAllItems(CompoundTag tag, Container container, HolderLookup.Provider lookupProvider) {
        return saveAllItems(tag, container, true, lookupProvider);
    }

    /**
     * Save items to a given tag, reading from an item list.
     * <p>
     * Similar to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList, HolderLookup.Provider)}.
     *
     * @param tagKey nbt tag key
     * @param tag    tag to save to
     * @param items  item list to save
     * @return the original tag
     */
    public static CompoundTag saveAllItems(String tagKey, CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider lookupProvider) {
        return saveAllItems(tagKey, tag, items, true, lookupProvider);
    }

    /**
     * Save items to a given tag, reading from a container.
     * <p>
     * Similar to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList, boolean, HolderLookup.Provider)}.
     *
     * @param tag       tag to save to
     * @param container container to save
     * @param saveEmpty save to tag if completely empty
     * @return the original tag
     */
    public static CompoundTag saveAllItems(CompoundTag tag, Container container, boolean saveEmpty, HolderLookup.Provider lookupProvider) {
        return saveAllItems(TAG_ITEMS,
                tag,
                container.getContainerSize(),
                container::getItem,
                saveEmpty,
                lookupProvider
        );
    }

    /**
     * Save items to a given tag, reading from an item list.
     * <p>
     * Equivalent to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList, boolean, HolderLookup.Provider)}.
     *
     * @param tag       tag to save to
     * @param items     item list to save
     * @param saveEmpty save to tag if completely empty
     * @return the original tag
     */
    public static CompoundTag saveAllItems(CompoundTag tag, NonNullList<ItemStack> items, boolean saveEmpty, HolderLookup.Provider lookupProvider) {
        return ContainerHelper.saveAllItems(tag, items, saveEmpty, lookupProvider);
    }

    /**
     * Save items to a given tag, reading from an item list.
     * <p>
     * Similar to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList, boolean, HolderLookup.Provider)}.
     *
     * @param tagKey    nbt tag key
     * @param tag       tag to save to
     * @param items     item list to save
     * @param saveEmpty save to tag if completely empty
     * @return the original tag
     */
    public static CompoundTag saveAllItems(String tagKey, CompoundTag tag, NonNullList<ItemStack> items, boolean saveEmpty, HolderLookup.Provider lookupProvider) {
        return saveAllItems(tagKey, tag, items.size(), items::get, saveEmpty, lookupProvider);
    }

    /**
     * Save items to a given tag, reading from a provider.
     * <p>
     * Similar to {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList, boolean, HolderLookup.Provider)}.
     *
     * @param tagKey     nbt tag key
     * @param tag        tag to save to
     * @param size       item provider size
     * @param itemGetter get items from the provider
     * @param saveEmpty  save to tag if completely empty
     * @return the original tag
     */
    public static CompoundTag saveAllItems(String tagKey, CompoundTag tag, int size, IntFunction<ItemStack> itemGetter, boolean saveEmpty, HolderLookup.Provider lookupProvider) {
        ListTag listTag = createTag(size, itemGetter, lookupProvider);
        if (!listTag.isEmpty() || saveEmpty) {
            tag.put(tagKey, listTag);
        }

        return tag;
    }

    /**
     * Create a list tag with all items from the provider. The tag is not saved anywhere yet.
     * <p>
     * Equivalent to {@link SimpleContainer#createTag(HolderLookup.Provider)}.
     *
     * @param size       item provider size
     * @param itemGetter get items from the provider
     * @return the list tag
     */
    public static ListTag createTag(int size, IntFunction<ItemStack> itemGetter, HolderLookup.Provider lookupProvider) {
        ListTag listTag = new ListTag();
        for (int i = 0; i < size; ++i) {
            ItemStack itemStack = itemGetter.apply(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte(TAG_SLOT, (byte) i);
                listTag.add(itemStack.save(lookupProvider, compoundTag));
            }
        }
        return listTag;
    }

    /**
     * Read items from a given tag, saving them to an item list.
     * <p>
     * Equivalent to {@link ContainerHelper#loadAllItems(CompoundTag, NonNullList, HolderLookup.Provider)}.
     *
     * @param tag   tag to read from
     * @param items item list to fill
     */
    public static void loadAllItems(CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider lookupProvider) {
        ContainerHelper.loadAllItems(tag, items, lookupProvider);
    }

    /**
     * Read items from a given tag, saving them to a container.
     * <p>
     * Similar to {@link ContainerHelper#loadAllItems(CompoundTag, NonNullList, HolderLookup.Provider)}.
     *
     * @param tag       tag to read from
     * @param container container to fill
     */
    public static void loadAllItems(CompoundTag tag, Container container, HolderLookup.Provider lookupProvider) {
        loadAllItems(TAG_ITEMS, tag, container.getContainerSize(), (ItemStack stack, int value) -> {
            container.setItem(value, stack);
        }, lookupProvider);
    }

    /**
     * Read items from a given tag, saving them to an item list.
     * <p>
     * Similar to {@link ContainerHelper#loadAllItems(CompoundTag, NonNullList, HolderLookup.Provider)}.
     *
     * @param tagKey nbt tag key
     * @param tag    tag to read from
     * @param items  item list to fill
     */
    public static void loadAllItems(String tagKey, CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider lookupProvider) {
        loadAllItems(tagKey, tag, items.size(), (ItemStack stack, int value) -> {
            items.set(value, stack);
        }, lookupProvider);
    }

    /**
     * Read items from a given tag, saving to a provider.
     * <p>
     * Similar to {@link ContainerHelper#loadAllItems(CompoundTag, NonNullList, HolderLookup.Provider)}.
     *
     * @param tagKey     nbt tag key
     * @param tag        tag to read from
     * @param size       item provider size
     * @param itemSetter set items to the provider
     */
    public static void loadAllItems(String tagKey, CompoundTag tag, int size, ObjIntConsumer<ItemStack> itemSetter, HolderLookup.Provider lookupProvider) {
        ListTag listTag = tag.getList(tagKey, Tag.TAG_COMPOUND);
        fromTag(listTag, size, itemSetter, lookupProvider);
    }

    /**
     * Read items from a given tag, saving to a provider.
     * <p>
     * Equivalent to {@link net.minecraft.world.SimpleContainer#fromTag(ListTag, HolderLookup.Provider)}.
     *
     * @param listTag    the list tag
     * @param size       item provider size
     * @param itemSetter set items to the provider
     */
    public static void fromTag(ListTag listTag, int size, ObjIntConsumer<ItemStack> itemSetter, HolderLookup.Provider lookupProvider) {
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int slot = compoundTag.getByte(TAG_SLOT) & 255;
            if (slot < size) {
                itemSetter.accept(ItemStack.parse(lookupProvider, compoundTag).orElse(ItemStack.EMPTY), slot);
            }
        }
    }
}
