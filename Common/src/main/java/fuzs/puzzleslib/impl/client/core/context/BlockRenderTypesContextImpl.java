package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.client.renderer.v1.RenderTypeHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class BlockRenderTypesContextImpl implements RenderTypesContext<Block> {

    @Override
    public void registerRenderType(Block block, RenderType renderType) {
        Objects.requireNonNull(block, "block is null");
        Objects.requireNonNull(renderType, "render type is null");
        RenderTypeHelper.registerRenderType(block, renderType);
    }

    @Override
    public RenderType getRenderType(Block object) {
        return ClientAbstractions.INSTANCE.getRenderType(object);
    }
}
