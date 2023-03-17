package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.RenderTypesContext;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class BlockRenderTypesContextFabricImpl implements RenderTypesContext<Block> {
    @Override
    public void registerRenderType(RenderType renderType, Block object, Block... objects) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(object, "block is null");
        BlockRenderLayerMap.INSTANCE.putBlock(object, renderType);
        Objects.requireNonNull(objects, "blocks is null");
        for (Block block : objects) {
            Objects.requireNonNull(block, "block is null");
            BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
        }
    }
}
