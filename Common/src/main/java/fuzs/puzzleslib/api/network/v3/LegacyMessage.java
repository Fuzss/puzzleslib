package fuzs.puzzleslib.api.network.v3;

import fuzs.puzzleslib.api.network.v4.message.Message;
import fuzs.puzzleslib.impl.network.StreamCodecRegistryImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Network message template providing a handler that runs when the message is received.
 *
 * @param <T1> the message listener type
 */
public interface LegacyMessage<T1, T2 extends Message.Context<?>> extends Message<T2> {

    /**
     * Automatically create a {@link StreamCodec} for the provided message class. The message implementation must be a
     * {@link Record}.
     *
     * @param clazz the message class type
     * @param <B>   the {@link io.netty.buffer.ByteBuf} type
     * @param <V>   the message type
     * @return the stream codec
     */
    static <B extends FriendlyByteBuf, V extends Record & LegacyMessage<?, ?>> StreamCodec<B, V> streamCodec(Class<V> clazz) {
        return StreamCodecRegistryImpl.fromType(clazz);
    }

    /**
     * Create a handler for this message, usually {@link ClientMessageListener} or {@link ServerMessageListener}.
     *
     * @return the handler instance
     */
    T1 getHandler();
}
