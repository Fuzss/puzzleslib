package fuzs.puzzleslib.api.codec.v1;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Additional codecs similar to {@link ExtraCodecs}.
 */
public final class CodecExtras {
    /**
     * A codec version of the serialization methods in {@link net.minecraft.world.ContainerHelper}.
     */
    public static final Codec<NonNullList<ItemStack>> NON_NULL_ITEM_STACK_LIST_CODEC = nonNullList(ItemStack.CODEC,
            Predicate.not(ItemStack::isEmpty), ItemStack.EMPTY
    );

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
                                return IntStream.range(0, items.size()).mapToObj(
                                        index -> new Pair<>(index, items.get(index))).filter(
                                        pair -> filter.test(pair.getSecond())).toList();
                            })
            ).apply(instance, (Integer size, List<Pair<Integer, T>> items) -> {
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
            return tag instanceof CompoundTag compoundTag ? DataResult.success(compoundTag) : DataResult.error(
                    () -> "Not a compound tag: " + tag);
        };
    }
}
