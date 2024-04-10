package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class BlockRenderTypesContextImpl implements RenderTypesContext<Block> {

    @Override
    public void registerRenderType(RenderType renderType, Block... blocks) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(blocks, "blocks is null");
        Preconditions.checkState(blocks.length > 0, "blocks is empty");
        for (Block block : blocks) {
            Objects.requireNonNull(block, "block is null");
            ClientAbstractions.INSTANCE.registerRenderType(block, renderType);
        }
    }

    @Override
    public RenderType getRenderType(Block object) {
        return ClientAbstractions.INSTANCE.getRenderType(object);
    }
}
