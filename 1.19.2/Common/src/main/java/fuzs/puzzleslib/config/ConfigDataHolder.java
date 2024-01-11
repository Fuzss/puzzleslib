package fuzs.puzzleslib.config;

import java.util.function.Consumer;

/**
 * a pretty basic holder for a config, {@link #config()} is all we want most of the time
 * @param <T> config type
 */
public interface ConfigDataHolder<T extends ConfigCore> {

    /**
     * @return config from this holder, possibly null
     */
    T config();

    /**
     * @return is the config full loaded and ready to be used
     */
    boolean isAvailable();

    /**
     * @param callback add a callback for this config, like data that has to be processed after every reload
     */
    default void accept(Runnable callback) {
        this.accept(config -> callback.run());
    }

    /**
     * @param callback add a callback for this config, like data that has to be processed after every reload
     */
    void accept(Consumer<T> callback);
}
