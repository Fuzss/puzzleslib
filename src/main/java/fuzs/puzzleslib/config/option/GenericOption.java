package fuzs.puzzleslib.config.option;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GenericOption<T> extends SimpleConfigOption<T> {

    private final Predicate<Object> validator;
    private final Collection<T> acceptableValues;

    GenericOption(ForgeConfigSpec.ConfigValue<T> value, ModConfig.Type type, GenericOptionBuilder<T> builder) {

        super(value, type, builder);
        this.validator = builder.validator;
        this.acceptableValues = builder.acceptableValues;
    }

    public boolean isRestricted() {

        return this.validator == null && this.acceptableValues == null;
    }

    @Nullable
    public Predicate<Object> getValidator() {

        return this.validator;
    }

    @Nullable
    public Collection<T> getAcceptableValues() {

        return this.acceptableValues;
    }

    public static class GenericOptionBuilder<T> extends SimpleConfigOptionBuilder<T> {

        private Predicate<Object> validator;
        private Collection<T> acceptableValues;

        GenericOptionBuilder(String name, T defaultValue) {

            super(name, defaultValue);
        }

        public GenericOptionBuilder<T> validate(Predicate<Object> validator) {

            this.validator = validator;
            return this;
        }

        public GenericOptionBuilder<T> acceptable(Collection<T> acceptableValues) {

            this.acceptableValues = acceptableValues;
            return this;
        }

        @Override
        SimpleConfigOption<T> createOption(ForgeConfigSpec.ConfigValue<T> value, ModConfig.Type type) {

            return new GenericOption<>(value, type, this);
        }

        @Override
        ForgeConfigSpec.ConfigValue<T> getConfigValue(ForgeConfigSpec.Builder builder) {

            if (this.acceptableValues != null && !this.acceptableValues.isEmpty()) {

                this.comment = ArrayUtils.addAll(this.comment, "Allowed Values: " + this.acceptableValues.stream().map(Objects::toString).collect(Collectors.joining(", ")));
                this.validator = this.validator.and(this.acceptableValues::contains);
            }

            if (this.validator != null) {

                return builder.define(this.name, this.defaultValue, this.validator);
            }

            return builder.define(this.name, this.defaultValue);
        }

    }

}
