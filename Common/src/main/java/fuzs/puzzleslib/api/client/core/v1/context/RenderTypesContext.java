package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * Register custom {@link RenderType RenderTypes} for blocks and fluids.
 *
 * @param <T> object type supported by provider, either {@link Block} or {@link Fluid}
 */
public interface RenderTypesContext<T> {

    /**
     * Register a <code>renderType</code> for an <code>object</code>
     *
     * @param renderType the {@link RenderType} for <code>object</code>
     * @param objects    object types supporting render type, either {@link Block} or {@link Fluid}
     */
    @SuppressWarnings("unchecked")
    void registerRenderType(RenderType renderType, T... objects);

    /**
     * Allows for retrieving the {@link RenderType} that has been registered for an object type.
     * <p>When not render type is registered {@link RenderType#solid()} is returned.
     *
     * @param object the object type to get the render type for
     * @return the render type
     */
    RenderType getRenderType(T object);
}
