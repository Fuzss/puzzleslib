package fuzs.puzzleslib.core;

import fuzs.puzzleslib.config.AbstractConfig;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.config.ConfigHolderV2;
import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.puzzleslib.proxy.Proxy;
import fuzs.puzzleslib.registry.RegistryManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * all sorts of instance factories that need to be created on a per-mod basis
 */
public interface CommonFactories {

    /**
     * this is very much unnecessary as the method is only ever called from loader specific code anyway which does have
     * access to the specific mod constructor, but for simplifying things and having this method in a common place we keep it here
     *
     * @return  provides a consumer for loading a mod being provided the base class
     */
    @Deprecated(forRemoval = true)
    default Consumer<ModConstructor> modConstructor() {
        return this.modConstructor("");
    }

    /**
     * this is very much unnecessary as the method is only ever called from loader specific code anyway which does have
     * access to the specific mod constructor, but for simplifying things and having this method in a common place we keep it here
     *
     * @param modId the mod id for registering events on Forge to the correct mod event bus
     * @return  provides a consumer for loading a mod being provided the base class
     */
    Consumer<ModConstructor> modConstructor(String modId);

    /**
     * creates a new network handler
     *
     * @param modId id for channel name
     * @return mod specific network handler with default channel
     */
    default NetworkHandler network(String modId) {
        return this.network(modId, false, false);
    }

    /**
     * creates a new network handler
     *
     * @param modId id for channel name
     * @param clientAcceptsVanillaOrMissing are servers without this mod or vanilla compatible
     * @param serverAcceptsVanillaOrMissing are clients without this mod or vanilla compatible
     * @return mod specific network handler with configured channel
     */
    NetworkHandler network(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing);

    /**
     * internal factory for client proxy, use {@link Proxy#INSTANCE}
     *
     * @return provides the client proxy supplier
     */
    @ApiStatus.Internal
    Supplier<Proxy> clientProxy();

    /**
     * internal factory for server proxy, use {@link Proxy#INSTANCE}
     *
     * @return provides the server proxy supplier
     */
    @ApiStatus.Internal
    Supplier<Proxy> serverProxy();

    /**
     * @param client client config factory
     * @param server server config factory
     * @param <C> client config type
     * @param <S> server config type
     * @return a config holder which only holds both a client config and a server config
     *
     * @deprecated use the new config implementation, see {@link #clientConfig}, {@link #commonConfig} and {@link #serverConfig}
     */
    @Deprecated(forRemoval = true)
    <C extends AbstractConfig, S extends AbstractConfig> ConfigHolder<C, S> config(Supplier<C> client, Supplier<S> server);

    /**
     * @param client client config factory
     * @param <C> client config type
     * @return a config holder which only holds a client config
     *
     * @deprecated use the new config implementation, see {@link #clientConfig}, {@link #commonConfig} and {@link #serverConfig}
     */
    @Deprecated(forRemoval = true)
    <C extends AbstractConfig> ConfigHolder<C, AbstractConfig> clientConfig(Supplier<C> client);

    /**
     * @param server server config factory
     * @param <S> server config type
     * @return a config holder which only holds a server config
     *
     * @deprecated use the new config implementation, see {@link #clientConfig}, {@link #commonConfig} and {@link #serverConfig}
     */
    @Deprecated(forRemoval = true)
    <S extends AbstractConfig> ConfigHolder<AbstractConfig, S> serverConfig(Supplier<S> server);

    /**
     * register a new client config to the holder/builder
     * just an overload for {@link ConfigHolderV2.Builder#clientConfig} that also creates a new builder instance
     *
     * @param clazz         client config main class
     * @param clientConfig  client config factory
     * @param <T>           client config type
     * @return              the builder we are working with
     */
    @Deprecated(forRemoval = true)
    default <T extends AbstractConfig> ConfigHolderV2.Builder client(Class<T> clazz, Supplier<T> clientConfig) {
        return this.clientConfig(clazz, clientConfig);
    }

    /**
     * register a new client config to the holder/builder
     * just an overload for {@link ConfigHolderV2.Builder#commonConfig} that also creates a new builder instance
     *
     * @param clazz         common config main class
     * @param commonConfig  common config factory
     * @param <T>           common config type
     * @return              the builder we are working with
     */
    @Deprecated(forRemoval = true)
    default <T extends AbstractConfig> ConfigHolderV2.Builder common(Class<T> clazz, Supplier<T> commonConfig) {
        return this.commonConfig(clazz, commonConfig);
    }

    /**
     * register a new client config to the holder/builder
     * just an overload for {@link ConfigHolderV2.Builder#serverConfig} that also creates a new builder instance
     *
     * @param clazz         server config main class
     * @param serverConfig  server config factory
     * @param <T>           server config type
     * @return              the builder we are working with
     */
    @Deprecated(forRemoval = true)
    default <T extends AbstractConfig> ConfigHolderV2.Builder server(Class<T> clazz, Supplier<T> serverConfig) {
        return this.serverConfig(clazz, serverConfig);
    }

    /**
     * register a new client config to the holder/builder
     * just an overload for {@link ConfigHolderV2.Builder#clientConfig} that also creates a new builder instance
     *
     * @param clazz         client config main class
     * @param clientConfig  client config factory
     * @param <T>           client config type
     * @return              the builder we are working with
     */
    <T extends AbstractConfig> ConfigHolderV2.Builder clientConfig(Class<T> clazz, Supplier<T> clientConfig);

    /**
     * register a new client config to the holder/builder
     * just an overload for {@link ConfigHolderV2.Builder#commonConfig} that also creates a new builder instance
     *
     * @param clazz         common config main class
     * @param commonConfig  common config factory
     * @param <T>           common config type
     * @return              the builder we are working with
     */
    <T extends AbstractConfig> ConfigHolderV2.Builder commonConfig(Class<T> clazz, Supplier<T> commonConfig);

    /**
     * register a new client config to the holder/builder
     * just an overload for {@link ConfigHolderV2.Builder#serverConfig} that also creates a new builder instance
     *
     * @param clazz         server config main class
     * @param serverConfig  server config factory
     * @param <T>           server config type
     * @return              the builder we are working with
     */
    <T extends AbstractConfig> ConfigHolderV2.Builder serverConfig(Class<T> clazz, Supplier<T> serverConfig);

    /**
     * creates a new registry manager for <code>namespace</code> or returns an existing one
     *
     * @param modId namespace used for registration
     * @return new mod specific registry manager
     */
    RegistryManager registration(String modId);

    /**
     * @param modId namespace used for registration
     * @return new mod specific registry manager
     *
     * @deprecated use {@link #registration} instead
     */
    @Deprecated(forRemoval = true)
    default RegistryManager registry(String modId) {
        return this.registration(modId);
    }
}
