package fuzs.puzzleslib.api.block.v1;

import fuzs.puzzleslib.api.event.v1.server.TagsUpdatedCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Objects;

/**
 * A helper class containing utilities for replacing an existing block by modifying the corresponding {@link BlockItem} and copying block tags.
 */
public final class BlockConversionHelper {

    private BlockConversionHelper() {

    }

    /**
     * Allows for updating both a {@link Block} and corresponding {@link BlockItem} simultaneously.
     *
     * @param item  the block item to update
     * @param block the block to update
     */
    public static void setBlockItemBlock(BlockItem item, Block block) {
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
    public static void setItemForBlock(Block block, Item item) {
        Objects.requireNonNull(block, "block " + (item != null ? "for item '" + BuiltInRegistries.ITEM.getKey(item) + "' " : "") + "is null");
        Objects.requireNonNull(item, "item for block '" + BuiltInRegistries.BLOCK.getKey(block) + "' is null");
        Item.BY_BLOCK.put(block, item);
        block.item = item;
    }

    /**
     * Allows for updating the stored {@link Block} on a {@link BlockItem}.
     * <p>Useful for switching the corresponding block implementation without the need to modify the original block.
     *
     * @param item  the block item to set the new block for
     * @param block the new block
     */
    public static void setBlockForItem(BlockItem item, Block block) {
        Objects.requireNonNull(item, "item " + (block != null ? "for block '" + BuiltInRegistries.BLOCK.getKey(block) + "' " : "") + "is null");
        Objects.requireNonNull(block, "block for item '" + BuiltInRegistries.ITEM.getKey(item) + "' is null");
        Block oldBlock = item.getBlock();
        // block can somehow be null on Forge apparently
        if (oldBlock != null) oldBlock.item = item;
        item.block = block;
    }

    /**
     * Allows for copying tags bound to one block to another.
     * <p>Ideally called after tags have been updated like in {@link TagsUpdatedCallback}.
     *
     * @param from the source block to copy tags from
     * @param to   the target block to copy tags to
     */
    @SuppressWarnings("deprecation")
    public static void copyBoundTags(Block from, Block to) {
        Objects.requireNonNull(from, "source " + (to != null ? "for target '" + BuiltInRegistries.BLOCK.getKey(to) + "' " : "") + "is null");
        Objects.requireNonNull(to, "target for source '" + BuiltInRegistries.BLOCK.getKey(from) + "' is null");
        if (to.builtInRegistryHolder().tags().findAny().isPresent()) {
            throw new IllegalStateException("target block tags not empty");
        }
        List<TagKey<Block>> tagKeys = from.builtInRegistryHolder().tags().toList();
        to.builtInRegistryHolder().bindTags(tagKeys);
    }
}
