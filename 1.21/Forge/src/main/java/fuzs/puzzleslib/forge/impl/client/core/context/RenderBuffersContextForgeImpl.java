package fuzs.puzzleslib.forge.impl.client.core.context;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.BufferBuilder;
import fuzs.puzzleslib.api.client.core.v1.context.RenderBuffersContext;
import net.minecraft.client.renderer.RenderType;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class RenderBuffersContextForgeImpl implements RenderBuffersContext {
    private static final Set<Consumer<BiConsumer<RenderType, BufferBuilder>>> CONSUMERS = Sets.newConcurrentHashSet();

    @Override
    public void registerRenderBuffer(RenderType renderType, BufferBuilder renderBuffer) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(renderBuffer, "render buffer is null");
        CONSUMERS.add(buffers -> buffers.accept(renderType, renderBuffer));
    }

    public static void addAll(Map<RenderType, BufferBuilder> buffers) {
        CONSUMERS.forEach(factory -> factory.accept(buffers::put));
    }
}
