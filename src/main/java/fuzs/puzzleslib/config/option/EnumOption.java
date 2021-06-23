package fuzs.puzzleslib.config.option;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.BiFunction;

public class EnumOption<T extends Enum<T>> extends ConfigOption<T> {

    private final Class<?> clazz;
    private final T[] acceptableValues;

    EnumOption(ForgeConfigSpec.ConfigValue<T> value, ModConfig.Type type, EnumOptionBuilder<T> builder) {

        super(value, type, builder);
        this.clazz = builder.clazz;
        this.acceptableValues = builder.acceptableValues;
    }

    public Class<?> getDeclaringClass() {

        return this.clazz;
    }

    public T[] getAcceptableValues() {

        return this.acceptableValues;
    }

    public static class EnumOptionBuilder<T extends Enum<T>> extends ConfigOptionBuilder<T> {

        private final Class<?> clazz;
        private T[] acceptableValues;

        @SuppressWarnings("unchecked")
        EnumOptionBuilder(String name, T defaultValue) {

            super(name, defaultValue);
            this.clazz = defaultValue.getDeclaringClass();
            this.acceptableValues = (T[]) this.clazz.getEnumConstants();
        }

        @SafeVarargs
        public final EnumOptionBuilder<T> acceptable(T... acceptableValues) {

            this.acceptableValues = acceptableValues;
            return this;
        }

        @Override
        BiFunction<ForgeConfigSpec.ConfigValue<T>, ModConfig.Type, ConfigOption<T>> getFactory() {

            return (value, type) -> new EnumOption<>(value, type, this);
        }

        @Override
        ForgeConfigSpec.ConfigValue<T> getConfigValue(ForgeConfigSpec.Builder builder) {

            return builder.defineEnum(this.name, this.defaultValue, this.acceptableValues);
        }

    }

}
