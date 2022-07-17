package fuzs.puzzleslib.core;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface RegistryAccess<T> extends Iterable<T> {

    ResourceKey<? extends Registry<T>> key();

    @Nullable
    ResourceLocation getKey(T object);

    Optional<ResourceKey<T>> getResourceKey(T object);

    int getId(@Nullable T object);

    @Nullable
    T get(@Nullable ResourceKey<T> resourceKey);

    @Nullable
    T get(@Nullable ResourceLocation resourceLocation);

    default Optional<T> getOptional(@Nullable ResourceLocation resourceLocation) {
        return Optional.ofNullable(this.get(resourceLocation));
    }

    default Optional<T> getOptional(@Nullable ResourceKey<T> resourceKey) {
        return Optional.ofNullable(this.get(resourceKey));
    }

    default T getOrThrow(ResourceKey<T> resourceKey) {
        T object = this.get(resourceKey);
        if (object == null) {
            throw new IllegalStateException("Missing key in " + this.key() + ": " + resourceKey);
        } else {
            return object;
        }
    }

    Set<ResourceLocation> keySet();

    Set<Map.Entry<ResourceKey<T>, T>> entrySet();

    Set<ResourceKey<T>> registryKeySet();

    Optional<Holder<T>> getRandom(RandomSource randomSource);

    default Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    default Stream<T> parallelStream() {
        return StreamSupport.stream(this.spliterator(), true);
    }

    boolean containsKey(ResourceLocation resourceLocation);

    boolean containsKey(ResourceKey<T> resourceKey);

    Holder<T> getOrCreateHolderOrThrow(ResourceKey<T> resourceKey);

    DataResult<Holder<T>> getOrCreateHolder(ResourceKey<T> resourceKey);

    Holder.Reference<T> createIntrusiveHolder(T object);

    Optional<Holder<T>> getHolder(int i);

    Optional<Holder<T>> getHolder(ResourceKey<T> resourceKey);

    Holder<T> getHolderOrThrow(ResourceKey<T> resourceKey);

    Stream<Holder.Reference<T>> holders();

    Optional<HolderSet.Named<T>> getTag(TagKey<T> tagKey);

    Iterable<Holder<T>> getTagOrEmpty(TagKey<T> tagKey);

    HolderSet.Named<T> getOrCreateTag(TagKey<T> tagKey);

    Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags();

    Stream<TagKey<T>> getTagNames();

    boolean isKnownTagName(TagKey<T> tagKey);

    void resetTags();

    void bindTags(Map<TagKey<T>, List<Holder<T>>> map);
}
