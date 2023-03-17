package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.RenderTypesContext;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;

public final class FluidRenderTypesContextFabricImpl implements RenderTypesContext<Fluid> {

    @Override
    public void registerRenderType(RenderType renderType, Fluid object, Fluid... objects) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(object, "fluid is null");
        BlockRenderLayerMap.INSTANCE.putFluid(object, renderType);
        Objects.requireNonNull(objects, "fluids is null");
        for (Fluid fluid : objects) {
            Objects.requireNonNull(fluid, "fluid is null");
            BlockRenderLayerMap.INSTANCE.putFluid(fluid, renderType);
        }
    }
}
