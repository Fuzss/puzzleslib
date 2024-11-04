package fuzs.puzzleslib.fabric.api.client.event.v1.registry;

import fuzs.puzzleslib.api.client.init.v1.SkullRenderersFactory;
import fuzs.puzzleslib.fabric.impl.client.event.SkullRendererRegistryImpl;

/**
 * This registry holds {@link SkullRenderersFactory}, which are added to the skull type models map on every resource reload in {@link net.minecraft.client.renderer.blockentity.SkullBlockRenderer#createSkullRenderers}
 */
public interface SkullRendererRegistry {
    /**
     * The singleton instance of the decorator registry.
     * Use this instance to call the methods in this interface.
     */
    SkullRendererRegistry INSTANCE = new SkullRendererRegistryImpl();

    /**
     * Register a {@link SkullRenderersFactory}
     *
     * @param factory the factory
     */
    void register(SkullRenderersFactory factory);
}
