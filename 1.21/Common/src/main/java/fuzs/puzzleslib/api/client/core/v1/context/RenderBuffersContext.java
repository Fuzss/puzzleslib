package fuzs.puzzleslib.api.client.core.v1.context;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.renderer.RenderType;

import java.util.Objects;

/**
 * Register a render buffer for a {@link RenderType} that is added in {@link net.minecraft.client.renderer.RenderBuffers#RenderBuffers(int)}.
 */
public interface RenderBuffersContext {

    /**
     * Registers a render buffer for the specified render type.
     *
     * @param renderTypes a render type of the render buffer
     */
    default void registerRenderBuffer(RenderType... renderTypes) {
        Objects.requireNonNull(renderTypes, "render types is null");
        Preconditions.checkState(renderTypes.length > 0, "render types is empty");
        for (RenderType renderType : renderTypes) {
            Objects.requireNonNull(renderType, "render type is null");
            this.registerRenderBuffer(renderType, new ByteBufferBuilder(renderType.bufferSize()));
        }
    }

    /**
     * Registers a render buffer for the specified render type.
     *
     * @param renderType   a render type of the render buffer
     * @param renderBuffer a render buffer to register
     */
    void registerRenderBuffer(RenderType renderType, ByteBufferBuilder renderBuffer);
}
