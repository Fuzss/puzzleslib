package fuzs.puzzleslib.config.core;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

/**
 * config value wrapper on Forge
 *
 * @param value     wrapped config value
 * @param <T>       config value data type
 */
public record ForgeConfigValueWrapper<T>(ForgeConfigSpec.ConfigValue<T> value) implements AbstractConfigValue<T> {

    @Override
    public List<String> getPath() {
        return this.value.getPath();
    }

    @Override
    public T get() {
        return this.value.get();
    }

    @Override
    public T getDefault() {
        return this.value.getDefault();
    }

    @Override
    public void save() {
        this.value.save();
    }

    @Override
    public void set(T value) {
        this.value.set(value);
    }

    @Override
    public void clearCache() {
        this.value.clearCache();
    }
}
