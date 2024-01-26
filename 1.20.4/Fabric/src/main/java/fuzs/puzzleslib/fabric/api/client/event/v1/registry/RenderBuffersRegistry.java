package fuzs.puzzleslib.fabric.api.client.event.v1.registry;

import com.mojang.blaze3d.vertex.BufferBuilder;
import fuzs.puzzleslib.fabric.impl.client.event.RenderBuffersRegistryImpl;
import net.minecraft.client.renderer.RenderType;

/**
 * Register a render buffer for a {@link RenderType} that is added in {@link net.minecraft.client.renderer.RenderBuffers#RenderBuffers(int)}.
 */
public interface RenderBuffersRegistry {
    /**
     * The singleton instance of the decorator registry.
     * Use this instance to call the methods in this interface.
     */
    RenderBuffersRegistry INSTANCE = new RenderBuffersRegistryImpl();

    /**
     * Registers a render buffer for the specified render type.
     *
     * @param renderType a render type of the render buffer
     */
    default void register(RenderType renderType) {
        this.register(renderType, new BufferBuilder(renderType.bufferSize()));
    }

    /**
     * Registers a render buffer for the specified render type.
     *
     * @param renderType   a render type of the render buffer
     * @param renderBuffer a render buffer to register
     */
    void register(RenderType renderType, BufferBuilder renderBuffer);
}
