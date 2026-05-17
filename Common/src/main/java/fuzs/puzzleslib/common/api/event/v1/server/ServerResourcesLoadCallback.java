package fuzs.puzzleslib.common.api.event.v1.server;

import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;

@FunctionalInterface
public interface ServerResourcesLoadCallback {
    EventInvoker<ServerResourcesLoadCallback> EVENT = EventInvoker.lookup(ServerResourcesLoadCallback.class);

    /**
     * Fires on the server when tags have been updated; useful for reloading data that depends on them.
     *
     * @param serverResources the reloadable server resources
     * @param registries      the dynamic registries
     */
    void onServerResourcesLoad(ReloadableServerResources serverResources, RegistryAccess registries);
}
