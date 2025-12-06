package fuzs.puzzleslib.api.config.v3;

import fuzs.puzzleslib.impl.config.ConfigHolderRegistry;
import fuzs.puzzleslib.impl.core.ModContext;

import java.nio.file.Paths;
import java.util.function.UnaryOperator;

/**
 * a config holder for holding mod configs there are three different kinds depending on where the data shall be used:
 * CLIENT, COMMON, SERVER this implementation is not limited to three held configs though, as many configs as desired
 * may be added (file names must be different!) instead of retrieving configs via mod config type they are stored by
 * class type
 */
public interface ConfigHolder {

    /**
     * Creates a new builder for registering configs to this holder instance.
     *
     * @param modId id for registration and config name
     * @return new builder instance
     */
    static Builder builder(String modId) {
        return ModContext.get(modId).getConfigHolder();
    }

    /**
     * @param clazz config clazz type
     * @param <T>   config type
     * @return the config holder
     */
    <T extends ConfigCore> ConfigDataHolder<T> getHolder(Class<T> clazz);

    /**
     * @param clazz config clazz type
     * @param <T>   config type
     * @return the actual config
     */
    default <T extends ConfigCore> T get(Class<T> clazz) {
        return this.getHolder(clazz).getConfig();
    }

    /**
     * @return config name
     */
    static UnaryOperator<String> getSimpleNameFactory() {
        return (String modId) -> modId + ".toml";
    }

    /**
     * @param configType type of config
     * @return config name
     */
    static UnaryOperator<String> getDefaultNameFactory(String configType) {
        return (String modId) -> modId + "-" + configType + ".toml";
    }

    /**
     * @param configType config file name
     * @param directory  directory to move config to
     * @return path to config in dir
     */
    static UnaryOperator<String> getDirectoryNameFactory(String configType, String directory) {
        return (String modId) -> Paths.get(directory, getDefaultNameFactory(configType).apply(modId)).toString();
    }

    /**
     * builder interface for registering configs, not needed anymore after initial registration is complete, but no new
     * instance is created, so we only store the super type {@link ConfigHolder}
     */
    interface Builder extends ConfigHolderRegistry {

        /**
         * register a new client config to the holder/builder
         *
         * @param <T>   client config type
         * @param clazz client config main class
         * @return the builder we are working with
         */
        <T extends ConfigCore> Builder client(Class<T> clazz);

        /**
         * register a new client config to the holder/builder
         *
         * @param <T>   common config type
         * @param clazz common config main class
         * @return the builder we are working with
         */
        <T extends ConfigCore> Builder common(Class<T> clazz);

        /**
         * register a new client config to the holder/builder
         *
         * @param <T>   server config type
         * @param clazz server config main class
         * @return the builder we are working with
         */
        <T extends ConfigCore> Builder server(Class<T> clazz);

        /**
         * this sets the file name on {@link ConfigDataHolder}, it's only used for storing, since actually it's only
         * ever need in this class when calling {@link #build}
         * <p>
         * by default this is set to {@link #getDefaultNameFactory}, otherwise {@link #getSimpleNameFactory()} and
         * {@link #getDirectoryNameFactory(String, String)} exist for convenience
         *
         * @param clazz           config main class
         * @param fileNameFactory file name operator, passed in is the modId
         * @param <T>             config type
         * @return the builder we are working with
         */
        <T extends ConfigCore> Builder setFileName(Class<T> clazz, UnaryOperator<String> fileNameFactory);
    }
}
