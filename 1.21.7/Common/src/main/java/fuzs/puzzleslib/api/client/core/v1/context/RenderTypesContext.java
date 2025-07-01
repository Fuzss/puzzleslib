package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * Register custom {@link ChunkSectionLayer ChunkSectionLayers} for blocks and fluids.
 *
 * @param <T> type supported by provider, either {@link Block} or {@link Fluid}
 */
public interface RenderTypesContext<T> {

    /**
     * Register a {@link ChunkSectionLayer}.
     *
     * @param object            the object
     * @param chunkSectionLayer the chunk section layer
     */
    void registerChunkRenderType(T object, ChunkSectionLayer chunkSectionLayer);

    /**
     * Allows for retrieving the {@link ChunkSectionLayer} that has been registered for an object.
     * <p>
     * When no render type is registered, {@link ChunkSectionLayer#SOLID} is returned.
     *
     * @param object the object
     * @return the chunk section layer
     */
    ChunkSectionLayer getChunkRenderType(T object);
}
