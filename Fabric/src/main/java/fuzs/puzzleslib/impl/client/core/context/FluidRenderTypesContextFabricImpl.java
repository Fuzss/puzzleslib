package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;

public final class FluidRenderTypesContextFabricImpl implements RenderTypesContext<Fluid> {

    @Override
    public void registerRenderType(RenderType renderType, Fluid... fluids) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(fluids, "fluids is null");
        Preconditions.checkPositionIndex(1, fluids.length, "fluids is empty");
        for (Fluid fluid : fluids) {
            Objects.requireNonNull(fluid, "fluid is null");
            BlockRenderLayerMap.INSTANCE.putFluid(fluid, renderType);
        }
    }
}
