package fuzs.puzzleslib.fabric.impl.client.event;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import fuzs.puzzleslib.fabric.api.client.event.v1.registry.RenderBuffersRegistry;
import net.minecraft.client.renderer.RenderType;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class RenderBuffersRegistryImpl implements RenderBuffersRegistry {
    private static final Set<Consumer<BiConsumer<RenderType, ByteBufferBuilder>>> CONSUMERS = Sets.newLinkedHashSet();

    @Override
    public void register(RenderType renderType, ByteBufferBuilder renderBuffer) {
        Objects.requireNonNull(renderType, "render type is null");
        Objects.requireNonNull(renderBuffer, "render buffer is null");
        CONSUMERS.add(buffers -> buffers.accept(renderType, renderBuffer));
    }

    public static void addAll(Map<RenderType, ByteBufferBuilder> buffers) {
        CONSUMERS.forEach(factory -> factory.accept(buffers::put));
    }
}
