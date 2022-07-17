package fuzs.puzzleslib.core;

import fuzs.puzzleslib.config.AbstractConfig;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.puzzleslib.proxy.Proxy;
import fuzs.puzzleslib.registry.RegistryManager;

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
    Consumer<ModConstructor> modConstructor();

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
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    Supplier<Proxy> clientProxy();

    /**
     * internal factory for server proxy, use {@link Proxy#INSTANCE}
     *
     * @return provides the server proxy supplier
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    Supplier<Proxy> serverProxy();

    /**
     * @param client client config factory
     * @param server server config factory
     * @param <C> client config type
     * @param <S> server config type
     * @return a config holder which only holds both a client config and a server config
     */
    <C extends AbstractConfig, S extends AbstractConfig> ConfigHolder<C, S> config(Supplier<C> client, Supplier<S> server);

    /**
     * @param client client config factory
     * @param <C> client config type
     * @return a config holder which only holds a client config
     */
    <C extends AbstractConfig> ConfigHolder<C, AbstractConfig> clientConfig(Supplier<C> client);

    /**
     * @param server server config factory
     * @param <S> server config type
     * @return a config holder which only holds a server config
     */
    <S extends AbstractConfig> ConfigHolder<AbstractConfig, S> serverConfig(Supplier<S> server);

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
