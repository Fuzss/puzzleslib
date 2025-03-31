package fuzs.puzzleslib.fabric.impl.network;

import fuzs.puzzleslib.api.network.v4.message.configuration.ClientboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import fuzs.puzzleslib.api.network.v4.message.configuration.ServerboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.Objects;

public final class MessageContextFabricImpl {

    private MessageContextFabricImpl() {
        // NO-OP
    }

    public record ClientboundConfiguration(ClientConfigurationNetworking.Context context) implements ClientboundConfigurationMessage.Context {

        @Override
        public ClientConfigurationPacketListenerImpl packetListener() {
            ClientConfigurationPacketListenerImpl networkHandler = this.context.networkHandler();
            Objects.requireNonNull(networkHandler, "network handler is null");
            return networkHandler;
        }

        @Override
        public void reply(CustomPacketPayload payload) {
            this.context.responseSender().sendPacket(payload);
        }

        @Override
        public void disconnect(Component component) {
            this.context.responseSender().disconnect(component);
        }

        @Override
        public Minecraft client() {
            Minecraft client = this.context.client();
            Objects.requireNonNull(client, "client is null");
            return client;
        }
    }

    public record ClientboundPlay(ClientPlayNetworking.Context context) implements ClientboundPlayMessage.Context {

        @Override
        public ClientPacketListener packetListener() {
            ClientPacketListener networkHandler = this.client().getConnection();
            Objects.requireNonNull(networkHandler, "network handler is null");
            return networkHandler;
        }

        @Override
        public void reply(CustomPacketPayload payload) {
            this.context.responseSender().sendPacket(payload);
        }

        @Override
        public void disconnect(Component component) {
            this.context.responseSender().disconnect(component);
        }

        @Override
        public Minecraft client() {
            Minecraft client = this.context.client();
            Objects.requireNonNull(client, "client is null");
            return client;
        }

        @Override
        public LocalPlayer player() {
            LocalPlayer player = this.context.player();
            Objects.requireNonNull(player, "player is null");
            return player;
        }

        @Override
        public ClientLevel level() {
            return this.player().clientLevel;
        }
    }

    public record ServerboundConfiguration(ServerConfigurationNetworking.Context context) implements ServerboundConfigurationMessage.Context {

        @Override
        public ServerConfigurationPacketListenerImpl packetListener() {
            ServerConfigurationPacketListenerImpl networkHandler = this.context().networkHandler();
            Objects.requireNonNull(networkHandler, "network handler is null");
            return networkHandler;
        }

        @Override
        public void reply(CustomPacketPayload payload) {
            this.context.responseSender().sendPacket(payload);
        }

        @Override
        public void disconnect(Component component) {
            this.context.responseSender().disconnect(component);
        }

        @Override
        public MinecraftServer server() {
            MinecraftServer server = this.context.server();
            Objects.requireNonNull(server, "server is null");
            return server;
        }
    }

    public record ServerboundPlay(ServerPlayNetworking.Context context) implements ServerboundPlayMessage.Context {

        @Override
        public ServerGamePacketListenerImpl packetListener() {
            ServerGamePacketListenerImpl networkHandler = this.player().connection;
            Objects.requireNonNull(networkHandler, "network handler is null");
            return networkHandler;
        }

        @Override
        public void reply(CustomPacketPayload payload) {
            this.context.responseSender().sendPacket(payload);
        }

        @Override
        public void disconnect(Component component) {
            this.context.responseSender().disconnect(component);
        }

        @Override
        public MinecraftServer server() {
            MinecraftServer server = this.context.server();
            Objects.requireNonNull(server, "server is null");
            return server;
        }

        @Override
        public ServerPlayer player() {
            ServerPlayer player = this.context.player();
            Objects.requireNonNull(player, "player is null");
            return player;
        }

        @Override
        public ServerLevel level() {
            return this.player().serverLevel();
        }
    }
}
