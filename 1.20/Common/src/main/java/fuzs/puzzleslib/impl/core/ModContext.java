package fuzs.puzzleslib.impl.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.client.event.v1.ClientPlayerEvents;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.Buildable;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.BaseModConstructor;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerEvents;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class ModContext {
    private static final Map<String, ModContext> MOD_CONTEXTS = Maps.newConcurrentMap();

    static {
        LoadCompleteCallback.EVENT.register(() -> {
            for (ModContext context : MOD_CONTEXTS.values()) {
                if (!context.buildables.isEmpty())
                    throw new IllegalStateException("mod context for %s has un-built buildables: %s".formatted(context.modId, context.buildables));
                if (!context.clientModConstructors.isEmpty())
                    throw new IllegalStateException("mod context for %s has un-built client mod constructors: %s".formatted(context.modId, context.clientModConstructors));
            }
        });
        PlayerEvents.LOGGED_IN.register((ServerPlayer player) -> {
            PuzzlesLibMod.NETWORK.sendTo(player, new ClientboundModListMessage(MOD_CONTEXTS.keySet()));
        });
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            ClientPlayerEvents.LOGGED_IN.register((LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection) -> {
                for (ModContext context : MOD_CONTEXTS.values()) {
                    context.presentServerside = false;
                }
            });
        }
    }

    final AtomicInteger networkHandlers = new AtomicInteger();
    final String modId;
    private final Queue<Buildable> buildables = Queues.newConcurrentLinkedQueue();
    private final Map<ResourceLocation, Runnable> clientModConstructors = Maps.newConcurrentMap();
    private final Set<ResourceLocation> constructedPairings = Sets.newConcurrentHashSet();
    private final Set<ContentRegistrationFlags> handledFlags = EnumSet.noneOf(ContentRegistrationFlags.class);
    @Nullable RegistryManager registryManagerV2;
    @Nullable fuzs.puzzleslib.api.init.v3.RegistryManager registryManagerV3;
    @Nullable CapabilityController capabilityController;
    // true by default for dedicated servers, is reset on client when joining new world
    private boolean presentServerside = true;

    ModContext(String modId) {
        this.modId = modId;
    }

    public static ModContext get(String modId) {
        return MOD_CONTEXTS.computeIfAbsent(modId, CommonFactories.INSTANCE::getModContext);
    }

    public static Stream<CapabilityController> getCapabilityControllers() {
        return MOD_CONTEXTS.values().stream().map(context -> context.capabilityController).filter(Objects::nonNull);
    }

    public static ResourceLocation getPairingIdentifier(String modId, BaseModConstructor modConstructor) {
        ResourceLocation identifier = modConstructor.getPairingIdentifier();
        return identifier != null ? identifier : new ResourceLocation(modId, "main");
    }

    public static void acceptServersideMods(Collection<String> modList) {
        modList.stream().map(MOD_CONTEXTS::get).filter(Objects::nonNull).forEach(context -> context.presentServerside = true);
    }

    public static boolean isPresentServerside(String modId) {
        return MOD_CONTEXTS.containsKey(modId) && MOD_CONTEXTS.get(modId).presentServerside;
    }

    public abstract NetworkHandlerV2 getNetworkHandlerV2(int id, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing);

    public abstract NetworkHandlerV3.Builder getNetworkHandlerV3$Builder(int id);

    public abstract ConfigHolder.Builder getConfigHolder$Builder();

    public abstract RegistryManager getRegistryManagerV2();

    public abstract fuzs.puzzleslib.api.init.v3.RegistryManager getRegistryManagerV3();

    public abstract CapabilityController getCapabilityController();

    <T extends Buildable> T addBuildable(T buildable) {
        Objects.requireNonNull(buildable, "buildable is null");
        this.buildables.offer(buildable);
        return buildable;
    }

    public final void scheduleClientModConstruction(ResourceLocation identifier, Runnable runnable) {
        if (this.constructedPairings.contains(identifier)) {
            runnable.run();
        } else {
            this.clientModConstructors.put(identifier, runnable);
        }
    }

    public final void beforeModConstruction() {
        while (!this.buildables.isEmpty()) {
            this.buildables.poll().build();
        }
    }

    public final void afterModConstruction(ResourceLocation identifier) {
        this.constructedPairings.add(identifier);
        Runnable runnable = this.clientModConstructors.remove(identifier);
        if (runnable != null) runnable.run();
    }

    public final Set<ContentRegistrationFlags> getFlagsToHandle(Set<ContentRegistrationFlags> availableFlags) {
        return availableFlags.stream()
                .filter(Predicate.not(this.handledFlags::contains))
                .peek(this.handledFlags::add)
                .collect(ImmutableSet.toImmutableSet());
    }
}
