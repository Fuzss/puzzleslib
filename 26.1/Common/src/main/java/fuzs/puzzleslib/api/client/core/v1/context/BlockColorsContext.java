package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

/**
 * Register block color providers, like tint getters for leaves or grass.
 */
public interface BlockColorsContext {

    /**
     * Register a new block color provider.
     *
     * @param block      the block
     * @param blockColor the {@link BlockColor}
     */
    void registerBlockColor(Block block, BlockColor blockColor);

    /**
     * Provides access to already registered block color providers.
     * <p>
     * Might be incomplete during registration, but is good to use as long as it doesn't try to retrieve itself.
     *
     * @return the registered {@link BlockColor}
     */
    @Nullable BlockColor getBlockColor(Block block);
}
