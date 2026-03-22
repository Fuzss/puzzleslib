package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.SpecialBlockModelRenderersContext;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialBlockRendererRegistry;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class SpecialBlockModelRenderersContextFabricImpl implements SpecialBlockModelRenderersContext {

    @Override
    public void registerSpecialBlockModelRenderer(Block block, SpecialModelRenderer.Unbaked specialModelRenderer) {
        Objects.requireNonNull(block, "block is null");
        Objects.requireNonNull(specialModelRenderer, "special model renderer is null");
        SpecialBlockRendererRegistry.register(block, specialModelRenderer);
    }
}
