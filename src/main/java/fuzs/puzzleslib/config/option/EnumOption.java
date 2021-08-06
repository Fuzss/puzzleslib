package fuzs.puzzleslib.config.option;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Collection;

public class EnumOption<T extends Enum<T>> extends SimpleConfigOption<T> {

    private final Class<T> declaringClazz;
    private final Collection<T> acceptableValues;

    EnumOption(ForgeConfigSpec.ConfigValue<T> value, ModConfig.Type type, EnumOptionBuilder<T> builder) {

        super(value, type, builder);
        this.declaringClazz = builder.declaringClazz;
        this.acceptableValues = builder.acceptableValues;
    }

    public Class<T> getDeclaringClass() {

        return this.declaringClazz;
    }

    public Collection<T> getAcceptableValues() {

        return this.acceptableValues;
    }

    public static class EnumOptionBuilder<T extends Enum<T>> extends SimpleConfigOptionBuilder<T> {

        private final Class<T> declaringClazz;
        private Collection<T> acceptableValues;

        EnumOptionBuilder(OptionBuilder previous, String name, T defaultValue) {

            super(previous, name, defaultValue);
            this.declaringClazz = defaultValue.getDeclaringClass();
            this.acceptableValues = Lists.newArrayList(this.declaringClazz.getEnumConstants());
        }

        @SafeVarargs
        public final EnumOptionBuilder<T> acceptable(T... acceptableValues) {

            return this.acceptable(Lists.newArrayList(acceptableValues));
        }

        public EnumOptionBuilder<T> acceptable(Collection<T> acceptableValues) {

            this.acceptableValues = acceptableValues;
            return this;
        }

        @Override
        SimpleConfigOption<T> createOption(ForgeConfigSpec.ConfigValue<T> value, ModConfig.Type type) {
            
            return new EnumOption<>(value, type, this);
        }

        @Override
        ForgeConfigSpec.ConfigValue<T> getConfigValue(ForgeConfigSpec.Builder builder) {

            return builder.defineEnum(this.name, this.defaultValue, this.acceptableValues);
        }

    }

}
