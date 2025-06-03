package fuzs.puzzleslib.impl.capability;

import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityKey;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record ClientboundEntityCapabilityMessage(ResourceLocation resourceLocation,
                                                 int entityId,
                                                 CompoundTag compoundTag) implements ClientboundPlayMessage {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundEntityCapabilityMessage> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ClientboundEntityCapabilityMessage::resourceLocation,
            ByteBufCodecs.VAR_INT,
            ClientboundEntityCapabilityMessage::entityId,
            ByteBufCodecs.TRUSTED_COMPOUND_TAG,
            ClientboundEntityCapabilityMessage::compoundTag,
            ClientboundEntityCapabilityMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<>() {
            @Override
            public void accept(Context context) {
                Entity entity = context.level().getEntity(ClientboundEntityCapabilityMessage.this.entityId);
                if (entity != null) {
                    CapabilityKey<?, ?> capabilityKey = CapabilityController.get(ClientboundEntityCapabilityMessage.this.resourceLocation);
                    capabilityKey.getIfProvided(entity)
                            .ifPresent(capabilityComponent -> capabilityComponent.read(
                                    ClientboundEntityCapabilityMessage.this.compoundTag,
                                    context.packetListener().registryAccess()));
                }
            }
        };
    }
}