package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.context.DataPackReloadListenersContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.SortedReloadListenerEvent;
import net.neoforged.neoforge.resource.VanillaServerListeners;

import java.util.Map;
import java.util.Objects;

public record DataPackReloadListenersContextNeoForgeImpl(AddServerReloadListenersEvent event) implements DataPackReloadListenersContext {
    private static final Map<ResourceLocation, ResourceLocation> VANILLA_SERVER_RELOAD_LISTENERS = ImmutableMap.<ResourceLocation, ResourceLocation>builder()
            .put(RECIPES, VanillaServerListeners.RECIPES)
            .put(FUNCTIONS, VanillaServerListeners.FUNCTIONS)
            .put(ADVANCEMENTS, VanillaServerListeners.ADVANCEMENTS)
            .build();

    @Override
    public void registerReloadListener(ResourceLocation resourceLocation, PreparableReloadListenerFactory reloadListenerFactory) {
        Objects.requireNonNull(resourceLocation, "id is null");
        Objects.requireNonNull(reloadListenerFactory, "reload listener factory is null");
        PreparableReloadListener reloadListener = reloadListenerFactory.apply(this.event.getServerResources(),
                this.event.getServerResources().getRegistryLookup());
        this.event.addListener(resourceLocation, reloadListener);
    }

    @Override
    public void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, PreparableReloadListenerFactory reloadListenerFactory) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(otherResourceLocation, "other resource location is null");
        Objects.requireNonNull(reloadListenerFactory, "reload listener factory is null");
        PreparableReloadListener reloadListener = reloadListenerFactory.apply(this.event.getServerResources(),
                this.event.getServerResources().getRegistryLookup());
        registerReloadListener(this.event,
                resourceLocation,
                otherResourceLocation,
                reloadListener,
                VANILLA_SERVER_RELOAD_LISTENERS);
    }

    public static void registerReloadListener(SortedReloadListenerEvent event, ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, PreparableReloadListener reloadListener, Map<ResourceLocation, ResourceLocation> vanillaReloadListeners) {
        if (vanillaReloadListeners.containsKey(resourceLocation)) {
            event.addListener(otherResourceLocation, reloadListener);
            event.addDependency(vanillaReloadListeners.get(resourceLocation), otherResourceLocation);
        } else if (vanillaReloadListeners.containsKey(otherResourceLocation)) {
            event.addListener(resourceLocation, reloadListener);
            event.addDependency(resourceLocation, vanillaReloadListeners.get(otherResourceLocation));
        } else {
            throw new RuntimeException("Unknown reload listeners: " + resourceLocation + ", " + otherResourceLocation);
        }
    }
}
