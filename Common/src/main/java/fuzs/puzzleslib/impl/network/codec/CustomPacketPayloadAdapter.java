package fuzs.puzzleslib.impl.network.codec;

import fuzs.puzzleslib.impl.network.CustomPacketPayloadAdapterImpl;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * An extension to {@link CustomPacketPayload} for wrapping a custom object.
 *
 * @param <T> the message type
 */
public interface CustomPacketPayloadAdapter<T> extends CustomPacketPayload {

    @Override
    Type<? extends CustomPacketPayloadAdapter<T>> type();

    /**
     * @return the wrapped object
     */
    T unwrap();

    /**
     * Creates a {@link CustomPacketPayload} compatible {@link StreamCodec}.
     *
     * @param type        the custom packet payload type
     * @param streamCodec the stream codec
     * @param <T>         data type
     * @return the {@link CustomPacketPayload} stream codec
     */
    static <B extends ByteBuf, T> StreamCodec<? super B, CustomPacketPayloadAdapter<T>> streamCodec(CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type, StreamCodec<? super ByteBuf, T> streamCodec) {
        return CustomPacketPayload.codec((CustomPacketPayloadAdapter<T> adapter, ByteBuf buf) -> {
            streamCodec.encode(buf, adapter.unwrap());
        }, (ByteBuf buf) -> {
            return new CustomPacketPayloadAdapterImpl<>(type, streamCodec.decode(buf));
        });
    }
}
