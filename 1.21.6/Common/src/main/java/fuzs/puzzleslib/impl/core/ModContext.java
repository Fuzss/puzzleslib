package fuzs.puzzleslib.impl.core;

import com.google.common.collect.Queues;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.utility.Buildable;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class ModContext {
    private static final Map<String, ModContext> MOD_CONTEXTS = new ConcurrentHashMap<>();

    protected final String modId;
    private final Queue<Buildable> buildables = Queues.newConcurrentLinkedQueue();
    @Nullable
    protected RegistryManager registryManager;
    /**
     * {@code true} by default for dedicated servers, is handled properly on clients.
     */
    private boolean presentServerside = ModLoaderEnvironment.INSTANCE.isServer();

    protected ModContext(String modId) {
        this.modId = modId;
    }

    public static void onRegisterConfigurationTasks(MinecraftServer minecraftServer, ServerConfigurationPacketListenerImpl listener, Consumer<ConfigurationTask> configurationTaskConsumer) {
        configurationTaskConsumer.accept(new ModListConfigurationTask(listener));
    }

    public static void onLoadComplete() {
        for (ModContext context : MOD_CONTEXTS.values()) {
            if (!context.buildables.isEmpty()) {
                throw new IllegalStateException("Mod context for %s has %s remaining buildable(s)".formatted(context.modId,
                        context.buildables.size()));
            }
        }
    }

    public static void clearPresentServerside() {
        for (ModContext context : MOD_CONTEXTS.values()) {
            context.presentServerside = false;
        }
    }

    public static Collection<String> getModList() {
        return Collections.unmodifiableSet(MOD_CONTEXTS.keySet());
    }

    public static ModContext get(String modId) {
        return MOD_CONTEXTS.computeIfAbsent(modId, ProxyImpl.get()::getModContext);
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

    public abstract ConfigHolder.Builder getConfigHolder();

    public abstract RegistryManager getRegistryManager();

    protected <T extends Buildable> T addBuildable(T buildable) {
        Objects.requireNonNull(buildable, "buildable is null");
        this.buildables.offer(buildable);
        return buildable;
    }

    public final void buildAll() {
        while (!this.buildables.isEmpty()) {
            this.buildables.poll().build();
        }
    }
}
