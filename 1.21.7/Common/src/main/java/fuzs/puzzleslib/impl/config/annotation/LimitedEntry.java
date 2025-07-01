package fuzs.puzzleslib.impl.config.annotation;

import com.google.common.base.Predicates;
import fuzs.puzzleslib.api.config.v3.Config;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class LimitedEntry<T> extends ValueEntry<T> {

    public LimitedEntry(Field field) {
        super(field);
    }

    private Set<String> getAllowedValues() {
        Config.AllowedValues allowedValues = this.field.getDeclaredAnnotation(Config.AllowedValues.class);
        if (allowedValues != null && allowedValues.values().length != 0) {
            return new LinkedHashSet<>(Arrays.asList(allowedValues.values()));
        } else {
            return this.getAllValues();
        }
    }

    Set<String> getAllValues() {
        return Collections.emptySet();
    }

    public final Set<String> getAllowedValueStrings() {
        Set<String> allowedValues = this.getAllowedValues()
                .stream()
                .map(this::applyStringFormatting)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (!allowedValues.isEmpty()) {
            Set<String> allValues = this.getAllValues()
                    .stream()
                    .map(this::applyStringFormatting)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            if (!allValues.isEmpty()) {
                for (String s : allowedValues) {
                    if (!allValues.contains(s)) {
                        throw new IllegalArgumentException(s + " is not contained in " + allValues);
                    }
                }
            }
        }

        return allowedValues;
    }

    static <E extends Enum<E>> Set<String> getAllEnumValues(Class<?> clazz) {
        return Arrays.stream(clazz.getEnumConstants())
                .map(value -> (E) value)
                .map(Enum::name)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public List<String> getComments(@Nullable Object o) {
        List<String> comments = super.getComments(o);
        this.addAllowedValuesComment(comments);
        return comments;
    }

    public void addAllowedValuesComment(List<String> comments) {
        Set<String> allowedValues = this.getAllowedValueStrings();
        if (!allowedValues.isEmpty()) {
            comments.add("Allowed Values: " + String.join(", ", allowedValues));
        }
    }

    public Predicate<Object> getValidator() {
        Set<String> allowedValues = this.getAllowedValueStrings();
        if (!allowedValues.isEmpty()) {
            return (Object o) -> {
                if (o != null) {
                    String s = o instanceof Enum<?> ? ((Enum<?>) o).name() : o.toString();
                    return allowedValues.contains(this.applyStringFormatting(s));
                } else {
                    return false;
                }
            };
        } else {
            return this.getEmptyValidator();
        }
    }

    public String applyStringFormatting(String s) {
        return s.toUpperCase(Locale.ROOT);
    }

    public Predicate<Object> getEmptyValidator() {
        return Predicates.alwaysTrue();
    }

    public static final class StringEntry extends LimitedEntry<String> {

        public StringEntry(Field field) {
            super(field);
        }

        @Override
        public ModConfigSpec.ConfigValue<String> getConfigValue(ModConfigSpec.Builder builder, @Nullable Object o) {
            return builder.define(this.getName(), this.getDefaultValue(o), this.getValidator());
        }

        @Override
        public Predicate<Object> getEmptyValidator() {
            return String.class::isInstance;
        }
    }

    public static final class EnumEntry<E extends Enum<E>> extends LimitedEntry<E> {

        public EnumEntry(Field field) {
            super(field);
        }

        @Override
        public ModConfigSpec.EnumValue<E> getConfigValue(ModConfigSpec.Builder builder, @Nullable Object o) {
            return builder.defineEnum(this.getName(), this.getDefaultValue(o), this.getValidator());
        }

        @Override
        Set<String> getAllValues() {
            return getAllEnumValues(this.field.getType());
        }

        @Override
        public void addAllowedValuesComment(List<String> comments) {
            // NO-OP
        }
    }

    public static final class ListEntry extends LimitedEntry<List<?>> {

        public ListEntry(Field field) {
            super(field);
        }

        @Nullable
        public Type getListType() {
            if (this.field.getGenericType() instanceof ParameterizedType type &&
                    type.getActualTypeArguments().length > 0) {
                return type.getActualTypeArguments()[0];
            } else {
                return null;
            }
        }

        @Override
        public ModConfigSpec.ConfigValue<List<?>> getConfigValue(ModConfigSpec.Builder builder, @Nullable Object o) {
            Supplier<?> newElementSupplier = getNewElementSupplier(this.getListType());
            return builder.defineList(this.getName(), this.getDefaultValue(o), (Supplier<Object>) newElementSupplier,
                    this.getValidator()
            );
        }

        @Override
        public Set<String> getAllValues() {
            if (this.getListType() instanceof Class<?> clazz && clazz.isEnum()) {
                return getAllEnumValues(clazz);
            } else {
                return super.getAllValues();
            }
        }

        @Override
        public String applyStringFormatting(String s) {
            return s.toLowerCase(Locale.ROOT);
        }

        static Supplier<?> getNewElementSupplier(Type type) {
            Objects.requireNonNull(type, "type is null");
            // all the value types supported by ModConfigSpec
            return () -> switch (type) {
                case Class<?> clazz when clazz == String.class -> "";
                case Class<?> clazz when clazz.isEnum() -> clazz.getEnumConstants()[0];
                case Class<?> clazz when clazz == Boolean.class -> false;
                case Class<?> clazz when clazz == Integer.class -> 0;
                case Class<?> clazz when clazz == Long.class -> 0L;
                case Class<?> clazz when clazz == Double.class -> 0.0;
                default -> throw new IllegalArgumentException("Unsupported list type: " + type);
            };
        }
    }
}
