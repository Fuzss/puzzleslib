package fuzs.puzzleslib.impl.config;

import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigDataHolder;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import org.jetbrains.annotations.ApiStatus;

public interface ConfigHolderRegistry extends ConfigHolder {

    @ApiStatus.Internal
    @Override
    <T extends ConfigCore> ConfigDataHolder<T> getHolder(Class<T> clazz);

    @ApiStatus.Internal
    @Override
    default <T extends ConfigCore> T get(Class<T> clazz) {
        return ConfigHolder.super.get(clazz);
    }
}
