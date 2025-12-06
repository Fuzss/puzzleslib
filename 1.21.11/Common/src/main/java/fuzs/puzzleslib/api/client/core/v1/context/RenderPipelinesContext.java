package fuzs.puzzleslib.api.client.core.v1.context;

import com.mojang.blaze3d.pipeline.RenderPipeline;

/**
 * Register new render pipelines to {@link net.minecraft.client.renderer.RenderPipelines}.
 */
public interface RenderPipelinesContext {

    /**
     * @param renderPipeline the render pipeline
     */
    void registerRenderPipeline(RenderPipeline renderPipeline);
}
