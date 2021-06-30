package fuzs.puzzleslib.config.option;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.BiFunction;

public class GenericOption<T> extends ConfigOption<T> {

    GenericOption(ForgeConfigSpec.ConfigValue<T> value, ModConfig.Type type, GenericOptionBuilder<T> builder) {

        super(value, type, builder);
    }

    public static class GenericOptionBuilder<T> extends ConfigOptionBuilder<T> {

        GenericOptionBuilder(String name, T defaultValue) {

            super(name, defaultValue);
        }

        @Override
        BiFunction<ForgeConfigSpec.ConfigValue<T>, ModConfig.Type, ConfigOption<T>> getFactory() {

            return (value, type) -> new GenericOption<>(value, type, this);
        }

        @Override
        ForgeConfigSpec.ConfigValue<T> getConfigValue(ForgeConfigSpec.Builder builder) {

            return builder.define(this.name, this.defaultValue);
        }

    }

}
