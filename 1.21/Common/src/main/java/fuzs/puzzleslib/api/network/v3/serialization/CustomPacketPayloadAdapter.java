package fuzs.puzzleslib.api.network.v3.serialization;

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
}
