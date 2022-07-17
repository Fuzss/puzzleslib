package fuzs.puzzleslib.core;

import fuzs.puzzleslib.config.AbstractConfig;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.config.FabricConfigHolderImpl;
import fuzs.puzzleslib.network.FabricNetworkHandler;
import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.puzzleslib.proxy.FabricClientProxy;
import fuzs.puzzleslib.proxy.FabricServerProxy;
import fuzs.puzzleslib.proxy.Proxy;
import fuzs.puzzleslib.registry.FabricRegistryManager;
import fuzs.puzzleslib.registry.RegistryManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * factories for various utilities on Fabric
 */
public class FabricFactories implements CommonFactories {

    @Override
    public Consumer<ModConstructor> modConstructor() {
        return FabricModConstructor::construct;
    }

    @Override
    public NetworkHandler network(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return FabricNetworkHandler.of(modId);
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
    public <C extends AbstractConfig, S extends AbstractConfig> ConfigHolder<C, S> config(Supplier<C> client, Supplier<S> server) {
        return new FabricConfigHolderImpl<>(client, server);
    }

    @Override
    public <C extends AbstractConfig> ConfigHolder<C, AbstractConfig> clientConfig(Supplier<C> client) {
        return new FabricConfigHolderImpl<>(client, () -> null);
    }

    @Override
    public <S extends AbstractConfig> ConfigHolder<AbstractConfig, S> serverConfig(Supplier<S> server) {
        return new FabricConfigHolderImpl<>(() -> null, server);
    }

    @Override
    public RegistryManager registration(String modId) {
        return FabricRegistryManager.of(modId);
    }
}
