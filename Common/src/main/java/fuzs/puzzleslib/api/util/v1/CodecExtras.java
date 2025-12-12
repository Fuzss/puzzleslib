package fuzs.puzzleslib.api.util.v1;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Additional codecs similar to {@link ExtraCodecs}.
 */
public final class CodecExtras {
    /**
     * A codec version of the serialization methods in {@link net.minecraft.world.ContainerHelper}.
     */
    public static final Codec<NonNullList<ItemStack>> NON_NULL_ITEM_STACK_LIST_CODEC = nonNullList(ItemStack.CODEC,
            Predicate.not(ItemStack::isEmpty),
            ItemStack.EMPTY);
    /**
     * Adapted from {@link net.minecraft.nbt.CompoundTag#CODEC}.
     */
    public static final Codec<ListTag> LIST_TAG_CODEC = Codec.PASSTHROUGH.comapFlatMap((Dynamic<?> dynamic) -> {
        Tag tag = dynamic.convert(NbtOps.INSTANCE).getValue();
        return tag instanceof ListTag listTag ?
                DataResult.success(listTag == dynamic.getValue() ? listTag.copy() : listTag) :
                DataResult.error(() -> "Not a list tag: " + tag);
    }, (ListTag listTag) -> new Dynamic<>(NbtOps.INSTANCE, listTag.copy()));

    private CodecExtras() {
        // NO-OP
    }

    /**
     * Creates a codec for {@link NonNullList}. List elements are stored together with their index.
     *
     * @param codec        the list element codec
     * @param filter       a filter for skipping empty elements, like empty item stacks
     * @param defaultValue the default empty value for the list
     * @param <T>          the list element type
     * @return the codec
     */
    public static <T> Codec<NonNullList<T>> nonNullList(Codec<T> codec, Predicate<T> filter, @Nullable T defaultValue) {
        return RecordCodecBuilder.create(instance -> {
            return instance.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("size").forGetter(NonNullList::size),
                    Codec.mapPair(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("slot"), codec.fieldOf("item"))
                            .codec()
                            .listOf()
                            .fieldOf("items")
                            .forGetter((NonNullList<T> items) -> {
                                return IntStream.range(0, items.size())
                                        .mapToObj(index -> new Pair<>(index, items.get(index)))
                                        .filter(pair -> filter.test(pair.getSecond()))
                                        .toList();
                            })).apply(instance, (Integer size, List<Pair<Integer, T>> items) -> {
                NonNullList<T> nonNullList = defaultValue != null ? NonNullList.withSize(size, defaultValue) :
                        NonNullList.createWithCapacity(size);
                for (Pair<Integer, T> pair : items) {
                    nonNullList.set(pair.getFirst(), pair.getSecond());
                }
                return nonNullList;
            });
        });
    }

    /**
     * A helper for turning results from {@link com.mojang.serialization.Encoder#encode(Object, DynamicOps, Object)} and
     * {@link com.mojang.serialization.Encoder#encodeStart(DynamicOps, Object)} into {@link CompoundTag} when
     * serializing using {@link net.minecraft.nbt.NbtOps}.
     * <p>
     * To be used with {@link Codec#flatMap(Function)}.
     *
     * @return the mapping function
     */
    public static Function<Tag, DataResult<CompoundTag>> mapCompoundTag() {
        return (Tag tag) -> {
            return tag instanceof CompoundTag compoundTag ? DataResult.success(compoundTag) :
                    DataResult.error(() -> "Not a compound tag: " + tag);
        };
    }

    /**
     * Creates a codec for a set of elements. Similar to {@link Codec#list(Codec)}.
     * <p>
     * Copied from {@code net.neoforged.neoforge.common.util.NeoForgeExtraCodecs#setOf}.
     *
     * @param codec the single element codec
     * @param <T>   the list element type
     * @return the list codec
     */
    public static <T> Codec<Set<T>> setOf(Codec<T> codec) {
        return Codec.list(codec).xmap(ImmutableSet::copyOf, ImmutableList::copyOf);
    }

    /**
     * A map codec that allows for key values that are not encoded as {@link net.minecraft.nbt.StringTag}, unlike
     * {@link Codec#unboundedMap(Codec, Codec)}.
     *
     * @param keyCodec   the map key codec
     * @param valueCodec the map value codec
     * @param <K>        the map key type
     * @param <V>        the map value type
     * @return the codec
     */
    public static <K, V> Codec<Map<K, V>> mapOf(Codec<K> keyCodec, Codec<V> valueCodec) {
        return mapOf(keyCodec.fieldOf("key"), valueCodec.fieldOf("value"));
    }

    /**
     * A map codec that allows for key values that are not encoded as {@link net.minecraft.nbt.StringTag}, unlike
     * {@link Codec#unboundedMap(Codec, Codec)}
     *
     * @param keyCodec   the map key codec
     * @param valueCodec the map value codec
     * @param <K>        the map key type
     * @param <V>        the map value type
     * @return the codec
     */
    public static <K, V> Codec<Map<K, V>> mapOf(MapCodec<K> keyCodec, MapCodec<V> valueCodec) {
        return Codec.mapPair(keyCodec, valueCodec).codec().listOf().xmap((List<Pair<K, V>> list) -> {
                    return list.stream()
                            .collect(ImmutableMap.<Pair<K, V>, K, V>toImmutableMap(Pair::getFirst, Pair::getSecond));
                },
                (Map<K, V> map) -> map.entrySet()
                        .stream()
                        .map((Map.Entry<K, V> entry) -> new Pair<>(entry.getKey(), entry.getValue()))
                        .toList());
    }

    /**
     * Creates a codec from a decoder. The returned codec can only decode, and will throw on any attempt to encode.
     * <p>
     * Copied from {@code net.neoforged.neoforge.common.util.NeoForgeExtraCodecs#decodeOnly}.
     *
     * @param decoder the decoder
     * @param <A>     the element type
     * @return the codec
     */
    public static <A> Codec<A> decodeOnly(Decoder<A> decoder) {
        return Codec.of(MapCodec.unitCodec(() -> {
            throw new UnsupportedOperationException("Cannot encode with decode-only codec! Decoder:" + decoder);
        }), decoder, "DecodeOnly[" + decoder + "]");
    }

    /**
     * Create an {@link Enum} codec.
     *
     * @param enumClazz the enum class
     * @param <E>       the enum type
     * @return the codec
     */
    public static <E extends Enum<E>> Codec<E> fromEnum(Class<E> enumClazz) {
        return fromEnum(enumClazz::getEnumConstants);
    }

    /**
     * Create an {@link Enum} codec.
     *
     * @param enumValues the enum values
     * @param <E>        the enum type
     * @return the codec
     */
    public static <E extends Enum<E>> Codec<E> fromEnum(Supplier<E[]> enumValues) {
        return fromEnumWithMapping(enumValues, (E enumConstant) -> enumConstant.name().toLowerCase(Locale.ROOT));
    }

    /**
     * Create an {@link Enum} codec.
     *
     * @param enumValues  the enum values
     * @param keyFunction the string key extractor
     * @param <E>         the enum type
     * @return the codec
     */
    public static <E extends Enum<E>> Codec<E> fromEnumWithMapping(Supplier<E[]> enumValues, Function<E, String> keyFunction) {
        E[] enums = enumValues.get();
        Function<String, E> function = Arrays.stream(enums)
                .collect(ImmutableMap.toImmutableMap(keyFunction, Function.identity()))::get;
        return Codec.stringResolver(keyFunction, function);
    }
}
