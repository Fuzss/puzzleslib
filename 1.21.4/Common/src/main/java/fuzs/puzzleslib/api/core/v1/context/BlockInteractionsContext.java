package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Register various block transformations triggered by right-clicking with certain vanilla tools.
 */
public interface BlockInteractionsContext {

    /**
     * A conversion usually used for removing the bark from log or wood blocks. Requires an axe.
     *
     * @param strippedBlock    the block after stripping
     * @param unstrippedBlocks the block before stripping
     */
    void registerStrippable(Block strippedBlock, Block... unstrippedBlocks);

    /**
     * A conversion usually used for removing oxidation from copper blocks. Requires an axe.
     *
     * @param scrapedBlock    the block after scraping
     * @param unscrapedBlocks the block before scraping
     */
    void registerScrapeable(Block scrapedBlock, Block... unscrapedBlocks);

    /**
     * A conversion usually used for removing wax from copper blocks. Requires an axe.
     *
     * @param unwaxedBlock the block before waxing
     * @param waxedBlocks  the block after waxing
     */
    void registerWaxable(Block unwaxedBlock, Block... waxedBlocks);

    /**
     * A conversion usually used for turning dirt blocks into dirt path. Requires a shovel.
     *
     * @param flattenedBlock    the block after flattening
     * @param unflattenedBlocks the block before flattening
     */
    default void registerFlattenable(Block flattenedBlock, Block... unflattenedBlocks) {
        this.registerFlattenable(flattenedBlock.defaultBlockState(), unflattenedBlocks);
    }

    /**
     * A conversion usually used for turning dirt-like blocks into dirt path. Requires a shovel.
     *
     * @param flattenedBlock    the block after flattening
     * @param unflattenedBlocks the block before flattening
     */
    void registerFlattenable(BlockState flattenedBlock, Block... unflattenedBlocks);

    /**
     * A conversion usually used for turning dirt-like blocks into farmland. Requires a hoe.
     * <p>
     * An air block is required above the untilled block.
     *
     * @param tilledBlock    the block after tilling
     * @param untilledBlocks the block before tilling
     */
    default void registerTillable(Block tilledBlock, Block... untilledBlocks) {
        this.registerTillable(tilledBlock.defaultBlockState(), null, true, untilledBlocks);
    }

    /**
     * A conversion usually used for turning dirt-like blocks into farmland. Requires a hoe.
     * <p>
     * No air block is required above the untilled block.
     *
     * @param tilledBlock    the block after tilling
     * @param droppedItem    a potential item dropped during the tilling interaction
     * @param untilledBlocks the block before tilling
     */
    default void registerTillable(Block tilledBlock, @Nullable ItemLike droppedItem, Block... untilledBlocks) {
        this.registerTillable(tilledBlock.defaultBlockState(), droppedItem, false, untilledBlocks);
    }

    /**
     * A conversion usually used for turning dirt-like blocks into farmland. Requires a hoe.
     *
     * @param tilledBlock     the block after tilling
     * @param droppedItem     a potential item dropped during the tilling interaction
     * @param requireAirAbove is an air block required above the untilled block for the interaction to be possible
     * @param untilledBlocks  the block before tilling
     */
    void registerTillable(BlockState tilledBlock, @Nullable ItemLike droppedItem, boolean requireAirAbove, Block... untilledBlocks);
}
