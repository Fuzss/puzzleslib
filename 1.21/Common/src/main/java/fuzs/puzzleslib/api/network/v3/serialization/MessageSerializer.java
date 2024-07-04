package fuzs.puzzleslib.api.network.v3.serialization;

import fuzs.puzzleslib.impl.network.CustomPacketPayloadAdapterImpl;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * A serializer for an arbitrary data type to be sent in networking packets.
 *
 * @param <T> The data type
 */
public interface MessageSerializer<T> extends StreamEncoder<FriendlyByteBuf, T>, StreamDecoder<FriendlyByteBuf, T> {

    /**
     * Serialize an instance to a byte buffer.
     *
     * @param buf byte buffer to write to
     * @param instance instance to serialize
     */
    @Override
    void encode(FriendlyByteBuf buf, T instance);

    /**
     * Deserialize an instance from a byte buffer.
     *
     * @param buf byte buffer to read from
     * @return read data instance
     */
    @Override
    T decode(FriendlyByteBuf buf);

    /**
     * Converts this serializer to a {@link StreamCodec} instance.
     *
     * @param type custom packet payload type used for registering this serializer
     * @return the stream codec
     */
    default StreamCodec<? super RegistryFriendlyByteBuf, CustomPacketPayloadAdapter<T>> streamCodec(CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type) {
        return CustomPacketPayload.codec((CustomPacketPayloadAdapter<T> adapter, RegistryFriendlyByteBuf buf) -> {
            this.encode(buf, adapter.unwrap());
        }, (RegistryFriendlyByteBuf buf) -> {
            return new CustomPacketPayloadAdapterImpl<>(type, this.decode(buf));
        });
    }

    /**
     * Converts a stream codec to a new message serializer.
     *
     * @param streamCodec the stream codec
     * @param <B>         the byte buffer type
     * @param <V>         the value type
     * @return the new message serializer
     */
    static <B extends ByteBuf, V> MessageSerializer<V> fromStreamCodec(StreamCodec<? super B, V> streamCodec) {
        return new MessageSerializers.MessageSerializerImpl<>((FriendlyByteBuf buf, V v) -> {
            streamCodec.encode((B) buf, v);
        }, (FriendlyByteBuf buf) -> {
            return streamCodec.decode((B) buf);
        });
    }
}
