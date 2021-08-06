package fuzs.puzzleslib.config.option;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class BooleanOption extends SimpleConfigOption<Boolean> {

    BooleanOption(ForgeConfigSpec.ConfigValue<Boolean> value, ModConfig.Type type, BooleanOptionBuilder builder) {

        super(value, type, builder);
    }

    public static class BooleanOptionBuilder extends SimpleConfigOptionBuilder<Boolean> {

        BooleanOptionBuilder(OptionBuilder previous, String name, Boolean defaultValue) {

            super(previous, name, defaultValue);
        }

        @Override
        SimpleConfigOption<Boolean> createOption(ForgeConfigSpec.ConfigValue<Boolean> value, ModConfig.Type type) {
            
            return new BooleanOption(value, type, this);
        }

        @Override
        ForgeConfigSpec.ConfigValue<Boolean> getConfigValue(ForgeConfigSpec.Builder builder) {

            return builder.define(this.name, this.defaultValue);
        }

    }

}
