package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.fabric.impl.capability.FabricCapabilityController;
import fuzs.puzzleslib.fabric.impl.config.FabricConfigHolderImpl;
import fuzs.puzzleslib.fabric.impl.init.FabricRegistryManager;
import fuzs.puzzleslib.fabric.impl.network.NetworkHandlerFabricV2;
import fuzs.puzzleslib.fabric.impl.network.NetworkHandlerFabricV3;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.resources.ResourceLocation;

public final class FabricModContext extends ModContext {

    public FabricModContext(String modId) {
        super(modId);
    }

    @Override
    public NetworkHandlerV2 getNetworkHandlerV2(ResourceLocation channelName, boolean optional) {
        return new NetworkHandlerFabricV2(channelName);
    }

    @Override
    public NetworkHandlerV3.Builder getNetworkHandlerV3(ResourceLocation channelName) {
        return this.addBuildable(new NetworkHandlerFabricV3(channelName));
    }

    @Override
    public ConfigHolder.Builder getConfigHolder() {
        return this.addBuildable(new FabricConfigHolderImpl(this.modId));
    }

    @Override
    public RegistryManager getRegistryManager() {
        if (this.registryManager == null) {
            this.registryManager = new FabricRegistryManager(this.modId);
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
