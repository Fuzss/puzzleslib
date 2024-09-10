package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.init.v1.SkullRenderersFactory;

/**
 * register models for custom {@link net.minecraft.world.level.block.SkullBlock.Type} implementations
 */
@FunctionalInterface
public interface SkullRenderersContext {

    /**
     * add models for specific skull types
     *
     * @param factory factory for the model(s)
     */
    void registerSkullRenderer(SkullRenderersFactory factory);
}
