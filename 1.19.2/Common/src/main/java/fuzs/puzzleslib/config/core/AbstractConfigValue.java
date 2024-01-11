package fuzs.puzzleslib.config.core;

import java.util.List;
import java.util.function.Supplier;

/**
 * abstract wrapper for ForgeConfigSpec.ConfigValue
 * {@link Supplier} is also implemented on ForgeConfigSpec.ConfigValue, so we do it as well
 *
 * @param <T> data type of this config value
 */
public interface AbstractConfigValue<T> extends Supplier<T> {

    /**
     * @return  path of this value in the config (as defined by categories)
     */
    List<String> getPath();

    /**
     * get the current config value, will throw an exception when invoked too early where the config has not been loaded yet
     *
     * @return  the config value
     */
    @Override
    T get();

    /**
     * @return  default value
     */
    T getDefault();

    /**
     * save the config value to disk
     */
    void save();

    /**
     * changes to config value on disk
     *
     * @param value     new value
     */
    void set(T value);

    /**
     * clear the config values cached value
     */
    void clearCache();
}
