package fuzs.puzzleslib.api.client.core.v1.context;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.renderer.RenderType;

import java.util.Objects;

/**
 * Register a render buffer for a {@link RenderType} that is added in
 * {@link net.minecraft.client.renderer.RenderBuffers#RenderBuffers(int)}.
 */
public interface RenderBuffersContext {

    /**
     * Registers a render buffer for the specified render type.
     *
     * @param renderType the render type of the render buffer
     */
    default void registerRenderBuffer(RenderType renderType) {
        Objects.requireNonNull(renderType, "render type is null");
        this.registerRenderBuffer(renderType, new ByteBufferBuilder(renderType.bufferSize()));
    }

    /**
     * Registers a render buffer for the specified render type.
     *
     * @param renderType   the render type of the render buffer
     * @param renderBuffer the render buffer
     */
    void registerRenderBuffer(RenderType renderType, ByteBufferBuilder renderBuffer);
}
