package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.neoforge.impl.capability.NeoForgeCapabilityController;
import fuzs.puzzleslib.neoforge.impl.config.NeoForgeConfigHolderImpl;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgeRegistryManager;
import fuzs.puzzleslib.neoforge.impl.network.NetworkHandlerNeoForgeV2;
import fuzs.puzzleslib.neoforge.impl.network.NetworkHandlerNeoForgeV3;
import net.minecraft.resources.ResourceLocation;

public final class NeoForgeModContext extends ModContext {

    public NeoForgeModContext(String modId) {
        super(modId);
    }

    @Override
    public NetworkHandlerV2 getNetworkHandlerV2(ResourceLocation channelName, boolean optional) {
        return new NetworkHandlerNeoForgeV2(channelName, optional);
    }

    @Override
    public NetworkHandlerV3.Builder getNetworkHandlerV3$Builder(ResourceLocation channelName) {
        return this.addBuildable(new NetworkHandlerNeoForgeV3(channelName));
    }

    @Override
    public ConfigHolder.Builder getConfigHolder$Builder() {
        return this.addBuildable(new NeoForgeConfigHolderImpl(this.modId));
    }

    @Override
    public fuzs.puzzleslib.api.init.v3.RegistryManager getRegistryManager() {
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
