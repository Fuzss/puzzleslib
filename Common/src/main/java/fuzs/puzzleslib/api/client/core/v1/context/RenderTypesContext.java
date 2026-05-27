package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.renderer.v1.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;

/**
 * Register custom {@link RenderType}s for blocks and fluids.
 *
 * @param <T> object type supported by provider, either {@link Block} or {@link Fluid}
 */
public interface RenderTypesContext<T> {

    /**
     * Register a {@link RenderType}.
     *
     * @param object     the object
     * @param renderType the render type
     */
    void registerRenderType(T object, RenderType renderType);

    /**
     * Register a {@link ChunkSectionLayer}.
     *
     * @param object            the object
     * @param chunkSectionLayer the chunk section layer
     */
    default void registerChunkRenderType(T object, ChunkSectionLayer chunkSectionLayer) {
        this.registerRenderType(object, chunkSectionLayer.renderType);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    default void registerRenderType(RenderType renderType, T... objects) {
        Objects.requireNonNull(objects, "objects is null");
        for (T object : objects) {
            this.registerRenderType(object, renderType);
        }
    }

    /**
     * Allows for retrieving the {@link RenderType} that has been registered for an object type.
     * <p>When not render type is registered {@link RenderType#solid()} is returned.
     *
     * @param object the object type to get the render type for
     * @return the render type
     */
    RenderType getRenderType(T object);
}
