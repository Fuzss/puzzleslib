package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

/**
 * Adds listeners to the client (resource packs) or server (data packs) resource manager to reload together with other
 * resources.
 */
public interface ReloadListenersContext {

    /**
     * Register a {@link PreparableReloadListener}.
     *
     * @param resourceLocation the reload listener resource location
     * @param reloadListener   the reload listener to add
     */
    void registerReloadListener(ResourceLocation resourceLocation, PreparableReloadListener reloadListener);

    /**
     * Register a {@link PreparableReloadListener}.
     *
     * @param resourceLocation the reload listener resource location
     * @param reloadListener   the reload listener to add
     */
    void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, PreparableReloadListener reloadListener);

    /**
     * Register a {@link ResourceManagerReloadListener}.
     *
     * @param resourceLocation the reload listener resource location
     * @param reloadListener   the reload listener to add
     */
    default void registerReloadListener(ResourceLocation resourceLocation, ResourceManagerReloadListener reloadListener) {
        this.registerReloadListener(resourceLocation, (PreparableReloadListener) reloadListener);
    }

    /**
     * Register a {@link ResourceManagerReloadListener}.
     *
     * @param resourceLocation      the reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param otherResourceLocation the other reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param reloadListener        the reload listener to add
     */
    default void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, ResourceManagerReloadListener reloadListener) {
        this.registerReloadListener(resourceLocation, otherResourceLocation, (PreparableReloadListener) reloadListener);
    }

    /**
     * Register a {@link SimplePreparableReloadListener}.
     *
     * @param resourceLocation      the reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param otherResourceLocation the other reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param reloadListener        the reload listener to add
     */
    default <T> void registerReloadListener(ResourceLocation resourceLocation, SimplePreparableReloadListener<T> reloadListener) {
        this.registerReloadListener(resourceLocation, (PreparableReloadListener) reloadListener);
    }

    /**
     * Register a {@link SimplePreparableReloadListener}.
     *
     * @param resourceLocation      the reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param otherResourceLocation the other reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param reloadListener        the reload listener to add
     */
    default <T> void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, SimplePreparableReloadListener<T> reloadListener) {
        this.registerReloadListener(resourceLocation, otherResourceLocation, (PreparableReloadListener) reloadListener);
    }
}
