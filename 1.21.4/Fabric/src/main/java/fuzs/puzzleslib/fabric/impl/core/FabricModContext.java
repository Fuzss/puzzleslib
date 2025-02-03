package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import fuzs.puzzleslib.fabric.impl.capability.FabricCapabilityController;
import fuzs.puzzleslib.fabric.impl.config.FabricConfigHolderImpl;
import fuzs.puzzleslib.fabric.impl.init.FabricRegistryManager;
import fuzs.puzzleslib.fabric.impl.network.FabricNetworkHandler;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.resources.ResourceLocation;

public final class FabricModContext extends ModContext {

    public FabricModContext(String modId) {
        super(modId);
    }

    @Override
    public NetworkHandler.Builder getNetworkHandler(ResourceLocation resourceLocation) {
        return this.addBuildable(new FabricNetworkHandler(resourceLocation));
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
