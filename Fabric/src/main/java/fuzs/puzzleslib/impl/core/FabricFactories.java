package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.init.v2.GameRulesFactory;
import fuzs.puzzleslib.api.init.v2.PotionBrewingRegistry;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.impl.capability.FabricCapabilityController;
import fuzs.puzzleslib.impl.config.FabricConfigHolderImpl;
import fuzs.puzzleslib.impl.event.FabricEventInvokerRegistryImpl;
import fuzs.puzzleslib.impl.init.FabricGameRulesFactory;
import fuzs.puzzleslib.impl.init.FabricRegistryManager;
import fuzs.puzzleslib.impl.init.PotionBrewingRegistryFabric;
import fuzs.puzzleslib.impl.network.NetworkHandlerFabricV2;
import fuzs.puzzleslib.impl.network.NetworkHandlerFabricV3;

import java.util.function.Supplier;

public final class FabricFactories implements CommonFactories {

    @Override
    public void constructMod(String modId, Supplier<ModConstructor> modConstructor, ContentRegistrationFlags... contentRegistrations) {
        FabricModConstructor.construct(modConstructor.get(), modId, contentRegistrations);
    }

    @Override
    public NetworkHandlerV2 networkingV2(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return NetworkHandlerFabricV2.of(modId);
    }

    @Override
    public NetworkHandlerV3.Builder networkingV3(String modId) {
        return new NetworkHandlerFabricV3.FabricBuilderImpl(modId);
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
        return new PotionBrewingRegistryFabric();
    }

    @Override
    public GameRulesFactory getGameRulesFactory() {
        return new FabricGameRulesFactory();
    }

    @Override
    public <T> EventInvoker<T> lookupEvent(Class<T> clazz) {
        return FabricEventInvokerRegistryImpl.INSTANCE.lookup(clazz);
    }
}
