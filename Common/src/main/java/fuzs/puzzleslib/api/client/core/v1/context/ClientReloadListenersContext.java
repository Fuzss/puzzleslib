package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.server.packs.resources.PreparableReloadListener;

/**
 * adds a listener to the client resource manager to reload at the end of all resources
 *
 * <p>(the resource manager uses a list for keeping track, so it's pretty safe to assume it'll load after vanilla,
 * Fabric has a very limited way of setting some sort of resource dependencies,
 * but they don't work for most stuff and Forge doesn't have them anyway)
 *
 * @deprecated migrate to {@link fuzs.puzzleslib.api.core.v1.context.AddReloadListenersContext}
 */
@Deprecated(forRemoval = true)
@FunctionalInterface
public interface ClientReloadListenersContext {

    /**
     * register a {@link PreparableReloadListener}
     *
     * @param id             id of this listener for identifying, only used on Fabric
     * @param reloadListener the reload-listener to add
     */
    void registerReloadListener(String id, PreparableReloadListener reloadListener);
}
