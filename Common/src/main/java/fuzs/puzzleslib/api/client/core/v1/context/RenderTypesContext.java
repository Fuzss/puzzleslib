package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * Register custom {@link RenderType}s for blocks and fluids.
 *
 * @param <T> object type supported by provider, either {@link Block} or {@link Fluid}
 */
@FunctionalInterface
public interface RenderTypesContext<T> {

    /**
     * Register a <code>renderType</code> for an <code>object</code>
     *
     * @param renderType the {@link RenderType} for <code>object</code>
     * @param object     object type supporting render type, either {@link Block} or {@link Fluid}
     * @param objects    more object types supporting render type, either {@link Block} or {@link Fluid}
     */
    @SuppressWarnings("unchecked")
    void registerRenderType(RenderType renderType, T object, T... objects);
}
