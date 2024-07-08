package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface AddDataPackReloadListenersCallback {
    EventInvoker<AddDataPackReloadListenersCallback> EVENT = EventInvoker.lookup(AddDataPackReloadListenersCallback.class);

    /**
     * Adds a listener to the server resource manager (for data packs) to reload at the end of all resources.
     *
     * @param registries the registry lookup
     * @param consumer   registers a reload listener with an id for debugging, common listener types include:
     *                   <ul>
     *                   <li>{@link net.minecraft.server.packs.resources.PreparableReloadListener}</li>
     *                   <li>{@link net.minecraft.server.packs.resources.ResourceManagerReloadListener}</li>
     *                   <li>{@link net.minecraft.server.packs.resources.SimplePreparableReloadListener}</li>
     *                   </ul>
     */
    void onAddDataPackReloadListeners(HolderLookup.Provider registries, BiConsumer<ResourceLocation, PreparableReloadListener> consumer);
}
