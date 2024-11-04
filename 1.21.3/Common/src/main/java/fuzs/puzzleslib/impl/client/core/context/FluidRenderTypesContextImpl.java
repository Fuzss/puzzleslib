package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;

public final class FluidRenderTypesContextImpl implements RenderTypesContext<Fluid> {

    @Override
    public void registerRenderType(RenderType renderType, Fluid... fluids) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(fluids, "fluids is null");
        Preconditions.checkState(fluids.length > 0, "fluids is empty");
        for (Fluid fluid : fluids) {
            Objects.requireNonNull(fluid, "fluid is null");
            ClientAbstractions.INSTANCE.registerRenderType(fluid, renderType);
        }
    }

    @Override
    public RenderType getRenderType(Fluid object) {
        return ClientAbstractions.INSTANCE.getRenderType(object);
    }
}
