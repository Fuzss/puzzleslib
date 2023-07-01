package fuzs.puzzleslib.impl.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.Buildable;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.PairedModConstructor;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class ModContext {
    private static final Map<String, ModContext> MOD_CONTEXTS = Collections.synchronizedMap(Maps.newIdentityHashMap());

    final String modId;
    private final Queue<Buildable> buildables = Queues.newArrayDeque();
    private final Map<ResourceLocation, Runnable> clientModConstructors = Maps.newHashMap();
    private final Set<ResourceLocation> constructedPairings = Sets.newHashSet();
    private final Set<ContentRegistrationFlags> handledFlags = EnumSet.noneOf(ContentRegistrationFlags.class);
    @Nullable NetworkHandlerV2 networkHandlerV2;
    @Nullable NetworkHandlerV3.Builder networkHandlerV3;
    @Nullable ConfigHolder.Builder configHolder;
    @Nullable RegistryManager registryManager;
    @Nullable CapabilityController capabilityController;

    ModContext(String modId) {
        this.modId = modId;
    }

    public static ModContext get(String modId) {
        return MOD_CONTEXTS.computeIfAbsent(modId.intern(), CommonFactories.INSTANCE::getModContext);
    }

    public static Stream<CapabilityController> getCapabilityControllers() {
        return MOD_CONTEXTS.values().stream().map(context -> context.capabilityController).filter(Objects::nonNull);
    }

    public static void testAllBuilt() {
        for (ModContext context : MOD_CONTEXTS.values()) {
            if (!context.buildables.isEmpty()) throw new IllegalStateException("mod context for %s has un-built buildables".formatted(context.modId));
            if (!context.clientModConstructors.isEmpty()) throw new IllegalStateException("mod context for %s has un-built client mod constructors".formatted(context.modId));
        }
    }

    public abstract NetworkHandlerV2 getNetworkHandlerV2(boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing);

    public abstract NetworkHandlerV3.Builder getNetworkHandlerV3$Builder();

    public abstract ConfigHolder.Builder getConfigHolder$Builder();

    public abstract RegistryManager getRegistryManager(boolean deferred);

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

    public static ResourceLocation getPairingIdentifier(String modId, PairedModConstructor modConstructor) {
        ResourceLocation identifier = modConstructor.getPairingIdentifier();
        return identifier != null ? identifier : new ResourceLocation(modId, "main");
    }
}
