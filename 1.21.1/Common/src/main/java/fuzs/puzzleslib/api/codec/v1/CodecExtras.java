package fuzs.puzzleslib.api.codec.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Additional codecs similar to {@link ExtraCodecs}.
 */
@Deprecated
public final class CodecExtras {
    /**
     * A codec version of the serialization methods in {@link net.minecraft.world.ContainerHelper}.
     */
    public static final Codec<NonNullList<ItemStack>> NON_NULL_ITEM_STACK_LIST_CODEC = fuzs.puzzleslib.api.util.v1.CodecExtras.NON_NULL_ITEM_STACK_LIST_CODEC;

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
        return fuzs.puzzleslib.api.util.v1.CodecExtras.nonNullList(codec, filter, defaultValue);
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
        return fuzs.puzzleslib.api.util.v1.CodecExtras.mapCompoundTag();
    }
}
