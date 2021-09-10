package fuzs.puzzleslib.config.option;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public abstract class CollectionOption<T, S extends Collection<T>> extends ConfigOption<S, List<? extends String>> {

    private final boolean disallowEmpty;
    private final Collection<T> acceptableValues;
    private final Function<String, T> nameToValue;

    CollectionOption(ForgeConfigSpec.ConfigValue<List<? extends String>> value, ModConfig.Type type, CollectionOptionBuilder<T, S> builder) {

        super(value, type, builder);
        this.disallowEmpty = builder.disallowEmpty;
        this.acceptableValues = builder.acceptableValues;
        this.nameToValue = builder::nameToValue;
    }

    @Override
    S convertValue(List<? extends String> value) {

        return value.stream()
                .map(this.nameToValue)
                .filter(Objects::nonNull)
                .collect(this.collect());
    }

    abstract Collector<? super T, ?, S> collect();

    public boolean isAllowedEmpty() {

        return !this.disallowEmpty;
    }

    public Collection<T> getAcceptableValues() {

        return this.acceptableValues;
    }

    public static abstract class CollectionOptionBuilder<T, S extends Collection<T>> extends ConfigOptionBuilder<S, List<? extends String>> {

        private boolean disallowEmpty;
        protected Collection<T> acceptableValues;

        CollectionOptionBuilder(OptionBuilder previous, String name, S defaultValue) {

            super(previous, name, defaultValue);
            assert !(defaultValue instanceof ImmutableCollection) : "CollectionOption doesn't support ImmutableCollection";
        }

        @Override
        List<String> buildComment() {

            List<String> comment = super.buildComment();
            if (this.disallowEmpty) {

                comment.add("This option is not allowed to be empty.");
            }

            if (this.acceptableValues != null) {

                comment.add("Allowed Values: " + this.acceptableValues.stream().map(Objects::toString).collect(Collectors.joining(", ")));
            }

            return comment;
        }

        public CollectionOptionBuilder<T, S> disallowEmpty() {

            this.disallowEmpty = true;
            return this;
        }

        @SafeVarargs
        public final CollectionOptionBuilder<T, S> acceptable(T... acceptableValues) {

            return this.acceptable(Lists.newArrayList(acceptableValues));
        }

        public CollectionOptionBuilder<T, S> acceptable(Collection<T> acceptableValues) {

            this.acceptableValues = acceptableValues;
            return this;
        }

        @Override
        abstract CollectionOption<T, S> createOption(ForgeConfigSpec.ConfigValue<List<? extends String>> value, ModConfig.Type type);

        @Override
        ForgeConfigSpec.ConfigValue<List<? extends String>> getConfigValue(ForgeConfigSpec.Builder builder) {

            assert !this.disallowEmpty || !this.defaultValue.isEmpty() : "Empty default collection on non-empty CollectionOptionBuilder";
            assert this.acceptableValues == null || !this.acceptableValues.isEmpty() : "Empty acceptable values collection";

            List<String> defaultAsList = this.defaultValue.stream().map(this::valueToName).filter(Objects::nonNull).collect(Collectors.toList());
            if (this.acceptableValues != null) {

                Set<String> acceptableToString = this.acceptableValues.stream().map(this::valueToName).filter(Objects::nonNull).collect(Collectors.toSet());
                if (this.disallowEmpty) {

                    return builder.defineList(split(this.name), () -> defaultAsList, acceptableToString::contains);
                }

                return builder.defineListAllowEmpty(split(this.name), () -> defaultAsList, acceptableToString::contains);
            }

            return builder.define(this.name, defaultAsList, o -> !this.disallowEmpty || !(o instanceof List<?>) || !((List<?>) o).isEmpty());
        }

        abstract String valueToName(T value);

        abstract T nameToValue(String name);

    }

}
