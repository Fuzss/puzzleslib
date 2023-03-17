package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.ColorProvidersContext;
import fuzs.puzzleslib.api.core.v1.contexts.MultiRegistrationContext;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;

public final class BlockColorProvidersContextFabricImpl implements ColorProvidersContext<Block, BlockColor>, MultiRegistrationContext<Block, BlockColor> {

    @Override
    public void registerColorProvider(BlockColor provider, Block object, Block... objects) {
        this.register(provider, object, objects);
    }

    @Override
    public BlockColor getProviders() {
        return (blockState, blockAndTintGetter, blockPos, i) -> {
            BlockColor blockColor = ColorProviderRegistry.BLOCK.get(blockState.getBlock());
            return blockColor == null ? -1 : blockColor.getColor(blockState, blockAndTintGetter, blockPos, i);
        };
    }

    @Override
    public void register(Block object, BlockColor type) {
        ColorProviderRegistry.BLOCK.register(type, object);
    }
}
