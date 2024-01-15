package fuzs.puzzleslib.forge.impl.core;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.forge.impl.capability.ForgeCapabilityController;
import fuzs.puzzleslib.forge.impl.config.ForgeConfigHolderImpl;
import fuzs.puzzleslib.forge.impl.init.ForgeRegistryManagerV2;
import fuzs.puzzleslib.forge.impl.init.ForgeRegistryManagerV3;
import fuzs.puzzleslib.forge.impl.network.NetworkHandlerForgeV2;
import fuzs.puzzleslib.forge.impl.network.NetworkHandlerForgeV3;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.resources.ResourceLocation;

public final class ForgeModContext extends ModContext {

    public ForgeModContext(String modId) {
        super(modId);
    }

    @Override
    public NetworkHandlerV2 getNetworkHandlerV2(ResourceLocation channelName, boolean optional) {
        return new NetworkHandlerForgeV2(channelName, optional);
    }

    @Override
    public NetworkHandlerV3.Builder getNetworkHandlerV3$Builder(ResourceLocation channelName) {
        return this.addBuildable(new NetworkHandlerForgeV3(channelName));
    }

    @Override
    public ConfigHolder.Builder getConfigHolder$Builder() {
        return this.addBuildable(new ForgeConfigHolderImpl(this.modId));
    }

    @Override
    public RegistryManager getRegistryManagerV2() {
        if (this.registryManagerV2 == null) {
            this.registryManagerV2 = new ForgeRegistryManagerV2(this.modId);
        }
        return this.registryManagerV2;
    }

    @Override
    public fuzs.puzzleslib.api.init.v3.RegistryManager getRegistryManagerV3() {
        if (this.registryManagerV3 == null) {
            this.registryManagerV3 = new ForgeRegistryManagerV3(this.modId);
        }
        return this.registryManagerV3;
    }

    @Override
    public CapabilityController getCapabilityController() {
        if (this.capabilityController == null) {
            this.capabilityController = new ForgeCapabilityController(this.modId);
        }
        return this.capabilityController;
    }
}
