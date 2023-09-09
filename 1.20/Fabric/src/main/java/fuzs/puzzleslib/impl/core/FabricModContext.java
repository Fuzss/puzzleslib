package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.impl.capability.FabricCapabilityController;
import fuzs.puzzleslib.impl.config.FabricConfigHolderImpl;
import fuzs.puzzleslib.impl.init.FabricRegistryManagerV2;
import fuzs.puzzleslib.impl.init.FabricRegistryManagerV3;
import fuzs.puzzleslib.impl.network.NetworkHandlerFabricV2;
import fuzs.puzzleslib.impl.network.NetworkHandlerFabricV3;
import net.minecraft.resources.ResourceLocation;

public final class FabricModContext extends ModContext {

    public FabricModContext(String modId) {
        super(modId);
    }

    @Override
    public NetworkHandlerV2 getNetworkHandlerV2(int id, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        if (id == -1) id = this.networkHandlers.incrementAndGet();
        return new NetworkHandlerFabricV2(new ResourceLocation(this.modId, "play/" + id));
    }

    @Override
    public NetworkHandlerV3.Builder getNetworkHandlerV3$Builder(int id) {
        if (id == -1) id = this.networkHandlers.incrementAndGet();
        return this.addBuildable(new NetworkHandlerFabricV3(new ResourceLocation(this.modId, "play/" + id)));
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
    public CapabilityController getCapabilityController() {
        if (this.capabilityController == null) {
            this.capabilityController = new FabricCapabilityController(this.modId);
        }
        return this.capabilityController;
    }
}
