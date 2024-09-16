package fuzs.puzzleslib.impl.attachment;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record ClientboundEntityDataAttachmentMessage<A>(Type<ClientboundEntityDataAttachmentMessage<A>> messageType,
                                                        int entityId,
                                                        @Nullable A value) implements CustomPacketPayload {

    public static <A> StreamCodec<? super RegistryFriendlyByteBuf, ClientboundEntityDataAttachmentMessage<A>> streamCodec(Type<ClientboundEntityDataAttachmentMessage<A>> type, StreamCodec<? super RegistryFriendlyByteBuf, A> valueStreamCodec) {
        return StreamCodec.composite(ByteBufCodecs.VAR_INT,
                ClientboundEntityDataAttachmentMessage::entityId,
                valueStreamCodec,
                ClientboundEntityDataAttachmentMessage::value,
                (Integer entityId, A value) -> {
                    return new ClientboundEntityDataAttachmentMessage<>(type, entityId, value);
                }
        );
    }

    @Override
    public Type<ClientboundEntityDataAttachmentMessage<A>> type() {
        return this.messageType;
    }
}
