package fuzs.puzzleslib.api.client.events.v2;

import fuzs.puzzleslib.api.client.registration.v1.SkullRenderersFactory;
import fuzs.puzzleslib.impl.client.events.SkullRenderersRegistryImpl;

/**
 * This registry holds {@link SkullRenderersFactory}, which are added to the skull type models map on every resource reload in {@link net.minecraft.client.renderer.blockentity.SkullBlockRenderer#createSkullRenderers}
 */
public interface SkullRenderersRegistry {
    /**
     * The singleton instance of the decorator registry.
     * Use this instance to call the methods in this interface.
     */
    SkullRenderersRegistry INSTANCE = new SkullRenderersRegistryImpl();

    /**
     * Register a {@link SkullRenderersFactory}
     *
     * @param factory the factory
     */
    void register(SkullRenderersFactory factory);
}
