package fuzs.puzzleslib.core;

import fuzs.puzzleslib.config.AbstractConfig;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.config.ForgeConfigHolderImpl;
import fuzs.puzzleslib.config.ConfigHolderV2;
import fuzs.puzzleslib.config.ForgeConfigHolderImplV2;
import fuzs.puzzleslib.network.ForgeNetworkHandler;
import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.puzzleslib.proxy.ForgeClientProxy;
import fuzs.puzzleslib.proxy.ForgeServerProxy;
import fuzs.puzzleslib.proxy.Proxy;
import fuzs.puzzleslib.registry.ForgeRegistryManager;
import fuzs.puzzleslib.registry.RegistryManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * factories for various utilities on Forge
 */
public class ForgeFactories implements CommonFactories {

    @Override
    public Consumer<ModConstructor> modConstructor(String modId) {
        return constructor -> ForgeModConstructor.construct(modId, constructor);
    }

    @Override
    public NetworkHandler network(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return ForgeNetworkHandler.of(modId, clientAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing);
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
    public <C extends AbstractConfig, S extends AbstractConfig> ConfigHolder<C, S> config(Supplier<C> client, Supplier<S> server) {
        return new ForgeConfigHolderImpl<>(client, server);
    }

    @Override
    public <C extends AbstractConfig> ConfigHolder<C, AbstractConfig> clientConfig(Supplier<C> client) {
        return new ForgeConfigHolderImpl<>(client, () -> null);
    }

    @Override
    public <S extends AbstractConfig> ConfigHolder<AbstractConfig, S> serverConfig(Supplier<S> server) {
        return new ForgeConfigHolderImpl<>(() -> null, server);
    }

    @Override
    public <T extends AbstractConfig> ConfigHolderV2.Builder client(Class<T> clazz, Supplier<T> clientConfig) {
        return new ForgeConfigHolderImplV2().client(clazz, clientConfig);
    }

    @Override
    public <T extends AbstractConfig> ConfigHolderV2.Builder common(Class<T> clazz, Supplier<T> commonConfig) {
        return new ForgeConfigHolderImplV2().common(clazz, commonConfig);
    }

    @Override
    public <T extends AbstractConfig> ConfigHolderV2.Builder server(Class<T> clazz, Supplier<T> serverConfig) {
        return new ForgeConfigHolderImplV2().server(clazz, serverConfig);
    }

    @Override
    public RegistryManager registration(String modId) {
        return ForgeRegistryManager.of(modId);
    }
}
