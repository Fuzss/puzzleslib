package fuzs.puzzleslib.api.network.v4.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.time.Instant;
import java.util.BitSet;
import java.util.Date;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * A few additional more or less useful stream codecs.
 */
public final class ExtraStreamCodecs {
    /**
     * {@link Character} stream codec
     */
    public static final StreamCodec<ByteBuf, Character> CHAR = StreamCodec.of((ByteBuf buf, Character character) -> {
        buf.writeChar(character);
    }, ByteBuf::readChar);
    /**
     * {@link Date} stream codec
     */
    public static final StreamCodec<FriendlyByteBuf, Date> DATE = StreamCodec.of(FriendlyByteBuf::writeDate,
            FriendlyByteBuf::readDate);
    /**
     * {@link Instant} stream codec
     */
    public static final StreamCodec<FriendlyByteBuf, Instant> INSTANT = StreamCodec.of(FriendlyByteBuf::writeInstant,
            FriendlyByteBuf::readInstant);
    /**
     * {@link BlockHitResult} stream codec
     */
    public static final StreamCodec<FriendlyByteBuf, BlockHitResult> BLOCK_HIT_RESULT = StreamCodec.of(FriendlyByteBuf::writeBlockHitResult,
            FriendlyByteBuf::readBlockHitResult);
    /**
     * {@link BitSet} stream codec
     */
    public static final StreamCodec<FriendlyByteBuf, BitSet> BIT_SET = StreamCodec.of(FriendlyByteBuf::writeBitSet,
            FriendlyByteBuf::readBitSet);
    /**
     * {@link BlockState} stream codec
     */
    public static final StreamCodec<ByteBuf, BlockState> BLOCK_STATE = ByteBufCodecs.VAR_INT.map(Block::stateById,
            Block::getId);

    private ExtraStreamCodecs() {
        // NO-OP
    }

    /**
     * Create an {@link Enum} stream codec.
     *
     * @param clazz the enum class
     * @param <E>   the enum type
     * @return the stream codec
     */
    public static <E extends Enum<E>> StreamCodec<ByteBuf, E> fromEnum(Class<E> clazz) {
        return fromEnum(clazz::getEnumConstants);
    }

    /**
     * Create an {@link Enum} stream codec.
     *
     * @param enumValues the enum values
     * @param <E>        the enum type
     * @return the stream codec
     */
    public static <E extends Enum<E>> StreamCodec<ByteBuf, E> fromEnum(Supplier<E[]> enumValues) {
        return fromEnum(enumValues, E::ordinal);
    }

    /**
     * Create an {@link Enum} stream codec.
     *
     * @param enumValues   the enum values
     * @param keyExtractor the numeric key extractor
     * @param <E>          the enum type
     * @return the stream codec
     */
    public static <E extends Enum<E>> StreamCodec<ByteBuf, E> fromEnum(Supplier<E[]> enumValues, ToIntFunction<E> keyExtractor) {
        E[] enums = enumValues.get();
        IntFunction<E> idMapper = ByIdMap.continuous(keyExtractor, enums, ByIdMap.OutOfBoundsStrategy.ZERO);
        return ByteBufCodecs.idMapper(idMapper, keyExtractor);
    }
}
