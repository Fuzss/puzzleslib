package fuzs.puzzleslib.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * helper for encoding and decoding a json config file
 */
@SuppressWarnings("unused")
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
     * @param fileFormat file format number
     * @param comments comments to add
     * @return new base {@link JsonObject} for building a new json config
     */
    public static JsonObject getConfigBase(int fileFormat, String... comments) {

        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty(FILE_FORMAT_STRING, fileFormat);
        addConfigComment(jsonobject, comments);

        return jsonobject;
    }

    /**
     * add comment to a json object, add as array when there are multiples
     * @param jsonobject {@link JsonObject} to add to
     * @param comments comment to add
     */
    private static void addConfigComment(JsonObject jsonobject, String[] comments) {

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
     * @param jsonObject object to create stream of
     * @return stream of object without default keys
     */
    public static Stream<Map.Entry<String, JsonElement>> getJsonElementStream(JsonObject jsonObject) {

        return jsonObject.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(FILE_FORMAT_STRING) && !entry.getKey().equals(COMMENT_STRING));
    }

}
