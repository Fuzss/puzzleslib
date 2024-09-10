package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@FunctionalInterface
public interface AddDataPackReloadListenersCallback {
    EventInvoker<AddDataPackReloadListenersCallback> EVENT = EventInvoker.lookup(
            AddDataPackReloadListenersCallback.class);

    /**
     * Adds a listener to the server resource manager (for data packs) to reload at the end of all resources.
     * <p>
     * Note that the {@link RegistryAccess} instance is unable to create tags, instead use {@link HolderLookup.Provider}
     * which does not have full access to registries though.
     *
     * @param consumer registers a reload listener with an id for debugging, common listener types include:
     *                 <ul>
     *                 <li>{@link net.minecraft.server.packs.resources.PreparableReloadListener}</li>
     *                 <li>{@link net.minecraft.server.packs.resources.ResourceManagerReloadListener}</li>
     *                 <li>{@link net.minecraft.server.packs.resources.SimplePreparableReloadListener}</li>
     *                 </ul>
     */
    void onAddDataPackReloadListeners(BiConsumer<ResourceLocation, BiFunction<HolderLookup.Provider, RegistryAccess, PreparableReloadListener>> consumer);
}
