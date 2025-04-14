package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import fuzs.puzzleslib.api.client.core.v1.context.RenderPipelinesContext;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import java.util.Objects;

public record RenderPipelinesContextNeoForgeImpl(RegisterRenderPipelinesEvent evt) implements RenderPipelinesContext {

    @Override
    public void registerRenderPipeline(RenderPipeline renderPipeline) {
        Objects.requireNonNull(renderPipeline, "render pipeline is null");
        this.evt.registerPipeline(renderPipeline);
    }
}
