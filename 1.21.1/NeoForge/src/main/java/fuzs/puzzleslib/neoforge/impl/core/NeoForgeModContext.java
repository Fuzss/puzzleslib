package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.neoforge.impl.capability.NeoForgeCapabilityController;
import fuzs.puzzleslib.neoforge.impl.config.NeoForgeConfigHolderImpl;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgeRegistryManager;
import fuzs.puzzleslib.neoforge.impl.network.NeoForgeNetworkHandler;
import net.minecraft.resources.ResourceLocation;

public final class NeoForgeModContext extends ModContext {

    public NeoForgeModContext(String modId) {
        super(modId);
    }

    @Override
    public NetworkHandler.Builder getNetworkHandler(ResourceLocation channelName) {
        return this.addBuildable(new NeoForgeNetworkHandler(channelName));
    }

    @Override
    public ConfigHolder.Builder getConfigHolder() {
        return this.addBuildable(new NeoForgeConfigHolderImpl(this.modId));
    }

    @Override
    public RegistryManager getRegistryManager() {
        if (this.registryManager == null) {
            this.registryManager = new NeoForgeRegistryManager(this.modId);
        }
        return this.registryManager;
    }

    @Override
    public CapabilityController getCapabilityController() {
        if (this.capabilityController == null) {
            this.capabilityController = new NeoForgeCapabilityController(this.modId);
        }
        return this.capabilityController;
    }
}
