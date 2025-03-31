package fuzs.puzzleslib.api.network.v4.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;

/**
 * A simplified message that allows for manually writing to and reading from a provided
 * {@link io.netty.buffer.ByteBuf}.
 *
 * @param <B> the {@link io.netty.buffer.ByteBuf} type
 */
public interface WritableMessage<B extends FriendlyByteBuf> {

    /**
     * Create a simple {@link StreamCodec} for the message.
     *
     * @param streamDecoder the message constructor accepting a single {@link io.netty.buffer.ByteBuf} argument
     * @param <B>the        {@link io.netty.buffer.ByteBuf} type
     * @param <V>           the message type
     * @return the stream codec
     */
    static <B extends FriendlyByteBuf, V extends WritableMessage<B>> StreamCodec<B, V> streamCodec(StreamDecoder<B, V> streamDecoder) {
        return StreamCodec.ofMember(V::write, streamDecoder);
    }

    /**
     * Serialize the message to {@link io.netty.buffer.ByteBuf}.
     *
     * @param buf the byte buffer
     */
    void write(B buf);
}
