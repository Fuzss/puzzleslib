package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.mojang.blaze3d.vertex.BufferBuilder;
import fuzs.puzzleslib.api.client.core.v1.context.RenderBuffersContext;
import net.minecraft.client.renderer.RenderType;

import java.util.Objects;
import java.util.function.BiConsumer;

public record RenderBuffersContextNeoForgeImpl(
        BiConsumer<RenderType, BufferBuilder> consumer) implements RenderBuffersContext {

    @Override
    public void registerRenderBuffer(RenderType renderType, BufferBuilder renderBuffer) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(renderBuffer, "render buffer is null");
        this.consumer.accept(renderType, renderBuffer);
    }
}
