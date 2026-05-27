package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.client.renderer.v1.RenderTypeHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;

public final class FluidRenderTypesContextImpl implements RenderTypesContext<Fluid> {

    @Override
    public void registerRenderType(Fluid fluid, RenderType renderType) {
        Objects.requireNonNull(fluid, "fluid is null");
        Objects.requireNonNull(renderType, "render type is null");
        RenderTypeHelper.registerRenderType(fluid, renderType);
    }

    @Override
    public RenderType getRenderType(Fluid object) {
        return ClientAbstractions.INSTANCE.getRenderType(object);
    }
}
