package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import fuzs.puzzleslib.api.client.core.v1.context.RenderPipelinesContext;
import net.minecraft.client.renderer.RenderPipelines;

import java.util.Objects;

public final class RenderPipelinesContextFabricImpl implements RenderPipelinesContext {

    @Override
    public void registerRenderPipeline(RenderPipeline renderPipeline) {
        Objects.requireNonNull(renderPipeline, "render pipeline is null");
        RenderPipelines.register(renderPipeline);
    }
}
