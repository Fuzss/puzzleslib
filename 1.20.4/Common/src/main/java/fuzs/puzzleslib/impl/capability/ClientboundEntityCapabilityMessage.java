package fuzs.puzzleslib.impl.capability;

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

public record ClientboundEntityCapabilityMessage(ResourceLocation identifier, int entityId, CompoundTag tag) implements ClientboundMessage<ClientboundEntityCapabilityMessage> {

    @Override
    public ClientMessageListener<ClientboundEntityCapabilityMessage> getHandler() {
        return new ClientMessageListener<>() {

            @Override
            public void handle(ClientboundEntityCapabilityMessage message, Minecraft client, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
                Entity entity = level.getEntity(message.entityId);
                if (entity != null) {
                    CapabilityKey<?, ?> capabilityKey = GlobalCapabilityRegister.get(message.identifier);
                    capabilityKey.getIfProvided(entity).ifPresent(capabilityComponent -> capabilityComponent.read(message.tag));
                }
            }
        };
    }
}