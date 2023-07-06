package fuzs.puzzleslib.config;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * callback for updating config values synced to fields
 */
public interface ValueCallback {

    /**
     * @param entry source config value object
     * @param save action to perform when value changes (is reloaded)
     * @param <T> type for value
     */
    <T> void accept(Supplier<T> entry, Consumer<T> save);
}
