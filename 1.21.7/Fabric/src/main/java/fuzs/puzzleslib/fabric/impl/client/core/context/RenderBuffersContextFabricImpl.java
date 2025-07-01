package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import fuzs.puzzleslib.api.client.core.v1.context.RenderBuffersContext;
import fuzs.puzzleslib.fabric.api.client.event.v1.registry.RenderBuffersRegistry;
import net.minecraft.client.renderer.RenderType;

import java.util.Objects;

public final class RenderBuffersContextFabricImpl implements RenderBuffersContext {

    @Override
    public void registerRenderBuffer(RenderType renderType, ByteBufferBuilder renderBuffer) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(renderBuffer, "render buffer is null");
        RenderBuffersRegistry.INSTANCE.register(renderType, renderBuffer);
    }
}
