package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BlockColorsContext;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class BlockBlockColorsContextFabricImpl implements BlockColorsContext {

    @Override
    public void registerBlockColor(BlockColor blockColor, Block block) {
        Objects.requireNonNull(blockColor, "block color is null");
        Objects.requireNonNull(block, "block is null");
        ColorProviderRegistry.BLOCK.register(blockColor, block);
    }

    @Override
    public @Nullable BlockColor getBlockColor(Block block) {
        return ColorProviderRegistry.BLOCK.get(block);
    }
}
