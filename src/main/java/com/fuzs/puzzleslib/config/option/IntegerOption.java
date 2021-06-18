package com.fuzs.puzzleslib.config.option;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.BiFunction;

public class IntegerOption extends NumberOption<Integer> {

    IntegerOption(ForgeConfigSpec.ConfigValue<Integer> value, ModConfig.Type type, IntegerOptionBuilder builder) {

        super(value, type, builder);
    }

    public static class IntegerOptionBuilder extends NumberOption.NumberOptionBuilder<Integer> {

        IntegerOptionBuilder(String name, Integer defaultValue) {

            super(name, defaultValue);
            this.minValue = Integer.MIN_VALUE;
            this.maxValue = Integer.MAX_VALUE;
        }

        @Override
        BiFunction<ForgeConfigSpec.ConfigValue<Integer>, ModConfig.Type, ConfigOption<Integer>> getFactory() {

            return (value, type) -> new IntegerOption(value, type, this);
        }

        @Override
        ForgeConfigSpec.ConfigValue<Integer> getConfigValue(ForgeConfigSpec.Builder builder) {

            return builder.defineInRange(this.name, this.defaultValue, this.minValue, this.maxValue);
        }

    }

}
