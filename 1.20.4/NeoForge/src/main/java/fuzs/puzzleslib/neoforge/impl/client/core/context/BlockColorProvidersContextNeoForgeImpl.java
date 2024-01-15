package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.ColorProvidersContext;
import fuzs.puzzleslib.neoforge.mixin.client.accessor.BlockColorsNeoForgeAccessor;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

public record BlockColorProvidersContextNeoForgeImpl(BiConsumer<BlockColor, Block> consumer,
                                                     BlockColors blockColors) implements ColorProvidersContext<Block, BlockColor> {

    @Override
    public void registerColorProvider(BlockColor provider, Block... blocks) {
        Objects.requireNonNull(provider, "provider is null");
        Objects.requireNonNull(blocks, "blocks is null");
        Preconditions.checkPositionIndex(1, blocks.length, "blocks is empty");
        for (Block block : blocks) {
            Objects.requireNonNull(block, "block is null");
            this.consumer.accept(provider, block);
        }
    }

    @Override
    public @Nullable BlockColor getProvider(Block block) {
        return ((BlockColorsNeoForgeAccessor) this.blockColors).puzzleslib$getBlockColors().get(ForgeRegistries.BLOCKS.getDelegateOrThrow(block));
    }
}
