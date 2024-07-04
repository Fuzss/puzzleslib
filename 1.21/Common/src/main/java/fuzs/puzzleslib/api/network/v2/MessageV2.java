package fuzs.puzzleslib.api.network.v2;

import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerMessageListener;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;

/**
 * Network message template providing a handler that runs when the message is received.
 * <p>
 * Additionally, controls decoding and encoding directly in the message class.
 *
 * @param <T> the implemented message type
 */
public interface MessageV2<T extends MessageV2<T>> {

    /**
     * Serialize this instance to a byte buffer.
     *
     * @param buf network data byte buffer
     */
    void write(FriendlyByteBuf buf);

    /**
     * Deserialize this instance from a byte buffer.
     *
     * @param buf network data byte buffer
     */
    void read(FriendlyByteBuf buf);

    /**
     * @return message handler for message on reception side
     */
    MessageHandler<T> makeHandler();

    /**
     * @return this message wrapped as {@link fuzs.puzzleslib.api.network.v3.ClientboundMessage}
     */
    default ClientboundMessage<T> toClientboundMessage() {
        return new ClientboundMessage<>() {

            @Override
            public ClientMessageListener<T> getHandler() {
                return new ClientMessageListener<>() {

                    @Override
                    public void handle(T message, Minecraft client, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
                        MessageV2.this.makeHandler().handle(message, player, client);
                    }
                };
            }

            @Override
            public T unwrap() {
                return (T) MessageV2.this;
            }
        };
    }

    /**
     * @return this message wrapped as {@link fuzs.puzzleslib.api.network.v3.ServerboundMessage}
     */
    default ServerboundMessage<T> toServerboundMessage() {
        return new ServerboundMessage<>() {

            @Override
            public ServerMessageListener<T> getHandler() {
                return new ServerMessageListener<>() {

                    @Override
                    public void handle(T message, MinecraftServer server, ServerGamePacketListenerImpl handler, ServerPlayer player, ServerLevel level) {
                        MessageV2.this.makeHandler().handle(message, player, server);
                    }
                };
            }

            @Override
            public T unwrap() {
                return (T) MessageV2.this;
            }
        };
    }

    /**
     * Handler for received messages.
     * <p>
     * This is implemented as an anonymous class, to force implementations to be a class as well, to prevent issues
     * concerning loading server-only classes on a client.
     *
     * @param <T> the message to handle
     */
    abstract class MessageHandler<T extends MessageV2<T>> {

        /**
         * Called to handle the given message.
         *
         * @param message  the message to handle
         * @param player   the server or client player
         * @param instance the minecraft server or minecraft client instance
         */
        public abstract void handle(T message, Player player, Object instance);
    }
}
