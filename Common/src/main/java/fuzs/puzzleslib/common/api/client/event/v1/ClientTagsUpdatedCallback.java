package fuzs.puzzleslib.common.api.client.event.v1;

import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import net.minecraft.core.RegistryAccess;

@FunctionalInterface
public interface ClientTagsUpdatedCallback {
    EventInvoker<ClientTagsUpdatedCallback> EVENT = EventInvoker.lookup(ClientTagsUpdatedCallback.class);

    /**
     * Fires on the client when tags have been updated; useful for reloading data that depends on them.
     *
     * @param registries the dynamic registries
     */
    void onClientTagsUpdated(RegistryAccess registries);
}
