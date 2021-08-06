package fuzs.puzzleslib.config.option;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public abstract class NumberOption<T extends Number> extends SimpleConfigOption<T> {

    private final T minValue;
    private final T maxValue;

    NumberOption(ForgeConfigSpec.ConfigValue<T> value, ModConfig.Type type, NumberOptionBuilder<T> builder) {

        super(value, type, builder);
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
    }

    public T getMin() {

        return this.minValue;
    }

    public T getMax() {

        return this.maxValue;
    }

    public static abstract class NumberOptionBuilder<T extends Number> extends SimpleConfigOption.SimpleConfigOptionBuilder<T> {

        T minValue;
        T maxValue;

        NumberOptionBuilder(OptionBuilder previous, String name, T defaultValue) {

            super(previous, name, defaultValue);
        }

        public NumberOptionBuilder<T> min(T minValue) {

            this.minValue = minValue;
            return this;
        }

        public NumberOptionBuilder<T> max(T maxValue) {

            this.maxValue = maxValue;
            return this;
        }

        public NumberOptionBuilder<T> range(T minValue, T maxValue) {

            this.minValue = minValue;
            this.maxValue = maxValue;
            return this;
        }

    }

}
