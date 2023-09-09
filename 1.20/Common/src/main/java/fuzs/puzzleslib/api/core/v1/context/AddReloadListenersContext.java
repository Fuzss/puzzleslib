package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

/**
 * Adds a listener to the client (resource packs) or server (data packs) resource manager to reload at the end of all resources.
 *
 * <p>The resource manager uses a list for keeping track, so it's pretty safe to assume it'll load after vanilla,
 * Fabric has a very limited way of setting some sort of resource dependencies,
 * but they don't work for most stuff and Forge doesn't have them anyway, so we skip that.
 */
@FunctionalInterface
public interface AddReloadListenersContext {

    /**
     * Register a {@link PreparableReloadListener}.
     *
     * @param id             id for this listener
     * @param reloadListener the reload listener to add
     */
    void registerReloadListener(String id, PreparableReloadListener reloadListener);

    /**
     * Register a {@link ResourceManagerReloadListener}.
     * <p>Exists as a dedicated overload to remind me this is a thing, and you do not need to always use the full-blown {@link PreparableReloadListener}.
     *
     * @param id             id for this listener
     * @param reloadListener the reload listener to add
     */
    default void registerReloadListener(String id, ResourceManagerReloadListener reloadListener) {
        this.registerReloadListener(id, (PreparableReloadListener) reloadListener);
    }

    /**
     * Register a {@link SimplePreparableReloadListener}.
     * <p>Exists as a dedicated overload to remind me this is a thing, and you do not need to always use the full-blown {@link PreparableReloadListener}.
     *
     * @param id             id for this listener
     * @param reloadListener the reload listener to add
     */
    default <T> void registerReloadListener(String id, SimplePreparableReloadListener<T> reloadListener) {
        this.registerReloadListener(id, (PreparableReloadListener) reloadListener);
    }
}
