package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.RenderTypesContext;
import fuzs.puzzleslib.api.core.v1.contexts.MultiRegistrationContext;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.Fluid;

public final class FluidRenderTypesContextFabricImpl implements RenderTypesContext<Fluid>, MultiRegistrationContext<Fluid, RenderType> {

    @Override
    public void registerRenderType(RenderType renderType, Fluid object, Fluid... objects) {
        this.register(renderType, object, objects);
    }

    @Override
    public void register(Fluid object, RenderType type) {
        BlockRenderLayerMap.INSTANCE.putFluid(object, type);
    }
}
