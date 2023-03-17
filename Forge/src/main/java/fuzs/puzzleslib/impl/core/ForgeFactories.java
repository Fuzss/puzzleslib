package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.networking.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.networking.v3.NetworkHandlerV3;
import fuzs.puzzleslib.api.registration.v2.GameRulesFactory;
import fuzs.puzzleslib.api.registration.v2.PotionBrewingRegistry;
import fuzs.puzzleslib.api.registration.v2.RegistryManager;
import fuzs.puzzleslib.impl.capability.ForgeCapabilityController;
import fuzs.puzzleslib.impl.config.ForgeConfigHolderImpl;
import fuzs.puzzleslib.impl.networking.NetworkHandlerForgeV3;
import fuzs.puzzleslib.impl.registration.PotionBrewingRegistryForge;
import fuzs.puzzleslib.impl.networking.NetworkHandlerForgeV2;
import fuzs.puzzleslib.impl.registration.ForgeGameRulesFactory;
import fuzs.puzzleslib.impl.registration.ForgeRegistryManager;

import java.util.function.Supplier;

/**
 * factories for various utilities on Forge
 */
public final class ForgeFactories implements CommonFactories {

    @Override
    public void constructMod(String modId, Supplier<ModConstructor> modConstructor, ContentRegistrationFlags... contentRegistrations) {
        ForgeModConstructor.construct(modConstructor.get(), modId, contentRegistrations);
    }

    @Override
    public NetworkHandlerV2 networkingV2(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return NetworkHandlerForgeV2.of(modId, clientAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing);
    }

    @Override
    public NetworkHandlerV3.Builder networkingV3(String modId) {
        return new NetworkHandlerForgeV3.ForgeBuilderImpl(modId);
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
        return new PotionBrewingRegistryForge();
    }

    @Override
    public GameRulesFactory getGameRulesFactory() {
        return new ForgeGameRulesFactory();
    }
}
