package fuzs.puzzleslib.api.client.core.v1.context;

import com.google.common.base.Preconditions;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Register block color providers, like tint getters for leaves or grass.
 */
public interface BlockColorsContext {

    /**
     * Register a new block color provider.
     *
     * @param blockColor the {@link BlockColor} instance
     * @param block      the block to register for
     */
    void registerBlockColor(BlockColor blockColor, Block block);

    /**
     * Register a new block color provider.
     *
     * @param blockColor the {@link BlockColor} instance
     * @param blocks     the blocks to register for
     */
    default void registerBlockColor(BlockColor blockColor, Block... blocks) {
        Objects.requireNonNull(blocks, "blocks is null");
        Preconditions.checkState(blocks.length > 0, "blocks is empty");
        for (Block block : blocks) {
            this.registerBlockColor(blockColor, block);
        }
    }

    /**
     * Provides access to already registered block color providers.
     * <p>
     * Might be incomplete during registration, but is good to use as long as it doesn't try to retrieve itself.
     *
     * @return the registered {@link net.minecraft.client.color.block.BlockColors} instance
     */
    @Nullable BlockColor getBlockColor(Block block);
}
