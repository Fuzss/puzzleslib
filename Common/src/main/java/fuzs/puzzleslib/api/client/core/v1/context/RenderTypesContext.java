package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * Register custom {@link RenderType RenderTypes} for blocks and fluids.
 *
 * @param <T> type supported by provider, either {@link Block} or {@link Fluid}
 */
public interface RenderTypesContext<T> {

    /**
     * Register a {@link RenderType}.
     *
     * @param renderType the render type
     * @param objects    objects supporting render type
     */
    @SuppressWarnings("unchecked")
    void registerRenderType(RenderType renderType, T... objects);

    /**
     * Allows for retrieving the {@link RenderType} that has been registered for an object.
     * <p>
     * When no render type is registered, {@link RenderType#solid()} is returned.
     *
     * @param object the object to get the render type for
     * @return the render type
     */
    RenderType getRenderType(T object);
}
