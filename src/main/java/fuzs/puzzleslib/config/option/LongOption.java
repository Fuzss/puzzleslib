package fuzs.puzzleslib.config.option;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class LongOption extends NumberOption<Long> {

    LongOption(ForgeConfigSpec.ConfigValue<Long> value, ModConfig.Type type, LongOptionBuilder builder) {

        super(value, type, builder);
    }

    public static class LongOptionBuilder extends NumberOptionBuilder<Long> {

        LongOptionBuilder(String name, Long defaultValue) {

            super(name, defaultValue);
            this.minValue = Long.MIN_VALUE;
            this.maxValue = Long.MAX_VALUE;
        }

        @Override
        SimpleConfigOption<Long> createOption(ForgeConfigSpec.ConfigValue<Long> value, ModConfig.Type type) {

            return new LongOption(value, type, this);
        }

        @Override
        ForgeConfigSpec.ConfigValue<Long> getConfigValue(ForgeConfigSpec.Builder builder) {

            return builder.defineInRange(this.name, this.defaultValue, this.minValue, this.maxValue);
        }

    }

}
