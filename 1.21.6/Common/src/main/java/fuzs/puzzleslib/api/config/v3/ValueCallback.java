package fuzs.puzzleslib.api.config.v3;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Consumer;

/**
 * Callback for updating config values synced to fields.
 */
@FunctionalInterface
public interface ValueCallback {

    /**
     * Registers the combination of <code>entry</code> and <code>save</code> for receiving reloads.
     *
     * @param entry source config value object
     * @param save  action to perform when value changes (it is reloaded)
     * @param <S>   type for value
     * @param <V>   the original config value
     * @return <code>entry</code> for builder format
     */
    <S, V extends ModConfigSpec.ConfigValue<S>> V accept(V entry, Consumer<S> save);
}
