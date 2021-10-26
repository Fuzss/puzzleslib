package fuzs.puzzleslib.config.v2;

import java.util.function.Supplier;

/**
 * a config holder holds two separate configs for both logical server and logical client
 * one or both types may not be present, depending on mod requirements and physical side
 * @param <C> client config type
 * @param <S> server config type
 */
public interface ConfigHolder<C extends AbstractConfig, S extends AbstractConfig> {

    /**
     * @return client config from this holder, possibly null
     */
    C client();

    /**
     * @return server config from this holder, possibly null
     */
    S server();

    /**
     * @param client client config factory
     * @param server server config factory
     * @param <C> client config type
     * @param <S> server config type
     * @return a config holder which only holds both a client config and a server config
     */
    static <C extends AbstractConfig, S extends AbstractConfig> ConfigHolderImpl<C, S> of(Supplier<C> client, Supplier<S> server) {
        return new ConfigHolderImpl<>(client, server);
    }

    /**
     * @param client client config factory
     * @param <C> client config type
     * @return a config holder which only holds a client config
     */
    static <C extends AbstractConfig> ConfigHolderImpl<C, AbstractConfig> ofClient(Supplier<C> client) {
        return new ConfigHolderImpl<>(client, () -> null);
    }

    /**
     * @param server server config factory
     * @param <S> server config type
     * @return a config holder which only holds a server config
     */
    static <S extends AbstractConfig> ConfigHolderImpl<AbstractConfig, S> ofServer(Supplier<S> server) {
        return new ConfigHolderImpl<>(() -> null, server);
    }
}
