package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.mixin.client.accessor.MultiPlayerGameModeFabricAccessor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

public class FabricClientProxy extends FabricServerProxy implements ClientProxyImpl {

    @Override
    public <T extends MessageV2<T>> void registerLegacyClientReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory) {
        ClientPlayNetworking.registerGlobalReceiver(channelName, (Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) -> {
            T message = factory.apply(buf);
            client.execute(() -> message.makeHandler().handle(message, client.player, client));
        });
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory) {
        ClientPlayNetworking.registerGlobalReceiver(channelName, (Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) -> {
            T message = factory.apply(buf);
            client.execute(() -> {
                LocalPlayer player = client.player;
                Objects.requireNonNull(player, "player is null");
                message.getHandler().handle(message, client, handler, player, client.level);
            });
        });
    }

    @Override
    public boolean shouldStartDestroyBlock(BlockPos blockPos) {
        MultiPlayerGameModeFabricAccessor gameMode = (MultiPlayerGameModeFabricAccessor) Minecraft.getInstance().gameMode;
        return !gameMode.puzzleslib$getIsDestroying() || !gameMode.puzzleslib$callSameDestroyTarget(blockPos);
    }

    @Override
    public void startClientPrediction(Level level, IntFunction<Packet<ServerGamePacketListener>> predictiveAction) {
        ((MultiPlayerGameModeFabricAccessor) Minecraft.getInstance().gameMode).puzzleslib$callStartPrediction((ClientLevel) level, predictiveAction::apply);
    }
}
