package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class FabricServerProxy implements FabricProxy {
    private MinecraftServer gameServer;

    public FabricServerProxy() {
        // registers for game server starting and stopping, so we can keep an instance of the server here so that
        // {@link FabricNetworkHandler} can be implemented much more similarly to Forge
        ServerLifecycleEvents.STARTING.register(EventPhase.FIRST, server -> this.gameServer = server);
        ServerLifecycleEvents.STOPPED.register(EventPhase.LAST, server -> this.gameServer = null);
    }

    @Override
    public Player getClientPlayer() {
        return null;
    }

    @Override
    public Level getClientLevel() {
        return null;
    }

    @Override
    public Object getClientInstance() {
        return null;
    }

    @Override
    public Connection getClientConnection() {
        return null;
    }

    @Override
    public MinecraftServer getGameServer() {
        return this.gameServer;
    }

    @Override
    public boolean hasControlDown() {
        return false;
    }

    @Override
    public boolean hasShiftDown() {
        return false;
    }

    @Override
    public boolean hasAltDown() {
        return false;
    }

    @Override
    public Component getKeyMappingComponent(String identifier) {
        return Component.empty();
    }

    @Override
    public <T extends MessageV2<T>> void registerLegacyClientReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory) {

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

    }

    @Override
    public <T extends Record & ServerboundMessage<T>> void registerServerReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory) {
        ServerPlayNetworking.registerGlobalReceiver(channelName, (MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) -> {
            T message = factory.apply(buf);
            server.execute(() -> message.getHandler().handle(message, server, handler, player, player.getLevel()));
        });
    }
}
