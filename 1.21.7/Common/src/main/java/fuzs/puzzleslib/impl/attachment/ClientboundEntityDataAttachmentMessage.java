package fuzs.puzzleslib.impl.attachment;

import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public record ClientboundEntityDataAttachmentMessage<V>(AttachmentTypeAdapter<Entity, V> attachmentType,
                                                        Type<ClientboundEntityDataAttachmentMessage<V>> payloadType,
                                                        int entityId,
                                                        Optional<V> value) implements ClientboundPlayMessage {

    public static <V> StreamCodec<? super RegistryFriendlyByteBuf, ClientboundEntityDataAttachmentMessage<V>> streamCodec(AttachmentTypeAdapter<Entity, V> attachmentType, Type<ClientboundEntityDataAttachmentMessage<V>> payloadType, StreamCodec<? super RegistryFriendlyByteBuf, V> valueStreamCodec) {
        return StreamCodec.composite(ByteBufCodecs.VAR_INT,
                ClientboundEntityDataAttachmentMessage::entityId,
                ByteBufCodecs.optional((StreamCodec<ByteBuf, V>) valueStreamCodec),
                ClientboundEntityDataAttachmentMessage::value,
                (Integer entityId, Optional<V> value) -> {
                    return new ClientboundEntityDataAttachmentMessage<>(attachmentType, payloadType, entityId, value);
                });
    }

    @Override
    public Type<ClientboundEntityDataAttachmentMessage<V>> type() {
        return this.payloadType;
    }

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<>() {
            @Override
            public void accept(Context context) {
                Entity entity = context.level().getEntity(ClientboundEntityDataAttachmentMessage.this.entityId());
                if (entity != null) {
                    if (ClientboundEntityDataAttachmentMessage.this.value().isPresent()) {
                        ClientboundEntityDataAttachmentMessage.this.attachmentType()
                                .setData(entity, ClientboundEntityDataAttachmentMessage.this.value().get());
                    } else {
                        ClientboundEntityDataAttachmentMessage.this.attachmentType().removeData(entity);
                    }
                }
            }
        };
    }
}
