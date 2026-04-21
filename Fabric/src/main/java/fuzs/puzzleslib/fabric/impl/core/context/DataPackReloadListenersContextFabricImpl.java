package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.context.DataPackReloadListenersContext;
import net.fabricmc.fabric.api.resource.v1.DataResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ReloadableServerResources;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;

public final class DataPackReloadListenersContextFabricImpl implements DataPackReloadListenersContext {
    private static final Map<Identifier, Identifier> VANILLA_SERVER_RELOAD_LISTENERS = ImmutableMap.<Identifier, Identifier>builder()
            .put(RECIPES, ResourceReloaderKeys.Server.RECIPES)
            .put(FUNCTIONS, ResourceReloaderKeys.Server.FUNCTIONS)
            .put(ADVANCEMENTS, ResourceReloaderKeys.Server.ADVANCEMENTS)
            .build();
    private static final ThreadLocal<WeakReference<ReloadableServerResources>> RELOADABLE_SERVER_RESOURCES_REFERENCE = ThreadLocal.withInitial(
            () -> new WeakReference<>(null));

    public static void setReloadableServerResources(ReloadableServerResources reloadableServerResources) {
        RELOADABLE_SERVER_RESOURCES_REFERENCE.set(new WeakReference<>(reloadableServerResources));
    }

    @Override
    public void registerReloadListener(Identifier identifier, PreparableReloadListenerFactory reloadListenerFactory) {
        Objects.requireNonNull(identifier, "id is null");
        Objects.requireNonNull(reloadListenerFactory, "reload listener factory is null");
        this.registerReloadListenerFactory(identifier, reloadListenerFactory);
    }

    @Override
    public void registerReloadListener(Identifier identifier, Identifier otherResourceLocation, PreparableReloadListenerFactory reloadListenerFactory) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(otherResourceLocation, "other identifier is null");
        Objects.requireNonNull(reloadListenerFactory, "reload listener factory is null");
        if (VANILLA_SERVER_RELOAD_LISTENERS.containsKey(identifier)) {
            this.registerReloadListenerFactory(otherResourceLocation, reloadListenerFactory);
            DataResourceLoader.get()
                    .addReloaderOrdering(VANILLA_SERVER_RELOAD_LISTENERS.get(identifier), otherResourceLocation);
        } else if (VANILLA_SERVER_RELOAD_LISTENERS.containsKey(otherResourceLocation)) {
            this.registerReloadListenerFactory(identifier, reloadListenerFactory);
            DataResourceLoader.get()
                    .addReloaderOrdering(identifier, VANILLA_SERVER_RELOAD_LISTENERS.get(otherResourceLocation));
        } else {
            throw new RuntimeException("Unknown reload listeners: " + identifier + ", " + otherResourceLocation);
        }
    }

    private void registerReloadListenerFactory(Identifier identifier, PreparableReloadListenerFactory reloadListenerFactory) {
        DataResourceLoader.get().registerReloader(identifier, (HolderLookup.Provider registries) -> {
            ReloadableServerResources reloadableServerResources = RELOADABLE_SERVER_RESOURCES_REFERENCE.get().get();
            Objects.requireNonNull(reloadableServerResources, "reloadable server resources is null");
            return reloadListenerFactory.apply(reloadableServerResources, registries);
        });
    }
}
