package fuzs.puzzleslib.api.network.v3.codec;

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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.time.Instant;
import java.util.BitSet;
import java.util.Date;

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
            FriendlyByteBuf::readDate
    );
    /**
     * {@link Instant} stream codec
     */
    public static final StreamCodec<FriendlyByteBuf, Instant> INSTANT = StreamCodec.of(FriendlyByteBuf::writeInstant,
            FriendlyByteBuf::readInstant
    );
    /**
     * {@link ChunkPos} stream codec
     */
    public static final StreamCodec<FriendlyByteBuf, ChunkPos> CHUNK_POS = StreamCodec.of(FriendlyByteBuf::writeChunkPos,
            FriendlyByteBuf::readChunkPos
    );
    /**
     * {@link BlockHitResult} stream codec
     */
    public static final StreamCodec<FriendlyByteBuf, BlockHitResult> BLOCK_HIT_RESULT = StreamCodec.of(FriendlyByteBuf::writeBlockHitResult,
            FriendlyByteBuf::readBlockHitResult
    );
    /**
     * {@link BitSet} stream codec
     */
    public static final StreamCodec<FriendlyByteBuf, BitSet> BIT_SET = StreamCodec.of(FriendlyByteBuf::writeBitSet,
            FriendlyByteBuf::readBitSet
    );
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
            }
    );
    /**
     * {@link Vec3} stream codec
     */
    public static final StreamCodec<ByteBuf, Vec3> VEC3 = StreamCodec.composite(ByteBufCodecs.DOUBLE,
            Vec3::x,
            ByteBufCodecs.DOUBLE,
            Vec3::y,
            ByteBufCodecs.DOUBLE,
            Vec3::z,
            Vec3::new
    );
    /**
     * {@link Vector3f} stream codec
     */
    public static final StreamCodec<ByteBuf, Vector3f> VECTOR3F = StreamCodec.composite(ByteBufCodecs.FLOAT,
            Vector3f::x,
            ByteBufCodecs.FLOAT,
            Vector3f::y,
            ByteBufCodecs.FLOAT,
            Vector3f::z,
            Vector3f::new
    );
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
     * read {@link Component}
     */
    public static Component readComponent(FriendlyByteBuf buf) {
        return ComponentSerialization.TRUSTED_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf);
    }

    /**
     * write {@link Component}
     */
    public static void writeComponent(FriendlyByteBuf buf, Component component) {
        ComponentSerialization.TRUSTED_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, component);
    }
}
