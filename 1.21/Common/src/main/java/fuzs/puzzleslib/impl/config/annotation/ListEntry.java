package fuzs.puzzleslib.impl.config.annotation;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Supplier;

public final class ListEntry extends LimitedEntry<List<?>> {

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
