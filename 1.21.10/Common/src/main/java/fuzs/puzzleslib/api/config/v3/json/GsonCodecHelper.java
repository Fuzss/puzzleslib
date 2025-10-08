package fuzs.puzzleslib.api.config.v3.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.data.CachedOutput;
import net.minecraft.util.GsonHelper;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A helper for encoding &amp; decoding values and json files.
 */
public final class GsonCodecHelper {
    /**
     * The gson instance.
     */
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private GsonCodecHelper() {
        // NO-OP
    }

    /**
     * Save a value as a json file at a provided path if no file is already present.
     *
     * @param codec the codec used for encoding
     * @param value the value to encode
     * @param path  the path to save the resulting file to
     * @param <T>   the value type
     * @return if the file was saved successfully
     */
    public static <T> boolean saveIfAbsent(Codec<T> codec, Supplier<T> value, Path path) {
        return !path.toFile().exists() && save(codec, value.get(), path);
    }

    /**
     * Save a value as a json file at a provided path.
     * <p>
     * Partially adapted from {@link net.minecraft.data.DataProvider#saveStable(CachedOutput, JsonElement, Path)}.
     *
     * @param codec the codec used for encoding
     * @param value the value to encode
     * @param path  the path to save the resulting file to
     * @param <T>   the value type
     * @return if the file was saved successfully
     */
    public static <T> boolean save(Codec<T> codec, T value, Path path) {
        path.toFile().getParentFile().mkdirs();
        try (JsonWriter jsonWriter = new JsonWriter(new FileWriter(path.toFile()))) {
            jsonWriter.setSerializeNulls(false);
            jsonWriter.setIndent("  ");
            JsonElement jsonElement = codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow();
            GsonHelper.writeValue(jsonWriter, jsonElement, null);
            return true;
        } catch (Exception e) {
            PuzzlesLib.LOGGER.error("Failed to write file at {}", path, e);
            return false;
        }
    }

    /**
     * Load a value from a json file at a provided path.
     * <p>
     * If the file is absent it is created by saving the supplied value.
     *
     * @param codec the codec used for decoding &amp; encoding operations
     * @param value the value to encode
     * @param path  the path to save &amp; load the file at
     * @param <T>   the value type
     * @return the loaded value
     */
    public static <T> T load(Codec<T> codec, Supplier<T> value, Path path) {
        saveIfAbsent(codec, value, path);
        return load(codec, path).orElseGet(value);
    }

    /**
     * Load a value from a json file at a provided path.
     *
     * @param codec the codec used for decoding
     * @param path  the path to load the file from
     * @param <T>   the value type
     * @return the loaded value
     */
    public static <T> Optional<T> load(Codec<T> codec, Path path) {
        try (FileReader fileReader = new FileReader(path.toFile())) {
            JsonElement jsonElement = GsonHelper.fromJson(GSON, fileReader, JsonElement.class);
            return codec.parse(JsonOps.INSTANCE, jsonElement).resultOrPartial();
        } catch (Exception e) {
            PuzzlesLib.LOGGER.error("Failed to read file at {}", path, e);
            return Optional.empty();
        }
    }
}
