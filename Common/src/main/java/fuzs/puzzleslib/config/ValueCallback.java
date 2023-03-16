package fuzs.puzzleslib.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Consumer;

/**
 * callback for updating config values synced to read-only fields
 */
@FunctionalInterface
public interface ValueCallback {

    /**
     * @param entry source config value object
     * @param save action to perform when value changes (is reloaded)
     * @return  <code>entry</code> for builder format
     * @param <S> type for value
     * @param <V> the original config value
     */
    <S, V extends ForgeConfigSpec.ConfigValue<S>> V accept(V entry, Consumer<S> save);
}
