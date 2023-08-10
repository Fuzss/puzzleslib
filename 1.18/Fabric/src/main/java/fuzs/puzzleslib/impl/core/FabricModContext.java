package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.impl.capability.FabricCapabilityController;
import fuzs.puzzleslib.impl.config.FabricConfigHolderImpl;
import fuzs.puzzleslib.impl.init.FabricRegistryManager;
import fuzs.puzzleslib.impl.network.NetworkHandlerFabricV2;
import fuzs.puzzleslib.impl.network.NetworkHandlerFabricV3;

public final class FabricModContext extends ModContext {

    public FabricModContext(String modId) {
        super(modId);
    }

    @Override
    public NetworkHandlerV2 getNetworkHandlerV2(boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return new NetworkHandlerFabricV2(this.modId + "-" + this.networkHandlers.incrementAndGet());
    }

    @Override
    public NetworkHandlerV3.Builder getNetworkHandlerV3$Builder() {
        return this.addBuildable(new NetworkHandlerFabricV3(this.modId + "-" + this.networkHandlers.incrementAndGet()));
    }

    @Override
    public ConfigHolder.Builder getConfigHolder$Builder() {
        return this.addBuildable(new FabricConfigHolderImpl(this.modId));
    }

    @Override
    public RegistryManager getRegistryManager(boolean deferred) {
        if (this.registryManager == null) {
            this.registryManager = new FabricRegistryManager(this.modId, deferred);
        } else if (((FabricRegistryManager) this.registryManager).deferred != deferred) {
            throw new IllegalArgumentException("deferred setting does not match existing value");
        }
        return this.registryManager;
    }

    @Override
    public CapabilityController getCapabilityController() {
        if (this.capabilityController == null) {
            this.capabilityController = new FabricCapabilityController(this.modId);
        }
        return this.capabilityController;
    }
}
