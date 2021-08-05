package fuzs.puzzleslib.config.option;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public abstract class SimpleConfigOption<T> extends ConfigOption<T, T> {

    SimpleConfigOption(ForgeConfigSpec.ConfigValue<T> value, ModConfig.Type type, ConfigOptionBuilder<T, T> builder) {

        super(value, type, builder);
    }

    @Override
    protected T convertValue(T value) {

        return value;
    }

    public static abstract class SimpleConfigOptionBuilder<T> extends ConfigOption.ConfigOptionBuilder<T, T> {

        SimpleConfigOptionBuilder(String name, T defaultValue) {

            super(name, defaultValue);
        }

        abstract SimpleConfigOption<T> createOption(ForgeConfigSpec.ConfigValue<T> value, ModConfig.Type type);

    }

}
