package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.core.HolderLookup;

@FunctionalInterface
public interface TagsUpdatedCallback {
    EventInvoker<TagsUpdatedCallback> EVENT = EventInvoker.lookup(TagsUpdatedCallback.class);

    /**
     * An event that runs on servers and clients when tags have been updated, useful for reloading data that depends on
     * tags.
     *
     * @param registries access to dynamic registries
     * @param client     <code>true</code> when this update was triggered by the client by receiving the sync packet
     *                   containing updated tags from the server, otherwise <code>false</code> when triggered from the
     *                   server reloading tags provided by active data packs
     */
    void onTagsUpdated(HolderLookup.Provider registries, boolean client);
}
