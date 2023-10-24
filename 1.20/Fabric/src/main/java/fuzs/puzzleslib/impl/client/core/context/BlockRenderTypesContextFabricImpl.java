package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class BlockRenderTypesContextFabricImpl implements RenderTypesContext<Block> {

    @Override
    public void registerRenderType(RenderType renderType, Block... blocks) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(blocks, "blocks is null");
        Preconditions.checkPositionIndex(1, blocks.length, "blocks is empty");
        for (Block block : blocks) {
            Objects.requireNonNull(block, "block is null");
            BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
        }
    }

    @Override
    public RenderType getRenderType(Block object) {
        return ClientAbstractions.INSTANCE.getRenderType(object);
    }
}
