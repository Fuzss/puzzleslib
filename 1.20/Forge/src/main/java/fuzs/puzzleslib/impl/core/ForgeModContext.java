package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.impl.capability.ForgeCapabilityController;
import fuzs.puzzleslib.impl.config.ForgeConfigHolderImpl;
import fuzs.puzzleslib.impl.init.ForgeRegistryManagerV2;
import fuzs.puzzleslib.impl.init.ForgeRegistryManagerV3;
import fuzs.puzzleslib.impl.network.NetworkHandlerForgeV2;
import fuzs.puzzleslib.impl.network.NetworkHandlerForgeV3;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class ForgeModContext extends ModContext {

    public ForgeModContext(String modId) {
        super(modId);
    }

    @Override
    public NetworkHandlerV2 getNetworkHandlerV2(@Nullable String context, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return new NetworkHandlerForgeV2(new ResourceLocation(this.modId, context == null ? "play" : context), clientAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing);
    }

    @Override
    public NetworkHandlerV3.Builder getNetworkHandlerV3$Builder(@Nullable String context) {
        return this.addBuildable(new NetworkHandlerForgeV3(new ResourceLocation(this.modId, context == null ? "play" : context)));
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
