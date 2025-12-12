package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import fuzs.puzzleslib.api.client.core.v1.context.RenderBuffersContext;
import net.minecraft.client.renderer.rendertype.RenderType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class RenderBuffersContextFabricImpl implements RenderBuffersContext {
    private static final Map<RenderType, ByteBufferBuilder> RENDER_TYPE_BUFFER_BUILDERS = new LinkedHashMap<>();

    @Override
    public void registerRenderBuffer(RenderType renderType, ByteBufferBuilder renderBuffer) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(renderBuffer, "render buffer is null");
        RENDER_TYPE_BUFFER_BUILDERS.put(renderType, renderBuffer);
    }

    public static void addAll(Map<RenderType, ByteBufferBuilder> renderBuffers) {
        renderBuffers.putAll(RENDER_TYPE_BUFFER_BUILDERS);
    }
}
