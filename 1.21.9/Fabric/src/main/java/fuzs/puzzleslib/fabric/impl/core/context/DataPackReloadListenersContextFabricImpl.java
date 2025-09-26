package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.context.DataPackReloadListenersContext;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DataPackReloadListenersContextFabricImpl implements DataPackReloadListenersContext {
    private static final Map<ResourceLocation, ResourceLocation> VANILLA_SERVER_RELOAD_LISTENERS = ImmutableMap.<ResourceLocation, ResourceLocation>builder()
            .put(RECIPES, ResourceReloaderKeys.Server.RECIPES)
            .put(FUNCTIONS, ResourceReloaderKeys.Server.FUNCTIONS)
            .put(ADVANCEMENTS, ResourceReloaderKeys.Server.ADVANCEMENTS)
            .build();

    private final Set<Map.Entry<ResourceLocation, ResourceLocation>> reloadListenerOrderings = new HashSet<>();
    private final ReloadableServerResources serverResources;
    private final RegistryAccess registryAccess;

    public DataPackReloadListenersContextFabricImpl(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
        this.serverResources = serverResources;
        this.registryAccess = registryAccess;
    }

    @Override
    public void registerReloadListener(ResourceLocation resourceLocation, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(resourceLocation, "id is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        ResourceLoader.get(PackType.SERVER_DATA).registerReloader(resourceLocation, reloadListener);
    }

    @Override
    public void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(otherResourceLocation, "other resource location is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        Map<ResourceLocation, ResourceLocation> vanillaReloadListeners = VANILLA_SERVER_RELOAD_LISTENERS;
        PackType packType = PackType.SERVER_DATA;
        registerReloadListener(resourceLocation, otherResourceLocation, reloadListener, vanillaReloadListeners, packType);
    }

    private void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, PreparableReloadListener reloadListener, Map<ResourceLocation, ResourceLocation> vanillaReloadListeners, PackType packType) {
        if (vanillaReloadListeners.containsKey(resourceLocation)) {
            ResourceLoader.get(packType).registerReloader(otherResourceLocation, reloadListener);
            this.registerWithOrdering(vanillaReloadListeners.get(resourceLocation), otherResourceLocation);
        } else if (vanillaReloadListeners.containsKey(otherResourceLocation)) {
            ResourceLoader.get(packType).registerReloader(resourceLocation, reloadListener);
            this.registerWithOrdering(resourceLocation, vanillaReloadListeners.get(otherResourceLocation));
        } else {
            throw new RuntimeException("Unknown reload listeners: " + resourceLocation + ", " + otherResourceLocation);
        }
    }

    private void registerWithOrdering(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation) {
        if (this.reloadListenerOrderings.add(Map.entry(resourceLocation, otherResourceLocation))) {
            ResourceLoader.get(PackType.SERVER_DATA).addReloaderOrdering(resourceLocation, otherResourceLocation);
        }
    }

    @Override
    public ReloadableServerResources getServerResources() {
        return this.serverResources;
    }

    @Override
    public RegistryAccess getRegistryAccess() {
        return this.registryAccess;
    }
}
