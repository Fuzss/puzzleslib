package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.capability.CapabilityController;
import fuzs.puzzleslib.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class S2CSyncCapabilityMessage implements Message<S2CSyncCapabilityMessage> {
    private ResourceLocation id;
    private int holderId;
    private CompoundTag tag;

    public S2CSyncCapabilityMessage() {

    }

    public S2CSyncCapabilityMessage(ResourceLocation id, Entity holder, CompoundTag tag) {
        this.id = id;
        this.holderId = holder.getId();
        this.tag = tag;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.id);
        buf.writeInt(this.holderId);
        buf.writeNbt(this.tag);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.id = buf.readResourceLocation();
        this.holderId = buf.readInt();
        this.tag = buf.readNbt();
    }

    @Override
    public MessageHandler<S2CSyncCapabilityMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(S2CSyncCapabilityMessage message, Player player, Object gameInstance) {
                Level level = ((Minecraft) gameInstance).level;
                Entity holder = level.getEntity(message.holderId);
                if (holder != null) {
                    CapabilityController.retrieve(message.id).orThrow(holder).read(message.tag);
                }
            }
        };
    }
}