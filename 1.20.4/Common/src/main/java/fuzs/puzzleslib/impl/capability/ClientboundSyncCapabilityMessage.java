package fuzs.puzzleslib.impl.capability;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityKey;
import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record ClientboundSyncCapabilityMessage(ResourceLocation identifier, int entityId, CompoundTag tag) implements ClientboundMessage<ClientboundSyncCapabilityMessage> {

    @Override
    public ClientMessageListener<ClientboundSyncCapabilityMessage> getHandler() {
        return new ClientMessageListener<>() {

            @Override
            public void handle(ClientboundSyncCapabilityMessage message, Minecraft client, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
                Entity entity = level.getEntity(message.entityId);
                if (entity != null) {
                    CapabilityKey<?, ?> capabilityKey = GlobalCapabilityRegister.retrieve(message.identifier);
                    if (capabilityKey.isProvidedBy(entity)) {
                        this.getCapabilityComponent(capabilityKey, entity).read(message.tag);
                    }
                }
            }

            private <T extends Entity> CapabilityComponent<T> getCapabilityComponent(CapabilityKey<?, ?> capabilityKey, Entity entity) {
                return ((CapabilityKey<T, ?>) capabilityKey).get((T) entity);
            }
        };
    }
}