package fuzs.puzzleslib.core;

import fuzs.puzzleslib.capability.CapabilityController;
import fuzs.puzzleslib.capability.FabricCapabilityController;
import fuzs.puzzleslib.config.ConfigCore;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.config.FabricConfigHolderImpl;
import fuzs.puzzleslib.impl.init.PotionBrewingRegistryImplFabric;
import fuzs.puzzleslib.init.FabricRegistryManager;
import fuzs.puzzleslib.init.RegistryManager;
import fuzs.puzzleslib.network.FabricNetworkHandler;
import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.puzzleslib.network.v2.NetworkHandlerRegistry;
import fuzs.puzzleslib.proxy.FabricClientProxy;
import fuzs.puzzleslib.proxy.FabricServerProxy;
import fuzs.puzzleslib.proxy.Proxy;
import fuzs.puzzleslib.init.PotionBrewingRegistry;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * factories for various utilities on Fabric
 */
public final class FabricFactories implements CommonFactories {

    @Override
    public Consumer<ModConstructor> modConstructor(String modId) {
        return constructor -> FabricModConstructor.construct(modId, constructor);
    }

    @Override
    public NetworkHandler network(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return FabricNetworkHandler.of(modId);
    }

    @Override
    public NetworkHandlerRegistry.Builder networkV2(String modId) {
        return new fuzs.puzzleslib.network.v2.FabricNetworkHandler.FabricBuilder(modId);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Supplier<Proxy> clientProxy() {
        return () -> new FabricClientProxy();
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Supplier<Proxy> serverProxy() {
        return () -> new FabricServerProxy();
    }

    @Override
    public <T extends ConfigCore> ConfigHolder.Builder clientConfig(Class<T> clazz, Supplier<T> clientConfig) {
        return new FabricConfigHolderImpl().clientConfig(clazz, clientConfig);
    }

    @Override
    public <T extends ConfigCore> ConfigHolder.Builder commonConfig(Class<T> clazz, Supplier<T> commonConfig) {
        return new FabricConfigHolderImpl().commonConfig(clazz, commonConfig);
    }

    @Override
    public <T extends ConfigCore> ConfigHolder.Builder serverConfig(Class<T> clazz, Supplier<T> serverConfig) {
        return new FabricConfigHolderImpl().serverConfig(clazz, serverConfig);
    }

    @Override
    public RegistryManager registration(String modId, boolean deferred) {
        return FabricRegistryManager.of(modId, deferred);
    }

    @Override
    public CapabilityController capabilities(String modId) {
        return FabricCapabilityController.of(modId);
    }

    @Override
    public PotionBrewingRegistry getPotionBrewingRegistry() {
        return new PotionBrewingRegistryImplFabric();
    }
}
