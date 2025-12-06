package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.impl.init.RegistryManagerImpl;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class ModContext {
    private static final Map<String, ModContext> MOD_CONTEXTS = new ConcurrentHashMap<>();

    private final String modId;
    protected final CustomPacketPayload.Type<BrandPayload> payloadType;
    @Nullable
    private ConfigHolderImpl configHolder;
    @Nullable
    private RegistryManagerImpl registryManager;

    public ModContext(String modId) {
        this.modId = modId;
        this.payloadType = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(modId, "handshake"));
    }

    public static void forEach(Consumer<ModContext> modContextConsumer) {
        MOD_CONTEXTS.values().forEach(modContextConsumer);
    }

    public static Map<String, ModContext> getModContexts() {
        return Collections.unmodifiableMap(MOD_CONTEXTS);
    }

    public static ModContext get(String modId) {
        return MOD_CONTEXTS.computeIfAbsent(modId, ProxyImpl.get()::getModContext);
    }

    public abstract boolean isPresentServerside();

    public abstract boolean isPresentClientside(ServerPlayer serverPlayer);

    public final ConfigHolder.Builder getConfigHolder() {
        if (this.configHolder == null) {
            return this.configHolder = this.createConfigHolder(this.modId);
        } else {
            return this.configHolder;
        }
    }

    protected abstract ConfigHolderImpl createConfigHolder(String modId);

    public final RegistryManager getRegistryManager() {
        if (this.registryManager == null) {
            return this.registryManager = this.createRegistryManager(this.modId);
        } else {
            return this.registryManager;
        }
    }

    protected abstract RegistryManagerImpl createRegistryManager(String modId);

    public final void runBeforeConstruction() {
        if (this.configHolder != null) {
            this.configHolder.freeze();
        }
    }

    public final void runAfterConstruction() {
        if (this.configHolder != null) {
            this.configHolder.isFrozenOrThrow();
        }

        if (this.registryManager != null) {
            this.registryManager.freeze();
            this.registryManager.isFrozenOrThrow();
        }
    }
}
