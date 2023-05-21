package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.ColorProvidersContext;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.function.BiConsumer;

public record BlockColorProvidersContextForgeImpl(BiConsumer<BlockColor, Block> consumer,
                                                  BlockColors blockColors) implements ColorProvidersContext<Block, BlockColor> {

    @Override
    public void registerColorProvider(BlockColor provider, Block... blocks) {
        Objects.requireNonNull(provider, "provider is null");
        Objects.requireNonNull(blocks, "blocks is null");
        Preconditions.checkPositionIndex(0, blocks.length, "blocks is empty");
        for (Block block : blocks) {
            Objects.requireNonNull(block, "block is null");
            this.consumer.accept(provider, block);
        }
    }

    @Override
    public BlockColor getProviders() {
        return this.blockColors::getColor;
    }
}
