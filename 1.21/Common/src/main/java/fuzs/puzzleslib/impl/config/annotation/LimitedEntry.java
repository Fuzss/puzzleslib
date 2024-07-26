package fuzs.puzzleslib.impl.config.annotation;

import com.google.common.base.Predicates;
import fuzs.puzzleslib.api.config.v3.Config;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class LimitedEntry<T> extends ValueEntry<T> {
    
    public LimitedEntry(Field field) {
        super(field);
    }

    private Set<String> getAllowedValues() {
        Config.AllowedValues allowedValues = this.field.getDeclaredAnnotation(Config.AllowedValues.class);
        if (allowedValues != null && allowedValues.values().length != 0) {
            return new LinkedHashSet<>(Arrays.asList(allowedValues.values()));
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public List<String> getComments(@Nullable Object o) {
        List<String> comments = super.getComments(o);
        Set<String> allowedValues = this.getAllowedValues();
        if (!allowedValues.isEmpty()) {
            comments.add("Allowed Values: " + String.join(", ", allowedValues));
        }
        return comments;
    }

    public Predicate<Object> getValidator() {
        Set<String> allowedValues = this.getAllowedValues();
        if (!allowedValues.isEmpty()) {
            return (Object o) -> {
                return o != null && allowedValues.contains(o instanceof Enum<?> ? ((Enum<?>) o).name() : o.toString());
            };
        } else {
            return Predicates.alwaysTrue();
        }
    }

    public static final class StringEntry extends LimitedEntry<String> {

        public StringEntry(Field field) {
            super(field);
        }

        @Override
        public ModConfigSpec.ConfigValue<String> getConfigValue(ModConfigSpec.Builder builder, @Nullable Object o) {
            return builder.define(this.getName(), this.getDefaultValue(o), this.getValidator());
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
    }

    public static final class ListEntry extends LimitedEntry<List<?>> {

        public ListEntry(Field field) {
            super(field);
        }

        @Override
        public ModConfigSpec.ConfigValue<List<?>> getConfigValue(ModConfigSpec.Builder builder, @Nullable Object o) {
            Supplier<?> newElementSupplier = getNewElementSupplier(this.field.getGenericType());
            return builder.defineList(this.getName(), this.getDefaultValue(o), (Supplier<Object>) newElementSupplier,
                    this.getValidator()
            );
        }

        private static Supplier<?> getNewElementSupplier(Type type) {
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
