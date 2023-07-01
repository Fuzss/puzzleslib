package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.impl.capability.ForgeCapabilityController;
import fuzs.puzzleslib.impl.config.ForgeConfigHolderImpl;
import fuzs.puzzleslib.impl.init.ForgeRegistryManager;
import fuzs.puzzleslib.impl.network.NetworkHandlerForgeV2;
import fuzs.puzzleslib.impl.network.NetworkHandlerForgeV3;

public final class ForgeModContext extends ModContext {

    public ForgeModContext(String modId) {
        super(modId);
    }

    @Override
    public NetworkHandlerV2 getNetworkHandlerV2(boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        if (this.networkHandlerV2 == null) {
            this.networkHandlerV2 = new NetworkHandlerForgeV2(this.modId, clientAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing);
        } else if (clientAcceptsVanillaOrMissing != ((NetworkHandlerForgeV2) this.networkHandlerV2).clientAcceptsVanillaOrMissing) {
            throw new IllegalArgumentException("client accepts vanilla or missing setting does not match existing value");
        } else if (serverAcceptsVanillaOrMissing != ((NetworkHandlerForgeV2) this.networkHandlerV2).serverAcceptsVanillaOrMissing) {
            throw new IllegalArgumentException("server accepts vanilla or missing setting does not match existing value");
        }
        return this.networkHandlerV2;
    }

    @Override
    public NetworkHandlerV3.Builder getNetworkHandlerV3$Builder() {
        if (this.networkHandlerV3 == null) {
            this.networkHandlerV3 = this.addBuildable(new NetworkHandlerForgeV3(this.modId));
        }
        return this.networkHandlerV3;
    }

    @Override
    public ConfigHolder.Builder getConfigHolder$Builder() {
        if (this.configHolder == null) {
            this.configHolder = this.addBuildable(new ForgeConfigHolderImpl(this.modId));
        }
        return this.configHolder;
    }

    @Override
    public RegistryManager getRegistryManager(boolean deferred) {
        if (this.registryManager == null) {
            this.registryManager = new ForgeRegistryManager(this.modId);
        }
        return this.registryManager;
    }

    @Override
    public CapabilityController getCapabilityController() {
        if (this.capabilityController == null) {
            this.capabilityController = new ForgeCapabilityController(this.modId);
        }
        return this.capabilityController;
    }
}
