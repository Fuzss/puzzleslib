package fuzs.puzzleslib.api.client.renderer.v1;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * A helper class containing render type related methods.
 */
public final class RenderTypeHelper {

    private RenderTypeHelper() {
        // NO-OP
    }

    /**
     * Allows for retrieving the {@link RenderType} that has been registered for a block.
     * <p>
     * When no render type is registered, {@link RenderType#solid()} is returned.
     *
     * @param block the block to get the render type for
     * @return the render type
     */
    public static RenderType getRenderType(Block block) {
        return ClientProxyImpl.get().getRenderType(block);
    }

    /**
     * Allows for retrieving the {@link RenderType} that has been registered for a fluid.
     * <p>
     * When no render type is registered, {@link RenderType#solid()} is returned.
     *
     * @param fluid the fluid to get the render type for
     * @return the render type
     */
    public static RenderType getRenderType(Fluid fluid) {
        return ItemBlockRenderTypes.getRenderLayer(fluid.defaultFluidState());
    }

    /**
     * Allows for registering a {@link RenderType} for a block.
     * <p>
     * When no render type is registered, {@link RenderType#solid()} is used.
     *
     * @param block      the block to register the render type for
     * @param renderType the render type
     */
    public static void registerRenderType(Block block, RenderType renderType) {
        ClientProxyImpl.get().registerRenderType(block, renderType);
    }

    /**
     * Allows for registering a {@link RenderType} for a fluid.
     * <p>
     * When no render type is registered, {@link RenderType#solid()} is used.
     *
     * @param fluid      the fluid to register the render type for
     * @param renderType the render type
     */
    public static void registerRenderType(Fluid fluid, RenderType renderType) {
        ClientProxyImpl.get().registerRenderType(fluid, renderType);
    }
}
