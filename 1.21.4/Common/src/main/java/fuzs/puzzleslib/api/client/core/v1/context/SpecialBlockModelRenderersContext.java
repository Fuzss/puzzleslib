package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.level.block.Block;

@FunctionalInterface
public interface SpecialBlockModelRenderersContext {

    /**
     * Register a custom unbaked special model renderer implementation to be used for statically rendered blocks, such
     * as blocks visually appearing in minecarts and held by enderman.
     *
     * @param block                the block requiring a special model renderer
     * @param specialModelRenderer the unbaked special model renderer implementation
     */
    void registerSpecialBlockModelRenderer(Block block, SpecialModelRenderer.Unbaked specialModelRenderer);
}
