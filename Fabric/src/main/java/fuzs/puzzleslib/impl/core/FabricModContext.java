package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.impl.capability.v2.FabricCapabilityController;
import fuzs.puzzleslib.impl.config.FabricConfigHolderImpl;
import fuzs.puzzleslib.impl.init.FabricRegistryManagerV2;
import fuzs.puzzleslib.impl.init.FabricRegistryManagerV3;
import fuzs.puzzleslib.impl.network.NetworkHandlerFabricV2;
import fuzs.puzzleslib.impl.network.NetworkHandlerFabricV3;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class FabricModContext extends ModContext {

    public FabricModContext(String modId) {
        super(modId);
    }

    @Override
    public NetworkHandlerV2 getNetworkHandlerV2(@Nullable String context, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        if (context == null) context = String.valueOf(this.networkHandlers.incrementAndGet());
        return new NetworkHandlerFabricV2(new ResourceLocation(this.modId, context));
    }

    @Override
    public NetworkHandlerV3.Builder getNetworkHandlerV3$Builder(@Nullable String context) {
        if (context == null) context = String.valueOf(this.networkHandlers.incrementAndGet());
        return this.addBuildable(new NetworkHandlerFabricV3(new ResourceLocation(this.modId, context)));
    }

    @Override
    public ConfigHolder.Builder getConfigHolder$Builder() {
        return this.addBuildable(new FabricConfigHolderImpl(this.modId));
    }

    @Override
    public RegistryManager getRegistryManagerV2() {
        if (this.registryManagerV2 == null) {
            this.registryManagerV2 = new FabricRegistryManagerV2(this.modId);
        }
        return this.registryManagerV2;
    }

    @Override
    public fuzs.puzzleslib.api.init.v3.RegistryManager getRegistryManagerV3() {
        if (this.registryManagerV3 == null) {
            this.registryManagerV3 = new FabricRegistryManagerV3(this.modId);
        }
        return this.registryManagerV3;
    }

    @Override
    public CapabilityController getCapabilityControllerV2() {
        if (this.capabilityControllerV2 == null) {
            this.capabilityControllerV2 = new FabricCapabilityController(this.modId);
        }
        return this.capabilityControllerV2;
    }

    @Override
    public fuzs.puzzleslib.api.capability.v3.CapabilityController getCapabilityControllerV3() {
        if (this.capabilityControllerV3 == null) {
            this.capabilityControllerV3 = new fuzs.puzzleslib.impl.capability.v3.FabricCapabilityController(this.modId);
        }
        return this.capabilityControllerV3;
    }
}
