package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.api.init.v2.GameRulesFactory;
import fuzs.puzzleslib.api.init.v2.PotionBrewingRegistry;
import fuzs.puzzleslib.api.init.v2.RegistryManager;

import java.util.function.Supplier;

/**
 * all sorts of instance factories that need to be created on a per-mod basis
 */
public interface CommonFactories {
    /**
     * instance of the common factories SPI
     */
    CommonFactories INSTANCE = ServiceProviderHelper.loadServiceProvider(CommonFactories.class);

    /**
     * this is very much unnecessary as the method is only ever called from loader specific code anyway which does have
     * access to the specific mod constructor, but for simplifying things and having this method in a common place we keep it here
     *
     * @param modId                the mod id for registering events on Forge to the correct mod event bus
     * @param modConstructor       the main mod instance for mod setup
     * @param contentRegistrations specific content this mod uses that needs to be additionally registered
     */
    void constructMod(String modId, Supplier<ModConstructor> modConstructor, ContentRegistrationFlags... contentRegistrations);

    /**
     * creates a new network handler
     *
     * @param modId                         id for channel name
     * @param clientAcceptsVanillaOrMissing are servers without this mod or vanilla compatible
     * @param serverAcceptsVanillaOrMissing are clients without this mod or vanilla compatible
     * @return mod specific network handler with configured channel
     */
    NetworkHandlerV2 networkingV2(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing);

    /**
     * creates a new network handler
     *
     * @param modId id for channel name
     * @return mod specific network handler with default channel
     */
    NetworkHandlerV3.Builder networkingV3(String modId);

    /**
     * internal factory for client proxy, use {@link Proxy#INSTANCE}
     *
     * @return provides the client proxy supplier
     */
    Supplier<Proxy> clientProxy();

    /**
     * internal factory for server proxy, use {@link Proxy#INSTANCE}
     *
     * @return provides the server proxy supplier
     */
    Supplier<Proxy> serverProxy();

    /**
     * register a new client config to the holder/builder
     * <p>just an overload for {@link ConfigHolder.Builder#clientConfig} that also creates a new builder instance
     *
     * @param clazz        client config main class
     * @param clientConfig client config factory
     * @param <T>          client config type
     * @return the builder we are working with
     */
    <T extends ConfigCore> ConfigHolder.Builder clientConfig(Class<T> clazz, Supplier<T> clientConfig);

    /**
     * register a new client config to the holder/builder
     * <p>just an overload for {@link ConfigHolder.Builder#commonConfig} that also creates a new builder instance
     *
     * @param clazz        common config main class
     * @param commonConfig common config factory
     * @param <T>          common config type
     * @return the builder we are working with
     */
    <T extends ConfigCore> ConfigHolder.Builder commonConfig(Class<T> clazz, Supplier<T> commonConfig);

    /**
     * register a new client config to the holder/builder
     * <p>just an overload for {@link ConfigHolder.Builder#serverConfig} that also creates a new builder instance
     *
     * @param clazz        server config main class
     * @param serverConfig server config factory
     * @param <T>          server config type
     * @return the builder we are working with
     */
    <T extends ConfigCore> ConfigHolder.Builder serverConfig(Class<T> clazz, Supplier<T> serverConfig);

    RegistryManager registration(String modId, boolean deferred);

    CapabilityController capabilities(String modId);

    PotionBrewingRegistry getPotionBrewingRegistry();

    GameRulesFactory getGameRulesFactory();

    <T> EventInvoker<T> lookupEvent(Class<T> clazz);
}
