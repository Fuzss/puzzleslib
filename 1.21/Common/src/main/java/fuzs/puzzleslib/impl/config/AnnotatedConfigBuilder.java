package fuzs.puzzleslib.impl.config;

import com.google.common.base.CaseFormat;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;

/**
 * Build config values from given class, values are marked via {@link Config} annotation.
 * <p>
 * Large based upon <a
 * href="https://github.com/VazkiiMods/Quark/blob/master/src/main/java/vazkii/quark/base/module/config/ConfigObjectSerializer.java">ConfigObjectSerializer.java</a>
 * from the Quark mod.
 */
public final class AnnotatedConfigBuilder {

    private AnnotatedConfigBuilder() {
        // NO-OP
    }

    /**
     * @param builder forge builder for creating forge config values, setting comments, etc.
     * @param context callback
     * @param target  object instance
     */
    public static <T extends ConfigCore> void serialize(ModConfigSpec.Builder builder, ConfigDataHolderImpl<?> context, @NotNull T target) {
        serialize(builder, context, target.getClass(), target);
    }

    /**
     * @param builder forge builder for creating forge config values, setting comments, etc.
     * @param context callback
     * @param target  target class
     */
    public static <T extends ConfigCore> void serialize(ModConfigSpec.Builder builder, ConfigDataHolderImpl<?> context, Class<? extends T> target) {
        serialize(builder, context, target, null);
    }

    /**
     * @param builder  forge builder for creating forge config values, setting comments, etc.
     * @param context  callback
     * @param target   target class
     * @param instance object instance, null when static
     * @param <T>      <code>instance</code> type
     */
    public static <T extends ConfigCore> void serialize(final ModConfigSpec.Builder builder, final ConfigDataHolderImpl<?> context, Class<? extends T> target, @Nullable T instance) {
        // we support defining config values in categories that don't actually exist in dedicated classes by setting Config::category
        // those categories will be created here instead of inside #buildConfig, so they don't support their own category comments
        Map<List<String>, Collection<Field>> pathToFields = setupFields(target);
        for (Map.Entry<List<String>, Collection<Field>> entry : pathToFields.entrySet()) {
            final List<String> path = entry.getKey();
            if (!path.isEmpty()) {
                for (String category : path) {
                    builder.push(category);
                }
            }
            for (Field field : entry.getValue()) {
                field.setAccessible(true);
                final boolean isStatic = Modifier.isStatic(field.getModifiers());
                if (!isStatic) Objects.requireNonNull(instance, "Null instance for non-static field");
                buildConfig(builder, context, isStatic ? null : instance, field,
                        field.getDeclaredAnnotation(Config.class)
                );
            }
            if (!path.isEmpty()) builder.pop(path.size());
        }
        if (instance != null) {
            instance.addToBuilder(builder, context);
            // add config reload callback last to make sure it runs together with value callbacks, not with the additional callbacks
            context.acceptValueCallback(instance::afterConfigReload);
        }
    }

    /**
     * collects all field, storing them by path (as defined by categories) in the config
     *
     * @param target class to collect fields from
     * @return all fields decorated with {@link Config} annotation
     */
    private static Map<List<String>, Collection<Field>> setupFields(Class<?> target) {
        Multimap<List<String>, Field> pathToField = HashMultimap.create();
        for (Field field : getAllFieldsRecursive(target)) {
            Config annotation = field.getDeclaredAnnotation(Config.class);
            if (annotation != null) {
                pathToField.put(new ArrayList<>(Arrays.asList(annotation.category())), field);
            }
        }
        return pathToField.asMap();
    }

