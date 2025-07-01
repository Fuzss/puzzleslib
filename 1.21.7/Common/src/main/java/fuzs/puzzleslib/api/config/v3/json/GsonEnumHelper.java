package fuzs.puzzleslib.api.config.v3.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.GsonHelper;

/**
 * A simple extension to {@link GsonHelper} for allowing the serialization of enum values.
 */
public final class GsonEnumHelper {

    private GsonEnumHelper() {
        // NO-OP
    }

    /**
     * Gets an enum value from a {@link JsonObject}, or returns a fallback if something goes wrong.
     *
     * @param enumName the name of the enum value to retrieve from <code>clazz</code>
     * @param clazz    the enum type class
     * @param <T>      the enum type
     * @return the enum value
     */
    public static <T extends Enum<T>> T convertToEnum(String enumName, Class<T> clazz) {
        try {
            return Enum.valueOf(clazz, enumName);
        } catch (IllegalArgumentException e) {
            throw new JsonSyntaxException("Unable to deserialize enum value" + enumName + "of type " + clazz, e);
        }
    }

    /**
     * Gets an enum value from a {@link JsonObject}, throws an exception if <code>key</code> is not found.
     *
     * @param jsonObject the {@link JsonObject} to get the value from
     * @param key        the json value key
     * @param clazz      the enum type class
     * @param <T>        the enum type
     * @return the enum value
     */
    public static <T extends Enum<T>> T getAsEnum(JsonObject jsonObject, String key, Class<T> clazz) {
        if (jsonObject.has(key)) {
            return convertToEnum(GsonHelper.getAsString(jsonObject, key), clazz);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a " + clazz);
        }
    }

    /**
     * Gets an enum value from a {@link JsonObject}, or returns a fallback if something goes wrong.
     *
     * @param jsonObject the {@link JsonObject} to get the value from
     * @param key        the json value key
     * @param clazz      the enum type class
     * @param fallback   fallback value in case <code>key</code> is not present of the string value found for <code>key</code> is not found in the enum
     * @param <T>        the enum type
     * @return the enum value or <code>fallback</code>
     */
    public static <T extends Enum<T>> T getAsEnum(JsonObject jsonObject, String key, Class<T> clazz, T fallback) {
        return jsonObject.has(key) ? convertToEnum(GsonHelper.getAsString(jsonObject, key), clazz) : fallback;
    }
}
