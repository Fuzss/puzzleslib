package fuzs.puzzleslib.config.option;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListOption<T> extends ConfigOption<List<T>, List<? extends T>> {

    private final boolean disallowEmpty;
    private final Collection<T> acceptableValues;

    ListOption(ForgeConfigSpec.ConfigValue<List<? extends T>> value, ModConfig.Type type, ListOptionBuilder<T> builder) {

        super(value, type, builder);
        this.disallowEmpty = builder.disallowEmpty;
        this.acceptableValues = builder.acceptableValues;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<T> convertValue(List<? extends T> value) {

        // not an issue as we only allow List<T> values in builder
        return (List<T>) value;
    }

    public boolean isAllowedEmpty() {

        return !this.disallowEmpty;
    }

    public Collection<T> getAcceptableValues() {

        return this.acceptableValues;
    }

    public static class ListOptionBuilder<T> extends ConfigOptionBuilder<List<T>, List<? extends T>> {

        private boolean disallowEmpty;
        private Collection<T> acceptableValues;

        ListOptionBuilder(String name, List<T> defaultValue) {

            // forge really has a problem with some list types, especially immutable list
            // the config manager thingy keeps thinking something is not correct and creates a new config all the time, filling up the config directory with backup config files
            // simply wrapping a list inside a new one seems to solve this
            super(name, new ArrayList<>(defaultValue));
        }

        public ListOptionBuilder<T> disallowEmpty() {

            this.disallowEmpty = true;
            return this;
        }

        public ListOptionBuilder<T> acceptable(Collection<T> acceptableValues) {

            this.acceptableValues = acceptableValues;
            return this;
        }

        @Override
        ConfigOption<List<T>, List<? extends T>> createOption(ForgeConfigSpec.ConfigValue<List<? extends T>> value, ModConfig.Type type) {
            
            return new ListOption<>(value, type, this);
        }

        @Override
        ForgeConfigSpec.ConfigValue<List<? extends T>> getConfigValue(ForgeConfigSpec.Builder builder) {

            assert !this.disallowEmpty || !this.defaultValue.isEmpty() : "Empty default list on non-empty ListOptionBuilder";
            if (this.disallowEmpty) {

                this.comment = ArrayUtils.addAll(this.comment, "This option is not allowed to be empty.");
            }

            if (this.acceptableValues != null && !this.acceptableValues.isEmpty()) {

                this.comment = ArrayUtils.addAll(this.comment, "Allowed Values: " + this.acceptableValues.stream().map(Objects::toString).collect(Collectors.joining(", ")));
                if (this.disallowEmpty) {

                    return builder.defineList(split(this.name), () -> this.defaultValue, this.acceptableValues::contains);
                }

                return builder.defineListAllowEmpty(split(this.name), () -> this.defaultValue, this.acceptableValues::contains);
            }

            return builder.define(this.name, this.defaultValue, o -> !this.disallowEmpty || !(o instanceof List<?>) || !((List<?>) o).isEmpty());
        }

    }

}
