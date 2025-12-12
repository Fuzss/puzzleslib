package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.context.DataPackReloadListenersContext;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.SortedReloadListenerEvent;
import net.neoforged.neoforge.resource.VanillaServerListeners;

import java.util.Map;
import java.util.Objects;

public record DataPackReloadListenersContextNeoForgeImpl(AddServerReloadListenersEvent event) implements DataPackReloadListenersContext {
    private static final Map<Identifier, Identifier> VANILLA_SERVER_RELOAD_LISTENERS = ImmutableMap.<Identifier, Identifier>builder()
            .put(RECIPES, VanillaServerListeners.RECIPES)
            .put(FUNCTIONS, VanillaServerListeners.FUNCTIONS)
            .put(ADVANCEMENTS, VanillaServerListeners.ADVANCEMENTS)
            .build();

    @Override
    public void registerReloadListener(Identifier identifier, PreparableReloadListenerFactory reloadListenerFactory) {
        Objects.requireNonNull(identifier, "id is null");
        Objects.requireNonNull(reloadListenerFactory, "reload listener factory is null");
        PreparableReloadListener reloadListener = reloadListenerFactory.apply(this.event.getServerResources(),
                this.event.getServerResources().getRegistryLookup());
        this.event.addListener(identifier, reloadListener);
    }

    @Override
    public void registerReloadListener(Identifier identifier, Identifier otherResourceLocation, PreparableReloadListenerFactory reloadListenerFactory) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(otherResourceLocation, "other identifier is null");
        Objects.requireNonNull(reloadListenerFactory, "reload listener factory is null");
        PreparableReloadListener reloadListener = reloadListenerFactory.apply(this.event.getServerResources(),
                this.event.getServerResources().getRegistryLookup());
        registerReloadListener(this.event,
                identifier,
                otherResourceLocation,
                reloadListener,
                VANILLA_SERVER_RELOAD_LISTENERS);
    }

    public static void registerReloadListener(SortedReloadListenerEvent event, Identifier identifier, Identifier otherResourceLocation, PreparableReloadListener reloadListener, Map<Identifier, Identifier> vanillaReloadListeners) {
        if (vanillaReloadListeners.containsKey(identifier)) {
            event.addListener(otherResourceLocation, reloadListener);
            event.addDependency(vanillaReloadListeners.get(identifier), otherResourceLocation);
        } else if (vanillaReloadListeners.containsKey(otherResourceLocation)) {
            event.addListener(identifier, reloadListener);
            event.addDependency(identifier, vanillaReloadListeners.get(otherResourceLocation));
        } else {
            throw new RuntimeException("Unknown reload listeners: " + identifier + ", " + otherResourceLocation);
        }
    }
}
