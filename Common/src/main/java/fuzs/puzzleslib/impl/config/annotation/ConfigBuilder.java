package fuzs.puzzleslib.impl.config.annotation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Build config values from given class, values are marked via {@link Config} annotation.
 * <p>
 * Underlying implementation largely based upon <a
 * href="https://github.com/VazkiiMods/Quark/blob/master/src/main/java/vazkii/quark/base/module/config/ConfigObjectSerializer.java">ConfigObjectSerializer.java</a>
 * from the Quark mod.
 */
public final class ConfigBuilder {

    private ConfigBuilder() {
        // NO-OP
    }

    public static <T extends ConfigCore> void build(ModConfigSpec.Builder builder, ConfigDataHolderImpl<?> context, @NonNull T o) {
        Objects.requireNonNull(o, "object is null");
        build(builder, context, o.getClass(), o);
    }

    public static <T extends ConfigCore> void build(ModConfigSpec.Builder builder, ConfigDataHolderImpl<?> context, Class<? extends T> clazz) {
        build(builder, context, clazz, null);
    }

    public static <T extends ConfigCore> void build(ModConfigSpec.Builder builder, ConfigDataHolderImpl<?> context, Class<? extends T> clazz, @Nullable T o) {
        Objects.requireNonNull(clazz, "clazz is null");
        // we support defining config values in categories that don't actually exist in dedicated classes by setting Config::category
        // those categories will be created here instead of inside #buildConfig, so they don't support their own category comments
        Map<List<String>, Collection<Field>> pathToFields = getAllFieldsWithPath(clazz);
        for (Map.Entry<List<String>, Collection<Field>> entry : pathToFields.entrySet()) {
            List<String> path = entry.getKey();
            if (!path.isEmpty()) {
                for (String category : path) {
                    builder.push(category);
                }
            }
            for (Field field : entry.getValue()) {
                field.setAccessible(true);
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                if (!isStatic) Objects.requireNonNull(o, "Null instance for non-static field");
                ConfigEntry<?> configEntry = getConfigEntry(field);
                configEntry.defineValue(builder, context, isStatic ? null : o);
            }
            if (!path.isEmpty()) {
                builder.pop(path.size());
            }
        }
        if (o != null) {
            o.addToBuilder(builder, context);
            // add config reload callback last to make sure it runs together with value callbacks, not with the additional callbacks
            context.acceptValueCallback(o::afterConfigReload);
        }
    }

    private static Map<List<String>, Collection<Field>> getAllFieldsWithPath(Class<?> target) {
        Multimap<List<String>, Field> paths = HashMultimap.create();
        for (Field field : collectAllFields(target)) {
            Config config = field.getDeclaredAnnotation(Config.class);
            if (config != null) {
                paths.put(new ArrayList<>(Arrays.asList(config.category())), field);
            }
        }
        return paths.asMap();
    }

    private static List<Field> collectAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static ConfigEntry<?> getConfigEntry(Field field) {
        return switch (field.getType()) {
            case Class<?> clazz when ConfigCore.class.isAssignableFrom(clazz) -> new ConfigEntry.ChildEntry(field);
            case Class<?> clazz when clazz == boolean.class -> new ValueEntry.BooleanEntry(field);
            case Class<?> clazz when clazz == int.class -> new NumberEntry.IntegerEntry(field);
            case Class<?> clazz when clazz == long.class -> new NumberEntry.LongEntry(field);
            case Class<?> clazz when clazz == double.class -> new NumberEntry.DoubleEntry(field);
            case Class<?> clazz when clazz == String.class -> new LimitedEntry.StringEntry(field);
            case Class<?> clazz when clazz.isEnum() -> new LimitedEntry.EnumEntry<>(field);
            case Class<?> clazz when clazz == List.class -> new LimitedEntry.ListEntry(field);
            default -> throw new IllegalArgumentException("Unsupported config value type: " + field.getType());
        };
    }
}
