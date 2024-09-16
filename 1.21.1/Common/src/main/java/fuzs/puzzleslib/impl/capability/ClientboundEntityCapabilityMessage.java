package fuzs.puzzleslib.impl.capability;

import com.mojang.serialization.DataResult;
import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityKey;
import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record ClientboundEntityCapabilityMessage(ResourceLocation identifier,
                                                 int entityId,
                                                 CompoundTag tag) implements ClientboundMessage<ClientboundEntityCapabilityMessage> {

    @Override
    public ClientMessageListener<ClientboundEntityCapabilityMessage> getHandler() {
        return new ClientMessageListener<>() {

            @Override
            public void handle(ClientboundEntityCapabilityMessage message, Minecraft minecraft, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
                Entity entity = level.getEntity(message.entityId);
                if (entity != null) {
                    CapabilityKey<?, ?> capabilityKey = CapabilityController.get(message.identifier);
                    if (capabilityKey.isProvidedBy(entity)) {
                        this.setCapabilityComponent(message.tag, entity, capabilityKey, handler.registryAccess());
                    }
                }
            }

            @SuppressWarnings("unchecked")
            private <T extends Entity, C extends CapabilityComponent<T>> void setCapabilityComponent(CompoundTag tag, Entity entity, CapabilityKey<?, ?> capabilityKey, HolderLookup.Provider registries) {
                ((DataResult<C>) capabilityKey.codec()
                        .parse(registries.createSerializationContext(NbtOps.INSTANCE),
                                tag
                        )).ifSuccess((C capabilityComponent) -> {
                    ((CapabilityKey<T, C>) capabilityKey).set((T) entity, capabilityComponent);
                });
            }
        };
    }
}