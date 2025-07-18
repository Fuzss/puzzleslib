package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;

public final class FluidRenderTypesContextFabricImpl implements RenderTypesContext<Fluid> {

    @Override
    public void registerChunkRenderType(Fluid fluid, ChunkSectionLayer chunkSectionLayer) {
        Objects.requireNonNull(fluid, "fluid is null");
        Objects.requireNonNull(chunkSectionLayer, "chunk section layer is null");
        BlockRenderLayerMap.putFluid(fluid, chunkSectionLayer);
    }

    @Override
    public ChunkSectionLayer getChunkRenderType(Fluid fluid) {
        Objects.requireNonNull(fluid, "fluid is null");
        return ItemBlockRenderTypes.getRenderLayer(fluid.defaultFluidState());
    }
}
