package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class BlockRenderTypesContextNeoForgeImpl implements RenderTypesContext<Block> {

    @SuppressWarnings("deprecation")
    @Override
    public void registerChunkRenderType(Block block, ChunkSectionLayer chunkSectionLayer) {
        Objects.requireNonNull(block, "block is null");
        Objects.requireNonNull(chunkSectionLayer, "chunk section layer is null");
        ItemBlockRenderTypes.setRenderLayer(block, chunkSectionLayer);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ChunkSectionLayer getChunkRenderType(Block block) {
        Objects.requireNonNull(block, "block is null");
        return ItemBlockRenderTypes.getChunkRenderType(block.defaultBlockState());
    }
}
