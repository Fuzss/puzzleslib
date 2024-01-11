package fuzs.puzzleslib.impl.core.resources;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.core.v1.resources.NamedReloadListener;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

public record ForwardingResourceManagerReloadListener(ResourceLocation identifier, Supplier<Collection<ResourceManagerReloadListener>> reloadListeners) implements ResourceManagerReloadListener, NamedReloadListener {

    public ForwardingResourceManagerReloadListener(ResourceLocation identifier, Supplier<Collection<ResourceManagerReloadListener>> reloadListeners) {
        this.identifier = identifier;
        this.reloadListeners = Suppliers.memoize(() -> ImmutableList.copyOf(reloadListeners.get()));
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Collection<ResourceManagerReloadListener> reloadListeners = this.reloadListeners.get();
        Objects.checkIndex(0, reloadListeners.size());
        for (ResourceManagerReloadListener reloadListener : reloadListeners) {
            try {
                reloadListener.onResourceManagerReload(resourceManager);
            } catch (Exception e) {
                PuzzlesLib.LOGGER.error("Unable to reload listener {}", reloadListener.getName(), e);
            }
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
