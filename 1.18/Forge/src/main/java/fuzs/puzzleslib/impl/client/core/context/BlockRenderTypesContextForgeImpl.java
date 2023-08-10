package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class BlockRenderTypesContextForgeImpl implements RenderTypesContext<Block> {

    @Override
    public void registerRenderType(RenderType renderType, Block... blocks) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(blocks, "blocks is null");
        Preconditions.checkPositionIndex(1, blocks.length, "blocks is empty");
        for (Block block : blocks) {
            Objects.requireNonNull(block, "block is null");
            ItemBlockRenderTypes.setRenderLayer(block, renderType);
        }
    }
}
