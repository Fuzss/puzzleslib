package fuzs.puzzleslib.neoforge.impl.network;

import fuzs.puzzleslib.api.network.v4.message.Message;
import fuzs.puzzleslib.api.network.v4.message.configuration.ClientboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.configuration.ServerboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

public abstract class MessageContextNeoForgeImpl<T extends PacketListener> implements Message.Context<T> {
    protected final IPayloadContext context;

    public MessageContextNeoForgeImpl(IPayloadContext context) {
        this.context = context;
    }

    @Override
    public T packetListener() {
        T networkHandler = (T) this.context.listener();
        Objects.requireNonNull(networkHandler, "network handler is null");
        return networkHandler;
    }

    @Override
    public void reply(CustomPacketPayload payload) {
        this.context.reply(payload);
    }

    @Override
    public void disconnect(Component component) {
        this.context.disconnect(component);
    }

    public static final class ClientboundConfiguration extends MessageContextNeoForgeImpl<ClientConfigurationPacketListenerImpl> implements ClientboundConfigurationMessage.Context {

        public ClientboundConfiguration(IPayloadContext context) {
            super(context);
        }

        @Override
        public Minecraft client() {
            Minecraft client = (Minecraft) this.context.listener().getMainThreadEventLoop();
            Objects.requireNonNull(client, "client is null");
            return client;
        }
    }

    public static final class ClientboundPlay extends MessageContextNeoForgeImpl<ClientPacketListener> implements ClientboundPlayMessage.Context {

        public ClientboundPlay(IPayloadContext context) {
            super(context);
        }

        @Override
        public Minecraft client() {
            Minecraft client = (Minecraft) this.context.listener().getMainThreadEventLoop();
            Objects.requireNonNull(client, "client is null");
            return client;
        }

        @Override
        public LocalPlayer player() {
            LocalPlayer player = (LocalPlayer) this.context.player();
            Objects.requireNonNull(player, "player is null");
            return player;
        }

        @Override
        public ClientLevel level() {
            return this.client().level;
        }
    }

    public static final class ServerboundConfiguration extends MessageContextNeoForgeImpl<ServerConfigurationPacketListenerImpl> implements ServerboundConfigurationMessage.Context {

        public ServerboundConfiguration(IPayloadContext context) {
            super(context);
        }

        @Override
        public MinecraftServer server() {
            MinecraftServer server = (MinecraftServer) this.context.listener().getMainThreadEventLoop();
            Objects.requireNonNull(server, "server is null");
            return server;
        }
    }

    public static final class ServerboundPlay extends MessageContextNeoForgeImpl<ServerGamePacketListenerImpl> implements ServerboundPlayMessage.Context {

        public ServerboundPlay(IPayloadContext context) {
            super(context);
        }

        @Override
        public MinecraftServer server() {
            MinecraftServer server = (MinecraftServer) this.context.listener().getMainThreadEventLoop();
            Objects.requireNonNull(server, "server is null");
            return server;
        }

        @Override
        public ServerPlayer player() {
            ServerPlayer player = (ServerPlayer) this.context.player();
            Objects.requireNonNull(player, "player is null");
            return player;
        }

        @Override
        public ServerLevel level() {
            return this.player().level();
        }
    }
}
