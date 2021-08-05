package fuzs.puzzleslib.config.option;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class DoubleOption extends NumberOption<Double> {

    DoubleOption(ForgeConfigSpec.ConfigValue<Double> value, ModConfig.Type type, DoubleOptionBuilder builder) {

        super(value, type, builder);
    }

    public static class DoubleOptionBuilder extends NumberOptionBuilder<Double> {

        DoubleOptionBuilder(String name, Double defaultValue) {

            super(name, defaultValue);
            this.minValue = Double.MIN_VALUE;
            this.maxValue = Double.MAX_VALUE;
        }

        @Override
        SimpleConfigOption<Double> createOption(ForgeConfigSpec.ConfigValue<Double> value, ModConfig.Type type) {

            return new DoubleOption(value, type, this);
        }

        @Override
        ForgeConfigSpec.ConfigValue<Double> getConfigValue(ForgeConfigSpec.Builder builder) {

            return builder.defineInRange(this.name, this.defaultValue, this.minValue, this.maxValue);
        }

    }

}
