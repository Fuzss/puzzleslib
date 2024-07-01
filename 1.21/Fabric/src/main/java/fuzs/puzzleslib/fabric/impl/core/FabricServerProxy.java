package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.function.Function;

public class FabricServerProxy implements FabricProxy {

    @Override
    public <T extends MessageV2<T>> void registerLegacyClientReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory) {
        // NO-OP
    }

    @Override
    public <T extends MessageV2<T>> void registerLegacyServerReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory) {
        ServerPlayNetworking.registerGlobalReceiver(channelName, (MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) -> {
            T message = factory.apply(buf);
            server.execute(() -> message.makeHandler().handle(message, player, server));
        });
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory) {
        // NO-OP
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> void registerServerReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory) {
        ServerPlayNetworking.registerGlobalReceiver(channelName, (MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) -> {
            T message = factory.apply(buf);
            server.execute(() -> message.getHandler().handle(message, server, handler, player, player.serverLevel()));
        });

        ServerPlayNetworking.registerGlobalReceiver(channelName, );
    }

    public <T extends Record & ServerboundMessage<T>> void registerServerReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory) {
        ServerPlayNetworking.registerGlobalReceiver(channelName, (MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) -> {
            T message = factory.apply(buf);
            server.execute(() -> message.getHandler().handle(message, server, handler, player, player.serverLevel()));
        });

        ServerPlayNetworking.registerGlobalReceiver(channelName, );
    }
}
