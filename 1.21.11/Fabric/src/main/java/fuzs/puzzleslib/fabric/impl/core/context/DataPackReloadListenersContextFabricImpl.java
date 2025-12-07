package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.context.DataPackReloadListenersContext;
import net.fabricmc.fabric.api.resource.v1.DataResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;

public final class DataPackReloadListenersContextFabricImpl implements DataPackReloadListenersContext {
    private static final Map<ResourceLocation, ResourceLocation> VANILLA_SERVER_RELOAD_LISTENERS = ImmutableMap.<ResourceLocation, ResourceLocation>builder()
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
    public void registerReloadListener(ResourceLocation resourceLocation, PreparableReloadListenerFactory reloadListenerFactory) {
        Objects.requireNonNull(resourceLocation, "id is null");
        Objects.requireNonNull(reloadListenerFactory, "reload listener factory is null");
        this.registerReloadListenerFactory(resourceLocation, reloadListenerFactory);
    }

    @Override
    public void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, PreparableReloadListenerFactory reloadListenerFactory) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(otherResourceLocation, "other resource location is null");
        Objects.requireNonNull(reloadListenerFactory, "reload listener factory is null");
        if (VANILLA_SERVER_RELOAD_LISTENERS.containsKey(resourceLocation)) {
            this.registerReloadListenerFactory(otherResourceLocation, reloadListenerFactory);
            DataResourceLoader.get()
                    .addReloaderOrdering(VANILLA_SERVER_RELOAD_LISTENERS.get(resourceLocation), otherResourceLocation);
        } else if (VANILLA_SERVER_RELOAD_LISTENERS.containsKey(otherResourceLocation)) {
            this.registerReloadListenerFactory(resourceLocation, reloadListenerFactory);
            DataResourceLoader.get()
                    .addReloaderOrdering(resourceLocation, VANILLA_SERVER_RELOAD_LISTENERS.get(otherResourceLocation));
        } else {
            throw new RuntimeException("Unknown reload listeners: " + resourceLocation + ", " + otherResourceLocation);
        }
    }

    private void registerReloadListenerFactory(ResourceLocation resourceLocation, PreparableReloadListenerFactory reloadListenerFactory) {
        DataResourceLoader.get().registerReloader(resourceLocation, (HolderLookup.Provider registries) -> {
            ReloadableServerResources reloadableServerResources = RELOADABLE_SERVER_RESOURCES_REFERENCE.get().get();
            Objects.requireNonNull(reloadableServerResources, "reloadable server resources is null");
            return reloadListenerFactory.apply(reloadableServerResources, registries);
        });
    }
}
