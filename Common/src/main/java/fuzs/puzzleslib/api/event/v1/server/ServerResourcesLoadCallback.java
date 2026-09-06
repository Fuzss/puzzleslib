package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.core.RegistryAccess;

@FunctionalInterface
public interface ServerResourcesLoadCallback {
    EventInvoker<ServerResourcesLoadCallback> EVENT = EventInvoker.lookup(ServerResourcesLoadCallback.class);

    /**
     * Fires on the server when tags have been updated; useful for reloading data that depends on them.
     *
     * @param registries the dynamic registries
     */
    void onServerResourcesLoad(RegistryAccess registries);
}
