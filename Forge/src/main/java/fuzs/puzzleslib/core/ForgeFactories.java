package fuzs.puzzleslib.core;

import fuzs.puzzleslib.capability.CapabilityController;
import fuzs.puzzleslib.capability.ForgeCapabilityController;
import fuzs.puzzleslib.config.ConfigCore;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.config.ForgeConfigHolderImpl;
import fuzs.puzzleslib.impl.registration.PotionBrewingRegistryImplForge;
import fuzs.puzzleslib.init.ForgeRegistryManager;
import fuzs.puzzleslib.init.RegistryManager;
import fuzs.puzzleslib.network.ForgeNetworkHandler;
import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.puzzleslib.proxy.ForgeClientProxy;
import fuzs.puzzleslib.proxy.ForgeServerProxy;
import fuzs.puzzleslib.proxy.Proxy;
import fuzs.puzzleslib.init.PotionBrewingRegistry;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * factories for various utilities on Forge
 */
public final class ForgeFactories implements CommonFactories {

    @Override
    public Consumer<ModConstructor> modConstructor(String modId) {
        return constructor -> ForgeModConstructor.construct(modId, constructor);
    }

    @Override
    public NetworkHandler network(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return ForgeNetworkHandler.of(modId, clientAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing);
    }

    @Override
    public fuzs.puzzleslib.api.networking.v3.NetworkHandler.Builder networkV2(String modId) {
        return new fuzs.puzzleslib.impl.networking.ForgeNetworkHandler.ForgeBuilderImpl(modId);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Supplier<Proxy> clientProxy() {
        return () -> new ForgeClientProxy();
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Supplier<Proxy> serverProxy() {
        return () -> new ForgeServerProxy();
    }

    @Override
    public <T extends ConfigCore> ConfigHolder.Builder clientConfig(Class<T> clazz, Supplier<T> clientConfig) {
        return new ForgeConfigHolderImpl().clientConfig(clazz, clientConfig);
    }

    @Override
    public <T extends ConfigCore> ConfigHolder.Builder commonConfig(Class<T> clazz, Supplier<T> commonConfig) {
        return new ForgeConfigHolderImpl().commonConfig(clazz, commonConfig);
    }

    @Override
    public <T extends ConfigCore> ConfigHolder.Builder serverConfig(Class<T> clazz, Supplier<T> serverConfig) {
        return new ForgeConfigHolderImpl().serverConfig(clazz, serverConfig);
    }

    @Override
    public RegistryManager registration(String modId, boolean deferred) {
        return ForgeRegistryManager.of(modId);
    }

    @Override
    public CapabilityController capabilities(String modId) {
        return ForgeCapabilityController.of(modId);
    }

    @Override
    public PotionBrewingRegistry getPotionBrewingRegistry() {
        return new PotionBrewingRegistryImplForge();
    }
}
