package fuzs.puzzleslib.common.impl.core;

import fuzs.puzzleslib.common.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.common.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.common.impl.config.ConfigHolderImpl;
import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.common.impl.init.RegistryManagerImpl;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class ModContext {
    private static final Map<String, ModContext> MOD_CONTEXTS = new ConcurrentHashMap<>();

    private final String modId;
    private final CustomPacketPayload.Type<BrandPayload> payloadType;
    @Nullable
    private ConfigHolderImpl configHolder;
    @Nullable
    private RegistryManagerImpl registryManager;

    public ModContext(String modId) {
        this.modId = modId;
        this.payloadType = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(modId, "handshake"));
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

    protected abstract void setupHandshakePayload(String modId, CustomPacketPayload.Type<BrandPayload> payloadType);

    public final boolean isPresentServerside() {
        return this.isPresentServerside(this.payloadType);
    }

    protected abstract boolean isPresentServerside(CustomPacketPayload.Type<BrandPayload> payloadType);

    public final boolean isPresentClientside(ServerPlayer serverPlayer) {
        return this.isPresentClientside(this.payloadType, serverPlayer);
    }

    protected abstract boolean isPresentClientside(CustomPacketPayload.Type<BrandPayload> payloadType, ServerPlayer serverPlayer);

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

        this.setupHandshakePayload(this.modId, this.payloadType);
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
