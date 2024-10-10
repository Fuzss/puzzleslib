package fuzs.puzzleslib.impl.attachment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Optional;

public record ClientboundEntityDataAttachmentMessage<V>(Type<ClientboundEntityDataAttachmentMessage<V>> messageType,
                                                        int entityId,
                                                        Optional<V> value) implements CustomPacketPayload {

    public static <V> StreamCodec<? super RegistryFriendlyByteBuf, ClientboundEntityDataAttachmentMessage<V>> streamCodec(Type<ClientboundEntityDataAttachmentMessage<V>> type, StreamCodec<? super RegistryFriendlyByteBuf, V> valueStreamCodec) {
        return StreamCodec.composite(ByteBufCodecs.VAR_INT,
                ClientboundEntityDataAttachmentMessage::entityId,
                ByteBufCodecs.optional((StreamCodec<ByteBuf, V>) valueStreamCodec),
                ClientboundEntityDataAttachmentMessage::value,
                (Integer entityId, Optional<V> value) -> {
                    return new ClientboundEntityDataAttachmentMessage<>(type, entityId, value);
                }
        );
    }

    @Override
    public Type<ClientboundEntityDataAttachmentMessage<V>> type() {
        return this.messageType;
    }
}
