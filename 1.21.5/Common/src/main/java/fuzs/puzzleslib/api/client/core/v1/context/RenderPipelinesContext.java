package fuzs.puzzleslib.api.client.core.v1.context;

import com.mojang.blaze3d.pipeline.RenderPipeline;

/**
 * Register new render pipelines to {@link net.minecraft.client.renderer.RenderPipelines}.
 * <p>
 * TODO remove access widener for {@link net.minecraft.client.renderer.RenderPipelines#register(RenderPipeline)} and use this instead
 */
@FunctionalInterface
public interface RenderPipelinesContext {

    /**
     * @param renderPipeline the render pipeline
     */
    void registerRenderPipeline(RenderPipeline renderPipeline);
}
