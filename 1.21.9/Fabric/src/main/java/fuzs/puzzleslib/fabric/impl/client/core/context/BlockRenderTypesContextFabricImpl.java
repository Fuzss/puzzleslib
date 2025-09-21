package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class BlockRenderTypesContextFabricImpl implements RenderTypesContext<Block> {

    @Override
    public void registerChunkRenderType(Block block, ChunkSectionLayer chunkSectionLayer) {
        Objects.requireNonNull(block, "block is null");
        Objects.requireNonNull(chunkSectionLayer, "chunk section layer is null");
        BlockRenderLayerMap.putBlock(block, chunkSectionLayer);
    }

    @Override
    public ChunkSectionLayer getChunkRenderType(Block block) {
        Objects.requireNonNull(block, "block is null");
        return ItemBlockRenderTypes.getChunkRenderType(block.defaultBlockState());
    }
}
