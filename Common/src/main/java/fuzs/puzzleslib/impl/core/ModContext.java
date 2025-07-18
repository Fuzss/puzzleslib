package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.utility.Buildable;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ModContext {
    private static final Map<String, ModContext> MOD_CONTEXTS = new ConcurrentHashMap<>();

    protected final String modId;
    protected final CustomPacketPayload.Type<BrandPayload> payloadType;
    private final Queue<Buildable> buildables = new ConcurrentLinkedQueue<>();
    @Nullable
    protected RegistryManager registryManager;

    protected ModContext(String modId) {
        this.modId = modId;
        this.payloadType = new CustomPacketPayload.Type<>(ResourceLocationHelper.fromNamespaceAndPath(modId,
                "handshake"));
    }

    public static void onLoadComplete() {
        for (ModContext context : MOD_CONTEXTS.values()) {
            if (!context.buildables.isEmpty()) {
                throw new IllegalStateException("Mod context for %s has %s remaining buildable(s)".formatted(context.modId,
                        context.buildables.size()));
            }
        }
    }

    public static Map<String, ModContext> getModContexts() {
        return Collections.unmodifiableMap(MOD_CONTEXTS);
    }

    public static ModContext get(String modId) {
        return MOD_CONTEXTS.computeIfAbsent(modId, ProxyImpl.get()::getModContext);
    }

    public abstract boolean isPresentServerside();

    public abstract boolean isPresentClientside(ServerPlayer serverPlayer);

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
