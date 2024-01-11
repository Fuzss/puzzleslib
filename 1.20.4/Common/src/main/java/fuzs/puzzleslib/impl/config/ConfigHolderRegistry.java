package fuzs.puzzleslib.impl.config;

import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigDataHolder;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;

public interface ConfigHolderRegistry extends ConfigHolder {

    @Deprecated
    @Override
    <T extends ConfigCore> ConfigDataHolder<T> getHolder(Class<T> clazz);

    @Deprecated
    @Override
    default <T extends ConfigCore> T get(Class<T> clazz) {
        return ConfigHolder.super.get(clazz);
    }
}
