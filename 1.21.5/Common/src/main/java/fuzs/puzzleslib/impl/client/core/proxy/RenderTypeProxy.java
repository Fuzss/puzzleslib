package fuzs.puzzleslib.impl.client.core.proxy;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public interface RenderTypeProxy {

    RenderType getRenderType(Block block);

    void registerRenderType(Block block, RenderType renderType);

    void registerRenderType(Fluid fluid, RenderType renderType);
}
