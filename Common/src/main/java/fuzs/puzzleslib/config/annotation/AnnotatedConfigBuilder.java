package fuzs.puzzleslib.config.annotation;

import com.google.common.base.CaseFormat;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.ObjectArrays;
import fuzs.puzzleslib.config.ConfigCore;
import fuzs.puzzleslib.config.ConfigDataHolderImpl;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * build config values from given class, values are marked via {@link Config} annotation
 * large based upon <a href="https://github.com/VazkiiMods/Quark">Quark's</a> <code>vazkii.quark.base.module.config.ConfigObjectSerializer</code> class
 */
public class AnnotatedConfigBuilder {
    /**
     * Unsafe instance for setting final fields in configs, so they may remain effectively read-only.
     */
    private static final Unsafe UNSAFE;

    static {
        try {
            Constructor<?> constructor = Unsafe.class.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            UNSAFE = (Unsafe) constructor.newInstance();
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param builder forge builder for creating forge config values, setting comments, etc.
     * @param context callback
     * @param target object instance
     */
    public static <T extends ConfigCore> void serialize(ForgeConfigSpec.Builder builder, ConfigDataHolderImpl<?> context, @NotNull T target) {
        serialize(builder, context, target.getClass(), target);
    }

    /**
     * @param builder forge builder for creating forge config values, setting comments, etc.
     * @param context callback
     * @param target target class
     */
    public static <T extends ConfigCore> void serialize(ForgeConfigSpec.Builder builder, ConfigDataHolderImpl<?> context, Class<? extends T> target) {
        serialize(builder, context, target, null);
    }

    /**
     * @param builder forge builder for creating forge config values, setting comments, etc.
     * @param context callback
     * @param target target class
     * @param instance object instance, null when static
     * @param <T> <code>instance</code> type
     */
    public static <T extends ConfigCore> void serialize(final ForgeConfigSpec.Builder builder, final ConfigDataHolderImpl<?> context, Class<? extends T> target, @Nullable T instance) {
        // add config reload callback first to make sure it's called when initially loading configs
        // (since on some systems reload event doesn't trigger during startup, resulting in configs only being loaded here)
        if (instance != null) context.accept(instance::afterConfigReload);
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
                // final fields are enforced since config values are read-only
                if (!Modifier.isFinal(field.getModifiers())) throw new RuntimeException("Field must be final");
                buildConfig(builder, context, isStatic ? null : instance, field, field.getDeclaredAnnotation(Config.class));
            }
            if (!path.isEmpty()) builder.pop(path.size());
        }
        // legacy method, kept for now for types unsupported by annotation system
        // not available when constructing static config classes
        if (instance != null) instance.addToBuilder(builder, context);
    }

