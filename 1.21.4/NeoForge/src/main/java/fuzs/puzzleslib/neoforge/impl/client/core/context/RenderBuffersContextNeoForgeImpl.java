package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import fuzs.puzzleslib.api.client.core.v1.context.RenderBuffersContext;
import net.minecraft.client.renderer.RenderType;

import java.util.Objects;
import java.util.function.BiConsumer;

public record RenderBuffersContextNeoForgeImpl(
        BiConsumer<RenderType, ByteBufferBuilder> consumer) implements RenderBuffersContext {

    @Override
    public void registerRenderBuffer(RenderType renderType, ByteBufferBuilder renderBuffer) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(renderBuffer, "render buffer is null");
        this.consumer.accept(renderType, renderBuffer);
    }
}
