package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.core.v1.Proxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.function.Function;
import java.util.function.IntFunction;

public interface FabricProxy extends Proxy {

    <T extends MessageV2<T>> void registerLegacyClientReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory);

    <T extends MessageV2<T>> void registerLegacyServerReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory);
    
    <T extends Record & ClientboundMessage<T>> void registerClientReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory);

    <T extends Record & ServerboundMessage<T>> void registerServerReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory);

    void startClientPrediction(Level level, IntFunction<Packet<ServerGamePacketListener>> predictiveAction);
}
