package fuzs.puzzleslib.impl.capability.v2;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record ClientboundSyncCapabilityMessage(ResourceLocation id, int holderId, CompoundTag tag) implements ClientboundMessage<ClientboundSyncCapabilityMessage> {

    public ClientboundSyncCapabilityMessage(ResourceLocation id, Entity holder, CompoundTag tag) {
        this(id, holder.getId(), tag);
    }

    @Override
    public ClientMessageListener<ClientboundSyncCapabilityMessage> getHandler() {
        return new ClientMessageListener<>() {

            @Override
            public void handle(ClientboundSyncCapabilityMessage message, Minecraft client, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
                Entity holder = level.getEntity(message.holderId);
                if (holder != null) {
                    CapabilityController.retrieve(message.id).orThrow(holder).read(message.tag);
                }
            }
        };
    }
}