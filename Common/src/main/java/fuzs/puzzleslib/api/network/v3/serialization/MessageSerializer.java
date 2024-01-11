package fuzs.puzzleslib.api.network.v3.serialization;

import net.minecraft.network.FriendlyByteBuf;

/**
 * A serializer for an arbitrary data type to be sent in networking packets.
 *
 * @param <T> The data type
 */
public interface MessageSerializer<T> {

    /**
     * Serialize an instance to a given byte buffer.
     *
     * @param buf byte buffer to write to
     * @param instance instance to serialize
     */
    void write(final FriendlyByteBuf buf, T instance);

    /**
     * Read a data instance from a byte buffer.
     *
     * @param buf byte buffer to read from
     * @return read data instance
     */
    T read(final FriendlyByteBuf buf);
}
