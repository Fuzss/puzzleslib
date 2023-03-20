package fuzs.puzzleslib.impl.core;

import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.Buildable;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public abstract class ModContext {
    private static final Map<String, ModContext> CONTEXTS = new MapMaker().weakKeys().makeMap();

    final String modId;
    @Nullable NetworkHandlerV2 networkHandlerV2;
    @Nullable NetworkHandlerV3.Builder networkHandlerV3;
    @Nullable ConfigHolder.Builder configHolder;
    @Nullable RegistryManager registryManager;
    @Nullable CapabilityController capabilityController;
    private Set<Buildable> buildables = Sets.newHashSet();

    ModContext(String modId) {
        this.modId = modId;
    }

    public static ModContext get(String modId) {
        return CONTEXTS.computeIfAbsent(modId, CommonFactories.INSTANCE::getModContext);
    }

    public static Stream<CapabilityController> getCapabilityControllers() {
        return CONTEXTS.values().stream().map(context -> context.capabilityController).filter(Objects::nonNull);
    }

    public abstract NetworkHandlerV2 getNetworkHandlerV2(boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing);

    public abstract NetworkHandlerV3.Builder getNetworkHandlerV3$Builder();

    public abstract ConfigHolder.Builder getConfigHolder$Builder();

    public abstract RegistryManager getRegistryManager(boolean deferred);

    public abstract CapabilityController getCapabilityController();

    <T extends Buildable> T addBuildable(T buildable) {
        Objects.requireNonNull(buildable, "buildable is null");
        Objects.requireNonNull(this.buildables, "buildables have already executed");
        this.buildables.add(buildable);
        return buildable;
    }

    public final void executeBuildables() {
        this.buildables.forEach(Buildable::build);
        this.buildables = null;
    }
}
