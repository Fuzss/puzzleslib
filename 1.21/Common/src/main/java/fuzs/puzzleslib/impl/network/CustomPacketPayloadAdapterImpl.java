package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.network.v3.serialization.CustomPacketPayloadAdapter;

public record CustomPacketPayloadAdapterImpl<T>(Type<CustomPacketPayloadAdapter<T>> type,
                                                T message) implements CustomPacketPayloadAdapter<T> {

    @Override
    public T unwrap() {
        return this.message;
    }
}
