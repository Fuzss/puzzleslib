package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import fuzs.puzzleslib.api.client.core.v1.context.RenderBuffersContext;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;

import java.util.Objects;

public record RenderBuffersContextNeoForgeImpl(RegisterRenderBuffersEvent event) implements RenderBuffersContext {

    @Override
    public void registerRenderBuffer(RenderType renderType, ByteBufferBuilder renderBuffer) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(renderBuffer, "render buffer is null");
        this.event.registerRenderBuffer(renderType, renderBuffer);
    }
}
