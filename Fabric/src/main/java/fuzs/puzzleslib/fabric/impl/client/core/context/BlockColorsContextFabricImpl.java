package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BlockColorsContext;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Objects;

public final class BlockColorsContextFabricImpl implements BlockColorsContext {

    @Override
    public void registerBlockColor(Block block, List<BlockTintSource> blockColors) {
        Objects.requireNonNull(block, "block is null");
        Objects.requireNonNull(blockColors, "block color is null");
        BlockColorRegistry.register(blockColors, block);
    }
}
