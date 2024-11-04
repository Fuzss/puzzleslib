package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ServerMessageListener;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public record ServerboundLegacyMessageAdapter<T extends MessageV2<T>>(T message) implements ServerboundMessage<T> {

    @Override
    public ServerMessageListener<T> getHandler() {
        return new ServerMessageListener<>() {

            @Override
            public void handle(T message, MinecraftServer server, ServerGamePacketListenerImpl handler, ServerPlayer player, ServerLevel level) {
                message.makeHandler().handle(message, player, server);
            }
        };
    }

    @Override
    public T unwrap() {
        return this.message;
    }
}
