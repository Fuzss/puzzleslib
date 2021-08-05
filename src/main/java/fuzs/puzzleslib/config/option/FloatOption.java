package fuzs.puzzleslib.config.option;

import fuzs.puzzleslib.util.PuzzlesUtil;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class FloatOption extends ConfigOption<Float, Double> {

    FloatOption(ForgeConfigSpec.ConfigValue<Double> value, ModConfig.Type type, FloatOptionBuilder builder) {

        super(value, type, builder);
    }

    @Override
    protected Float convertValue(Double value) {
        
        return value.floatValue();
    }

    public static class FloatOptionBuilder extends ConfigOptionBuilder<Float, Double> {

        private Float minValue;
        private Float maxValue;

        FloatOptionBuilder(String name, Float defaultValue) {

            super(name, defaultValue);
            this.minValue = Float.MIN_VALUE;
            this.maxValue = Float.MAX_VALUE;
        }

        public FloatOptionBuilder min(Float minValue) {

            this.minValue = minValue;
            return this;
        }

        public FloatOptionBuilder max(Float maxValue) {

            this.maxValue = maxValue;
            return this;
        }

        public FloatOptionBuilder range(Float minValue, Float maxValue) {

            this.minValue = minValue;
            this.maxValue = maxValue;
            return this;
        }

        @Override
        ConfigOption<Float, Double> createOption(ForgeConfigSpec.ConfigValue<Double> value, ModConfig.Type type) {
            
            return new FloatOption(value, type, this);
        }

        @Override
        ForgeConfigSpec.ConfigValue<Double> getConfigValue(ForgeConfigSpec.Builder builder) {

            // will generate weird values when not rounded
            return builder.defineInRange(this.name, PuzzlesUtil.round(this.defaultValue, 6), PuzzlesUtil.round(this.minValue, 6), PuzzlesUtil.round(this.maxValue, 6));
        }

    }

}
