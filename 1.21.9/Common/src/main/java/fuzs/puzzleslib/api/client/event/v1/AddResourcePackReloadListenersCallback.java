package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface AddResourcePackReloadListenersCallback {
    EventInvoker<AddResourcePackReloadListenersCallback> EVENT = EventInvoker.lookup(
            AddResourcePackReloadListenersCallback.class);

    /**
     * Adds a listener to the client resource manager (for resource packs) to reload at the end of all resources.
     *
     * @param consumer registers a reload listener with an id for debugging, common listener types include:
     *                 <ul>
     *                 <li>{@link PreparableReloadListener}</li>
     *                 <li>{@link net.minecraft.server.packs.resources.ResourceManagerReloadListener}</li>
     *                 <li>{@link net.minecraft.server.packs.resources.SimplePreparableReloadListener}</li>
     *                 </ul>
     */
    void onAddResourcePackReloadListeners(BiConsumer<ResourceLocation, PreparableReloadListener> consumer);
}