    /**
     * collects all field, storing them by path (as defined by categories) in the config
     *
     * @param target    class to collect fields from
     * @return          all fields decorated with {@link Config} annotation
     */
    private static Map<List<String>, Collection<Field>> setupFields(Class<?> target) {
        Multimap<List<String>, Field> pathToField = HashMultimap.create();
        for (Field field : collectFieldsRecursive(target)) {
            Config annotation = field.getDeclaredAnnotation(Config.class);
            if (annotation != null) {
                pathToField.put(Lists.newArrayList(annotation.category()), field);
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
    private static List<Field> collectFieldsRecursive(Class<?> clazz) {
        List<Field> list = new LinkedList<>();
        while (clazz != Object.class) {
            list.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return list;
    }

    /**
     * @param builder forge builder for creating forge config values, setting comments, etc.
     * @param context callback
     * @param instance object instance, null when static
     * @param field field to save to
     * @param annotation config annotation for config value data
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void buildConfig(final ForgeConfigSpec.Builder builder, final ConfigDataHolderImpl<?> context, @Nullable Object instance, Field field, Config annotation) {
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
        final String[] description = annotation.description();
        if (description.length != 0) builder.comment(description);

        if (ConfigCore.class.isAssignableFrom(type)) {
            builder.push(name);
            serialize(builder, context, (Class<? extends ConfigCore>) type, (ConfigCore) defaultValue);
            builder.pop();
            return;
        }

        // does this require a world restart? just an indicator, doesn't actually do something
        if (annotation.worldRestart()) builder.worldRestart();

        if (type == boolean.class) {
            addCallback(context, builder.define(name, (boolean) defaultValue), field, instance);
        } else if (type == int.class) {
            int min = Integer.MIN_VALUE;
            int max = Integer.MAX_VALUE;
            Config.IntRange intRange = field.getDeclaredAnnotation(Config.IntRange.class);
            if (intRange != null) {
                min = intRange.min();
                max = intRange.max();
            }
            addCallback(context, builder.defineInRange(name, (int) defaultValue, min, max), field, instance);
        } else if (type == long.class) {
            long min = Long.MIN_VALUE;
            long max = Long.MAX_VALUE;
            Config.LongRange longRange = field.getDeclaredAnnotation(Config.LongRange.class);
            if (longRange != null) {
                min = longRange.min();
                max = longRange.max();
            }
            addCallback(context, builder.defineInRange(name, (long) defaultValue, min, max), field, instance);
        } else if (type == double.class) {
            double min = Double.MIN_VALUE;
            double max = Double.MAX_VALUE;
            Config.DoubleRange doubleRange = field.getDeclaredAnnotation(Config.DoubleRange.class);
            if (doubleRange != null) {
                min = doubleRange.min();
                max = doubleRange.max();
            }
            addCallback(context, builder.defineInRange(name, (double) defaultValue, min, max), field, instance);
        } else if (type == String.class) {
            Config.AllowedValues allowedValues = field.getDeclaredAnnotation(Config.AllowedValues.class);
            if (allowedValues != null && allowedValues.values().length != 0) {
                builder.comment(ObjectArrays.concat(description, String.format("Allowed Values: %s", String.join(", ", allowedValues.values()))));
                addCallback(context, builder.define(name, (String) defaultValue, o -> testAllowedValues(allowedValues.values(), o)), field, instance);
            } else {
                addCallback(context, builder.define(name, (String) defaultValue), field, instance);
            }
        } else if (type.isEnum()) {
            Config.AllowedValues allowedValues = field.getDeclaredAnnotation(Config.AllowedValues.class);
            if (allowedValues != null && allowedValues.values().length != 0) {
                // allowed values line handled by forge
                addCallback(context, builder.defineEnum(name, (Enum) defaultValue, o -> testAllowedValues(allowedValues.values(), o)), field, instance);
            } else {
                addCallback(context, builder.defineEnum(name, (Enum) defaultValue), field, instance);
            }
        } else if (type == List.class) {
            // currently, only supports a predicate for string and enum lists, might also want to add range check for number values
            Config.AllowedValues allowedValues = field.getDeclaredAnnotation(Config.AllowedValues.class);
            if (allowedValues != null && allowedValues.values().length != 0) {
                builder.comment(ObjectArrays.concat(description, String.format("Allowed Values: %s", String.join(", ", allowedValues.values()))));
                addCallback(context, builder.defineList(name, (List<?>) defaultValue, o -> testAllowedValues(allowedValues.values(), o)), field, instance);
            } else {
                addCallback(context, builder.defineList(name, (List<?>) defaultValue, o -> true), field, instance);
            }
        } else {
            throw new IllegalArgumentException(String.format("Unsupported config value type: %s", type));
        }
    }

    /**
     * @param allowedValues allowed values array
     * @param o object to test
     * @return does <code>allowedValues</code> contain <code>o</code> tested by comparing strings
     */
    private static boolean testAllowedValues(String[] allowedValues, @Nullable Object o) {
        if (o != null) {
            String value = o instanceof Enum<?> ? ((Enum<?>) o).name() : o.toString();
            for (String allowedValue : allowedValues) {
                if (allowedValue.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param context callback
     * @param configValue forge config value
     * @param field field to save to
     * @param instance object instance, null when static
     */
    private static void addCallback(ConfigDataHolderImpl<?> context, ForgeConfigSpec.ConfigValue<?> configValue, Field field, @Nullable Object instance) {
        context.accept(configValue, v -> {
            long fieldOffset = UNSAFE.objectFieldOffset(field);
            UNSAFE.putObject(instance, fieldOffset, configValue.get());
        });
    }
}
