package fuzs.puzzleslib.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * helper for encoding and decoding a json config file
 */
public class JsonSerializationUtil {
    /**
     * default string for json config file format identifier
     */
    public static final String FILE_FORMAT_STRING = "__file_format";
    /**
     * default string for json config comment identifier
     */
    public static final String COMMENT_STRING = "__comment";

    /**
     * config base
     * @param comments comments to add
     * @return new base {@link JsonObject} for building a new json config
     */
    public static JsonObject getConfigBase(String... comments) {
        return getConfigBase(-1, comments);
    }

    /**
     * config base
     * @param fileFormat file format number, use -1 to skip option
     * @param comments comments to add
     * @return new base {@link JsonObject} for building a new json config
     */
    public static JsonObject getConfigBase(int fileFormat, String... comments) {
        JsonObject jsonobject = new JsonObject();
        if (fileFormat != -1) {
            jsonobject.addProperty(FILE_FORMAT_STRING, fileFormat);
        }
        addConfigComment(jsonobject, comments);
        return jsonobject;
    }

    /**
     * add comment to a json object, add as array when there are multiples
     * @param jsonobject {@link JsonObject} to add to
     * @param comments comment to add
     */
    private static void addConfigComment(JsonObject jsonobject, String... comments) {
        if (comments.length == 1) {
            jsonobject.addProperty(COMMENT_STRING, comments[0]);
        } else if (comments.length > 1) {
            JsonArray jsonarray = new JsonArray();
            Stream.of(comments).forEach(jsonarray::add);
            jsonobject.add(COMMENT_STRING, jsonarray);
        }
    }

    /**
     * @param jsonObject json object to search for file format property
     * @return file format or -1 if not found
     */
    public static int readFileFormat(JsonObject jsonObject) {
        return Optional.ofNullable(jsonObject.get(FILE_FORMAT_STRING)).map(JsonElement::getAsInt).orElse(-1);
    }

    /**
     * @param reader json reader
     * @return stream of object without default keys
     */
    public static JsonObject readJsonObject(FileReader reader) {
        final JsonElement jsonElement = JsonConfigFileUtil.GSON.fromJson(reader, JsonElement.class);
        if (!jsonElement.isJsonObject()) throw new IllegalArgumentException("unable to get json object from file reader");
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject.remove(FILE_FORMAT_STRING);
        jsonObject.remove(COMMENT_STRING);
        return jsonObject;
    }
}
