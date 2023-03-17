package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.ColorProvidersContext;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class BlockColorProvidersContextFabricImpl implements ColorProvidersContext<Block, BlockColor> {

    @Override
    public void registerColorProvider(BlockColor provider, Block object, Block... objects) {
        Objects.requireNonNull(provider, "provider is null");
        this.registerItemColorProvider(object, provider);
        Objects.requireNonNull(objects, "blocks is null");
        for (Block block : objects) {
            this.registerItemColorProvider(block, provider);
        }
    }

    private void registerItemColorProvider(Block block, BlockColor provider) {
        Objects.requireNonNull(block, "block is null");
        ColorProviderRegistry.BLOCK.register(provider, block);
    }

    @Override
    public BlockColor getProviders() {
        return (blockState, blockAndTintGetter, blockPos, i) -> {
            BlockColor blockColor = ColorProviderRegistry.BLOCK.get(blockState.getBlock());
            return blockColor == null ? -1 : blockColor.getColor(blockState, blockAndTintGetter, blockPos, i);
        };
    }
}
