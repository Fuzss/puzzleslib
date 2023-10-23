package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.mixin.accessor.BlockAccessor;
import fuzs.puzzleslib.mixin.accessor.BlockItemAccessor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface RegistryEntryAddedCallback<T> {

    @SuppressWarnings("unchecked")
    static <T> EventInvoker<RegistryEntryAddedCallback<T>> registryEntryAdded(ResourceKey<? extends Registry<T>> resourceKey) {
        Objects.requireNonNull(resourceKey, "resource key is null");
        return EventInvoker.lookup((Class<RegistryEntryAddedCallback<T>>) (Class<?>) RegistryEntryAddedCallback.class, resourceKey);
    }

    /**
     * A callback that runs whenever a new entry is added to a {@link Registry}.
     * <p>Note that the implementation is only designed for built-in registries and probably will not work with dynamic registries.
     *
     * @param registry  the read-only registry
     * @param id        the identifier for the added entry
     * @param entry     the added entry
     * @param registrar access to the registry for adding additional entries
     */
    void onRegistryEntryAdded(Registry<T> registry, ResourceLocation id, T entry, BiConsumer<ResourceLocation, Supplier<T>> registrar);

    /**
     * Allows for updating both a {@link Block} and corresponding {@link BlockItem} simultaneously.
     *
     * @param item  the block item to update
     * @param block the block to update
     */
    static void setBlockItemBlock(BlockItem item, Block block) {
        setItemForBlock(block, item);
        setBlockForItem(item, block);
    }

    /**
     * Allows for updating the stored {@link Item} on a {@link Block}.
     * <p>Note that the implementation is not restricted to {@link BlockItem}.
     *
     * @param block the block to set the new item for
     * @param item  the new item
     */
    static void setItemForBlock(Block block, Item item) {
        Objects.requireNonNull(block, "block is null");
        Objects.requireNonNull(item, "item is null");
        Item.BY_BLOCK.put(block, item);
        ((BlockAccessor) block).puzzleslib$setItem(item);
    }

    /**
     * Allows for updating the stored {@link Block} on a {@link BlockItem}.
     * <p>Useful for switching the corresponding block implementation without the need to modify the original block.
     *
     * @param item  the block item to set the new block for
     * @param block the new block
     */
    static void setBlockForItem(BlockItem item, Block block) {
        Objects.requireNonNull(item, "item is null");
        Objects.requireNonNull(block, "block is null");
        Block oldBlock = item.getBlock();
        // block can somehow be null on Forge apparently
        if (oldBlock != null) ((BlockAccessor) oldBlock).puzzleslib$setItem(item);
        ((BlockItemAccessor) item).puzzleslib$setBlock(block);
    }
}