    /**
     * get all fields from a class, and it's possible superclasses
     *
     * @param clazz class to get fields for
     * @return all fields from class and all superclasses
     */
    private static List<Field> getAllFieldsRecursive(Class<?> clazz) {
        List<Field> list = new LinkedList<>();
        while (clazz != Object.class) {
            list.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return list;
    }

    /**
     * @param builder    forge builder for creating forge config values, setting comments, etc.
     * @param context    callback
     * @param instance   object instance, null when static
     * @param field      field to save to
     * @param annotation config annotation for config value data
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void buildConfig(final ModConfigSpec.Builder builder, final ConfigDataHolderImpl<?> context, @Nullable Object instance, Field field, Config annotation) {
        // get the name from the config, often this is left blank, so instead we create it from the field's name with an underscore format
        String name = annotation.name();
        if (StringUtils.isBlank(name)) {
            name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
        }

        // get the value from this field, it'll be used as the default value for the config value
        Class<?> type = field.getType();
        Object defaultValue;
        try {
            defaultValue = field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // add a description if one is present
        List<String> comments = new ArrayList<>(Arrays.asList(annotation.description()));

        if (ConfigCore.class.isAssignableFrom(type)) {
            builder.comment(comments.toArray(String[]::new));
            builder.push(name);
            serialize(builder, context, (Class<? extends ConfigCore>) type, (ConfigCore) defaultValue);
            builder.pop();
            return;
        }

        // final fields are permitted until here, since values must be able to change
        // previously only new config categories are handled, those instances never change
        if (Modifier.isFinal(field.getModifiers())) throw new RuntimeException("Field may not be final");

        // does this require a world restart? just an indicator, doesn't actually do something
        if (annotation.worldRestart()) builder.worldRestart();

        if (type == boolean.class) {
            builder.comment(comments.toArray(String[]::new));
            defineValue(context, builder.define(name, (boolean) defaultValue), field, instance, comments);
        } else if (type == int.class) {
            int min = Integer.MIN_VALUE;
            int max = Integer.MAX_VALUE;
            Config.IntRange intRange = field.getDeclaredAnnotation(Config.IntRange.class);
            if (intRange != null) {
                min = intRange.min();
                max = intRange.max();
            }
            builder.comment(comments.toArray(String[]::new));
            defineValue(context, builder.defineInRange(name, (int) defaultValue, min, max), field, instance, comments);
        } else if (type == long.class) {
            long min = Long.MIN_VALUE;
            long max = Long.MAX_VALUE;
            Config.LongRange longRange = field.getDeclaredAnnotation(Config.LongRange.class);
            if (longRange != null) {
                min = longRange.min();
                max = longRange.max();
            }
            builder.comment(comments.toArray(String[]::new));
            defineValue(context, builder.defineInRange(name, (long) defaultValue, min, max), field, instance, comments);
        } else if (type == double.class) {
            double min = Double.MIN_VALUE;
            double max = Double.MAX_VALUE;
            Config.DoubleRange doubleRange = field.getDeclaredAnnotation(Config.DoubleRange.class);
            if (doubleRange != null) {
                min = doubleRange.min();
                max = doubleRange.max();
            }
            builder.comment(comments.toArray(String[]::new));
            defineValue(context, builder.defineInRange(name, (double) defaultValue, min, max), field, instance, comments);
        } else if (type == String.class) {
            Set<String> allowedValues = getAllowedValues(field.getDeclaredAnnotation(Config.AllowedValues.class));
            getAllowedValuesComment(allowedValues).ifPresent(builder::comment);
            builder.comment(comments.toArray(String[]::new));
            defineValue(context,
                    builder.define(name, (String) defaultValue,
                            getValidator(allowedValues)),
                    field, instance, comments
            );
        } else if (type.isEnum()) {
            Set<String> allowedValues = getAllowedValues(field.getDeclaredAnnotation(Config.AllowedValues.class));
            getAllowedValuesComment(allowedValues).ifPresent(builder::comment);
            builder.comment(comments.toArray(String[]::new));
            defineValue(context, builder.defineEnum(name, (Enum) defaultValue,
                    getValidator(allowedValues)
            ), field, instance, comments);
        } else if (type == List.class) {
            Set<String> allowedValues = getAllowedValues(field.getDeclaredAnnotation(Config.AllowedValues.class));
            getAllowedValuesComment(allowedValues).ifPresent(builder::comment);
            builder.comment(comments.toArray(String[]::new));
            Supplier<?> newElementSupplier = getNewElementSupplier(field.getGenericType());
            defineValue(context, builder.defineList(name, (List) defaultValue, newElementSupplier,
                    getValidator(allowedValues)
            ), field, instance, comments);
        } else {
            throw new IllegalArgumentException("Unsupported config value type: " + type);
        }
    }

    private static Optional<String> getAllowedValuesComment(Set<String> allowedValues) {
        if (!allowedValues.isEmpty()) {
            return Optional.of("Allowed Values: " + String.join(", ", allowedValues));
        } else {
            return Optional.empty();
        }
    }

    private static Set<String> getAllowedValues(@Nullable Config.AllowedValues allowedValues) {
        if (allowedValues != null && allowedValues.values().length != 0) {
            return new LinkedHashSet<>(Arrays.asList(allowedValues.values()));
        } else {
            return Collections.emptySet();
        }
    }

    private static Predicate<Object> getValidator(Set<String> allowedValues) {
        if (!allowedValues.isEmpty()) {
            return (Object o) -> {
                return o != null && allowedValues.contains(o instanceof Enum<?> ? ((Enum<?>) o).name() : o.toString());
            };
        } else {
            return Predicates.alwaysTrue();
        }
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

    private static void defineValue(ConfigDataHolderImpl<?> context, ModConfigSpec.ConfigValue<?> configValue, Field field, @Nullable Object instance, List<String> comments) {
        addCallback(context, configValue, field, instance);
        ConfigTranslationsManager.INSTANCE.addConfigValue(context.getModId(), configValue.getPath());
        ConfigTranslationsManager.INSTANCE.addConfigValueComment(context.getModId(), configValue.getPath(), comments);
    }

    /**
     * @param context     callback
     * @param configValue forge config value
     * @param field       field to save to
     * @param instance    object instance, null when static
     */
    private static void addCallback(ValueCallback context, ModConfigSpec.ConfigValue<?> configValue, Field field, @Nullable Object instance) {
        try {
            MethodHandle methodHandle = MethodHandles.lookup().unreflectSetter(field);
            context.accept(configValue, value -> {
                try {
                    methodHandle.invoke(instance, configValue.get());
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            });
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
