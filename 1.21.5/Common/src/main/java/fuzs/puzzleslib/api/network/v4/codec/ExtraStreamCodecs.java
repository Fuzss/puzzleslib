package fuzs.puzzleslib.api.network.v4.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.time.Instant;
import java.util.BitSet;
import java.util.Date;
import java.util.function.IntFunction;
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
     * {@link ChunkPos} stream codec
     */
    public static final StreamCodec<ByteBuf, ChunkPos> CHUNK_POS = ChunkPos.STREAM_CODEC;
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
     * {@link ResourceKey} stream codec
     */
    public static final StreamCodec<ByteBuf, ResourceKey<?>> DIRECT_RESOURCE_KEY = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ResourceKey::registry,
            ResourceLocation.STREAM_CODEC,
            ResourceKey::location,
            (ResourceLocation registry, ResourceLocation location) -> {
                return ResourceKey.create(ResourceKey.createRegistryKey(registry), location);
            });
    /**
     * {@link Vec3} stream codec
     */
    public static final StreamCodec<ByteBuf, Vec3> VEC3 = Vec3.STREAM_CODEC;
    /**
     * {@link Vector3f} stream codec
     */
    public static final StreamCodec<ByteBuf, Vector3f> VECTOR3F = ByteBufCodecs.VECTOR3F;
    /**
     * {@link FriendlyByteBuf} stream codec
     */
    public static final StreamCodec<FriendlyByteBuf, FriendlyByteBuf> FRIENDLY_BYTE_BUF = new StreamCodec<>() {

        @Override
        public FriendlyByteBuf decode(FriendlyByteBuf buf) {
            FriendlyByteBuf newBuf = new FriendlyByteBuf(Unpooled.buffer());
            newBuf.writeBytes(buf.copy());
            buf.skipBytes(buf.readableBytes());
            return newBuf;
        }

        @Override
        public void encode(FriendlyByteBuf buf, FriendlyByteBuf toEncode) {
            buf.writeBytes(toEncode.copy());
            toEncode.release();
        }
    };
    /**
     * {@link RegistryFriendlyByteBuf} stream codec
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, RegistryFriendlyByteBuf> REGISTRY_FRIENDLY_BYTE_BUF = new StreamCodec<>() {

        @Override
        public RegistryFriendlyByteBuf decode(RegistryFriendlyByteBuf buf) {
            RegistryFriendlyByteBuf newBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), buf.registryAccess());
            newBuf.writeBytes(buf.copy());
            buf.skipBytes(buf.readableBytes());
            return newBuf;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, RegistryFriendlyByteBuf toEncode) {
            buf.writeBytes(toEncode.copy());
            toEncode.release();
        }
    };

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
        return fromEnum(clazz, E::ordinal);
    }

    /**
     * Create an {@link Enum} stream codec.
     *
     * @param clazz        the enum class
     * @param keyExtractor the numeric key extractor
     * @param <E>          the enum type
     * @return the stream codec
     */
    public static <E extends Enum<E>> StreamCodec<ByteBuf, E> fromEnum(Class<E> clazz, ToIntFunction<E> keyExtractor) {
        IntFunction<E> idMapper = ByIdMap.continuous(keyExtractor,
                clazz.getEnumConstants(),
                ByIdMap.OutOfBoundsStrategy.ZERO);
        return ByteBufCodecs.idMapper(idMapper, keyExtractor);
    }

    /**
     * read {@link Component}
     */
    @Deprecated
    public static Component readComponent(RegistryFriendlyByteBuf buf) {
        return ComponentSerialization.TRUSTED_STREAM_CODEC.decode(buf);
    }

    /**
     * write {@link Component}
     */
    @Deprecated
    public static void writeComponent(RegistryFriendlyByteBuf buf, Component component) {
        ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, component);
    }

    /**
     * read {@link ItemStack}
     */
    @Deprecated
    public static ItemStack readItem(RegistryFriendlyByteBuf buf) {
        return ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
    }

    /**
     * write {@link ItemStack}
     */
    @Deprecated
    public static void writeItem(RegistryFriendlyByteBuf buf, ItemStack itemStack) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemStack);
    }
}
