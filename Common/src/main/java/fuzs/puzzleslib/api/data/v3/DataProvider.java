package fuzs.puzzleslib.api.data.v3;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Copied from Minecraft 26.2.
 */
public interface DataProvider {
    static <T> CompletableFuture<?> saveAll(CachedOutput cache, Codec<T> codec, PackOutput.PathProvider pathProvider, Map<ResourceLocation, T> entries) {
        Objects.requireNonNull(pathProvider);
        return saveAll(cache, codec, pathProvider::json, entries);
    }

    static <T, E> CompletableFuture<?> saveAll(CachedOutput cache, Codec<E> codec, Function<T, Path> pathGetter, Map<T, E> contents) {
        return saveAll(cache, (E e) -> codec.encodeStart(JsonOps.INSTANCE, e).getOrThrow(), pathGetter, contents);
    }

    static <T, E> CompletableFuture<?> saveAll(CachedOutput cache, Function<E, JsonElement> serializer, Function<T, Path> pathGetter, Map<T, E> contents) {
        return CompletableFuture.allOf(contents.entrySet().stream().map((Map.Entry<T, E> entry) -> {
            Path path = pathGetter.apply(entry.getKey());
            JsonElement json = serializer.apply(entry.getValue());
            return net.minecraft.data.DataProvider.saveStable(cache, json, path);
        }).toArray(CompletableFuture[]::new));
    }

    static <T> CompletableFuture<?> saveStable(CachedOutput cache, HolderLookup.Provider registries, Codec<T> codec, T value, Path path) {
        RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);
        return saveStable(cache, ops, codec, value, path);
    }

    static <T> CompletableFuture<?> saveStable(CachedOutput cache, Codec<T> codec, T value, Path path) {
        return saveStable(cache, JsonOps.INSTANCE, codec, value, path);
    }

    private static <T> CompletableFuture<?> saveStable(CachedOutput cache, DynamicOps<JsonElement> ops, Codec<T> codec, T value, Path path) {
        JsonElement json = codec.encodeStart(ops, value).getOrThrow();
        return net.minecraft.data.DataProvider.saveStable(cache, json, path);
    }
}
