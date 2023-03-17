package fuzs.puzzleslib.api.client.core.v1.contexts;

import fuzs.puzzleslib.api.client.registration.v1.SkullRenderersFactory;

/**
 * register models for custom {@link net.minecraft.world.level.block.SkullBlock.Type} implementations
 */
public interface SkullRenderersContext {

    /**
     * add models for specific skull types
     *
     * @param factory factory for the model(s)
     */
    void registerSkullRenderer(SkullRenderersFactory factory);
}
