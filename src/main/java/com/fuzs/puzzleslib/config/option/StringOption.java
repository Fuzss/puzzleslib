package com.fuzs.puzzleslib.config.option;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.BiFunction;

public class StringOption extends ConfigOption<String> {

    StringOption(ForgeConfigSpec.ConfigValue<String> value, ModConfig.Type type, StringOptionBuilder builder) {

        super(value, type, builder);
    }

    public static class StringOptionBuilder extends ConfigOptionBuilder<String> {

        StringOptionBuilder(String name, String defaultValue) {

            super(name, defaultValue);
        }

        @Override
        BiFunction<ForgeConfigSpec.ConfigValue<String>, ModConfig.Type, ConfigOption<String>> getFactory() {

            return (value, type) -> new StringOption(value, type, this);
        }

        @Override
        ForgeConfigSpec.ConfigValue<String> getConfigValue(ForgeConfigSpec.Builder builder) {

            return builder.define(this.name, this.defaultValue);
        }

    }

}
