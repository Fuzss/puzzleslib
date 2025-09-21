package fuzs.puzzleslib.fabric.api.core.v1;

import net.fabricmc.loader.api.FabricLoader;

/**
 * A utility class for dealing with {@code intermediary} mappings for development purposes.
 */
public final class FabricMappingResolver {

    private FabricMappingResolver() {
        // NO-OP
    }

    /**
     * Maps a class name from the current namespace ({@code intermediary} in production, {@code named} in development)
     * to {@code intermediary}.
     * <p>
     * E.g. {@code net.minecraft.core.MappedRegistry} will be mapped to {@code net.minecraft.class_2378} in development,
     * while in production {@code net.minecraft.core.MappedRegistry} will be mapped to
     * {@code net.minecraft.core.MappedRegistry}, as no mapping is found, since the {@code named} namespace is not
     * present.
     * <p>
     * If no mapping is found, like when the provided namespace format does not match the current namespace, the
     * original value is returned.
     *
     * @param clazz the class name
     * @return the class name mapped to {@code intermediary} if possible, otherwise the original value
     */
    public static String unmapClassName(Class<?> clazz) {
        return unmapClassName(clazz.getName());
    }

    /**
     * Maps a class name from the current namespace ({@code intermediary} in production, {@code named} in development)
     * to {@code intermediary}.
     * <p>
     * E.g. {@code net.minecraft.core.MappedRegistry} will be mapped to {@code net.minecraft.class_2378} in development,
     * while in production {@code net.minecraft.core.MappedRegistry} will be mapped to
     * {@code net.minecraft.core.MappedRegistry}, as no mapping is found, since the {@code named} namespace is not
     * present.
     * <p>
     * If no mapping is found, like when the provided namespace format does not match the current namespace, the
     * original value is returned.
     *
     * @param clazz the class name
     * @return the class name mapped to {@code intermediary} if possible, otherwise the original value
     */
    public static String unmapClassName(String clazz) {
        return FabricLoader.getInstance().getMappingResolver().unmapClassName("intermediary", clazz.replace('/', '.'));
    }

    /**
     * Maps a field name from {@code intermediary} to the current namespace ({@code intermediary} in production,
     * {@code named} in development).
     * <p>
     * E.g. {@code field_36468} will be mapped to {@code this$0} in development, while in production {@code field_36468}
     * will be mapped to {@code field_36468}, as no mapping is found, since the {@code named} namespace is not present.
     * <p>
     * If no mapping is found, like when the provided namespace format does not match the current namespace, the
     * original value is returned.
     *
     * @param owningClazz the class the field is found in, must be provided as {@code intermediary}, such as
     *                    {@code net.minecraft.class_2378} for {@code net.minecraft.core.MappedRegistry}
     * @param fieldName   the field name, must be provided as {@code intermediary}, such as {@code field_36468}
     * @param fieldType   the field type, must be provided as {@code intermediary}, such as
     *                    {@code Lnet/minecraft/class_2378;} for {@code net.minecraft.core.MappedRegistry}
     * @return the field name mapped to the current namespace if possible, otherwise the original value
     */
    public static String mapFieldName(Class<?> owningClazz, String fieldName, Class<?> fieldType) {
        return mapFieldName(unmapClassName(owningClazz), fieldName, fieldType);
    }

    /**
     * Maps a field name from {@code intermediary} to the current namespace ({@code intermediary} in production,
     * {@code named} in development).
     * <p>
     * E.g. {@code field_36468} will be mapped to {@code this$0} in development, while in production {@code field_36468}
     * will be mapped to {@code field_36468}, as no mapping is found, since the {@code named} namespace is not present.
     * <p>
     * If no mapping is found, like when the provided namespace format does not match the current namespace, the
     * original value is returned.
     *
     * @param owningClazz the class the field is found in, must be provided as {@code intermediary}, such as
     *                    {@code net.minecraft.class_2378} for {@code net.minecraft.core.MappedRegistry}
     * @param fieldName   the field name, must be provided as {@code intermediary}, such as {@code field_36468}
     * @param fieldType   the field type, must be provided as {@code intermediary}, such as
     *                    {@code Lnet/minecraft/class_2378;} for {@code net.minecraft.core.MappedRegistry}
     * @return the field name mapped to the current namespace if possible, otherwise the original value
     */
    public static String mapFieldName(String owningClazz, String fieldName, Class<?> fieldType) {
        return mapFieldName(owningClazz, fieldName, "L" + unmapClassName(fieldType).replace('.', '/') + ";");
    }

    /**
     * Maps a field name from {@code intermediary} to the current namespace ({@code intermediary} in production,
     * {@code named} in development).
     * <p>
     * E.g. {@code field_36468} will be mapped to {@code this$0} in development, while in production {@code field_36468}
     * will be mapped to {@code field_36468}, as no mapping is found, since the {@code named} namespace is not present.
     * <p>
     * If no mapping is found, like when the provided namespace format does not match the current namespace, the
     * original value is returned.
     *
     * @param owningClazz the class the field is found in, must be provided as {@code intermediary}, such as
     *                    {@code net.minecraft.class_2378} for {@code net.minecraft.core.MappedRegistry}
     * @param fieldName   the field name, must be provided as {@code intermediary}, such as {@code field_36468}
     * @param fieldType   the field type, must be provided as {@code intermediary}, such as
     *                    {@code Lnet/minecraft/class_2378;} for {@code net.minecraft.core.MappedRegistry}
     * @return the field name mapped to the current namespace if possible, otherwise the original value
     */
    public static String mapFieldName(String owningClazz, String fieldName, String fieldType) {
        owningClazz = unmapClassName(owningClazz);
        fieldType = fieldType.matches("^L.+;$") ? fieldType : "L" + unmapClassName(fieldType) + ";";
        return FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", owningClazz, fieldName,
                fieldType.replace('.', '/')
        );
    }
}
