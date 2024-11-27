package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface AddDataPackReloadListenersCallback {
    EventInvoker<AddDataPackReloadListenersCallback> EVENT = EventInvoker.lookup(AddDataPackReloadListenersCallback.class);

    /**
     * Adds a listener to the server resource manager (for data packs) to reload at the end of all resources.
     *
     * @param fullRegistries        the registries from {@link ReloadableServerRegistries.LoadResult#layers()}
     * @param lookupWithUpdatedTags the registries from
     *                              {@link ReloadableServerRegistries.LoadResult#lookupWithUpdatedTags()}
     * @param consumer              registers a reload listener with an id for debugging, common listener types
     *                              include:
     *                              <ul>
     *                              <li>{@link net.minecraft.server.packs.resources.PreparableReloadListener}</li>
     *                              <li>{@link net.minecraft.server.packs.resources.ResourceManagerReloadListener}</li>
     *                              <li>{@link net.minecraft.server.packs.resources.SimplePreparableReloadListener}</li>
     *                              </ul>
     */
    void onAddDataPackReloadListeners(RegistryAccess fullRegistries, HolderLookup.Provider lookupWithUpdatedTags, BiConsumer<ResourceLocation, PreparableReloadListener> consumer);
}
