package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.fabric.impl.capability.FabricCapabilityController;
import fuzs.puzzleslib.fabric.impl.config.FabricConfigHolderImpl;
import fuzs.puzzleslib.fabric.impl.init.FabricRegistryManager;
import fuzs.puzzleslib.impl.core.ModContext;

public final class FabricModContext extends ModContext {

    public FabricModContext(String modId) {
        super(modId);
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
