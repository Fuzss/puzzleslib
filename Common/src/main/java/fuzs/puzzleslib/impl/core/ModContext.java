package fuzs.puzzleslib.impl.core;

import com.google.common.collect.Queues;
import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.BaseModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.utility.Buildable;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerNetworkEvents;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ModContext {
    private static final Map<String, ModContext> MOD_CONTEXTS = new ConcurrentHashMap<>();

    protected final String modId;
    private final Queue<Buildable> buildables = Queues.newConcurrentLinkedQueue();
    private final Map<ResourceLocation, Runnable> clientModConstructors = new ConcurrentHashMap<>();
    private final Set<ResourceLocation> constructedPairings = ConcurrentHashMap.newKeySet();
    @Nullable
    protected RegistryManager registryManager;
    @Nullable
    protected CapabilityController capabilityController;
    // true by default for dedicated servers, is reset on client when joining new world
    private boolean presentServerside = true;

    protected ModContext(String modId) {
        this.modId = modId;
    }

    public static void registerEventHandlers() {
        LoadCompleteCallback.EVENT.register(() -> {
            for (ModContext context : MOD_CONTEXTS.values()) {
                if (!context.buildables.isEmpty()) {
                    throw new IllegalStateException("Mod context for %s has %s remaining buildable(s)".formatted(context.modId,
                            context.buildables.size()));
                }
                if (!context.clientModConstructors.isEmpty()) {
                    throw new IllegalStateException("Mod context for %s has remaining client mod constructor(s): %s".formatted(
                            context.modId,
                            context.clientModConstructors.keySet()));
                }
            }
        });
        PlayerNetworkEvents.LOGGED_IN.register((ServerPlayer serverPlayer) -> {
            PuzzlesLibMod.NETWORK.sendMessage(PlayerSet.ofPlayer(serverPlayer),
                    new ClientboundModListMessage(MOD_CONTEXTS.keySet()));
        });
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            ClientPlayerNetworkEvents.LOGGED_IN.register((LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection) -> {
                for (ModContext context : MOD_CONTEXTS.values()) {
                    context.presentServerside = false;
                }
            });
        }
    }

    public static ModContext get(String modId) {
        return MOD_CONTEXTS.computeIfAbsent(modId, CommonFactories.INSTANCE::getModContext);
    }

    public static ResourceLocation getPairingIdentifier(String modId, BaseModConstructor modConstructor) {
        ResourceLocation identifier = modConstructor.getPairingIdentifier();
        return identifier != null ? identifier : ResourceLocationHelper.fromNamespaceAndPath(modId, "main");
    }

    public static void acceptServersideMods(Collection<String> modList) {
        modList.stream()
                .map(MOD_CONTEXTS::get)
                .filter(Objects::nonNull)
                .forEach(context -> context.presentServerside = true);
    }

    public static boolean isPresentServerside(String modId) {
        return MOD_CONTEXTS.containsKey(modId) && MOD_CONTEXTS.get(modId).presentServerside;
    }

    public abstract NetworkHandler.Builder getNetworkHandler(ResourceLocation resourceLocation);

    public abstract ConfigHolder.Builder getConfigHolder();

    public abstract RegistryManager getRegistryManager();

    public abstract CapabilityController getCapabilityController();

    protected <T extends Buildable> T addBuildable(T buildable) {
        Objects.requireNonNull(buildable, "buildable is null");
        this.buildables.offer(buildable);
        return buildable;
    }

    public final void scheduleClientModConstruction(ResourceLocation resourceLocation, Runnable runnable) {
        if (this.constructedPairings.contains(resourceLocation)) {
            runnable.run();
        } else {
            this.clientModConstructors.put(resourceLocation, runnable);
        }
    }

    public final void beforeModConstruction() {
        while (!this.buildables.isEmpty()) {
            this.buildables.poll().build();
        }
    }

    public final void afterModConstruction(ResourceLocation resourceLocation) {
        this.constructedPairings.add(resourceLocation);
        Runnable runnable = this.clientModConstructors.remove(resourceLocation);
        if (runnable != null) {
            runnable.run();
        }
    }
}
