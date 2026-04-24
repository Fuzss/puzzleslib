package fuzs.puzzleslib.common.api.client.core.v1.context;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Register block color providers, like tint getters for leaves or grass.
 */
public interface BlockColorsContext {

    /**
     * Register a new block tint source.
     *
     * @param block      the block
     * @param blockColor the {@link BlockTintSource}
     */
    default void registerBlockColor(Block block, BlockTintSource blockColor) {
        Objects.requireNonNull(blockColor, "block color is null");
        this.registerBlockColor(block, Collections.singletonList(blockColor));
    }

    /**
     * Register a new block tint source.
     *
     * @param block       the block
     * @param blockColors the {@link BlockTintSource BlockTintSources}
     */
    void registerBlockColor(Block block, List<BlockTintSource> blockColors);
}
