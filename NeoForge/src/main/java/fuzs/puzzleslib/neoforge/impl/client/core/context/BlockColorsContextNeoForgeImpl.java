package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BlockColorsContext;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;
import java.util.Objects;

public record BlockColorsContextNeoForgeImpl(RegisterColorHandlersEvent.BlockTintSources event) implements BlockColorsContext {

    @Override
    public void registerBlockColor(Block block, List<BlockTintSource> blockColors) {
        Objects.requireNonNull(block, "block is null");
        Objects.requireNonNull(blockColors, "block colors is null");
        this.event.register(blockColors, block);
    }
}
