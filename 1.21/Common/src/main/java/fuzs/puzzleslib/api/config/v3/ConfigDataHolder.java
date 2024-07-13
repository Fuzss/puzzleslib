package fuzs.puzzleslib.api.config.v3;

import java.util.function.Consumer;

/**
 * a pretty basic holder for a config, {@link #getConfig()} is all we want most of the time
 * @param <T> config type
 */
public interface ConfigDataHolder<T extends ConfigCore> {

    /**
     * @return config from this holder, possibly null
     */
    T getConfig();

    /**
     * @return is the config full loaded and ready to be used
     */
    boolean isAvailable();

    /**
     * @param callback add a callback for this config, like data that has to be processed after every reload
     */
    @Deprecated(forRemoval = true)
    default void accept(Runnable callback) {
        this.addCallback(callback);
    }

    /**
     * @param callback add a callback for this config, like data that has to be processed after every reload
     */
    default void addCallback(Runnable callback) {
        this.addCallback(config -> callback.run());
    }

    /**
     * @param callback add a callback for this config, like data that has to be processed after every reload
     */
    @Deprecated(forRemoval = true)
    default void accept(Consumer<T> callback) {
        this.addCallback(callback);
    }

    /**
     * @param callback add a callback for this config, like data that has to be processed after every reload
     */
    void addCallback(Consumer<T> callback);
}
