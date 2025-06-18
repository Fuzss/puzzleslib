package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.SpecialBlockModelRenderersContext;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterSpecialBlockModelRendererEvent;

import java.util.Objects;

public record SpecialBlockModelRenderersContextNeoForgeImpl(RegisterSpecialBlockModelRendererEvent event) implements SpecialBlockModelRenderersContext {

    @Override
    public void registerSpecialBlockModelRenderer(Block block, SpecialModelRenderer.Unbaked specialModelRenderer) {
        Objects.requireNonNull(block, "block is null");
        Objects.requireNonNull(specialModelRenderer, "special model renderer is null");
        this.event.register(block, specialModelRenderer);
    }
}
