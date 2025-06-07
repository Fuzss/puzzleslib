package fuzs.puzzleslib.api.network.v4;

import fuzs.puzzleslib.api.util.v1.EntityHelper;
import fuzs.puzzleslib.api.network.v4.message.Message;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Helper class for sending {@link Message Messages}, {@link CustomPacketPayload CustomPacketPayloads} and
 * {@link Packet Packets} to the server and clients.
 */
public final class MessageSender {

    private MessageSender() {
        // NO-OP
    }

    /**
     * Send a message to the server.
     *
     * @param message the message to send
     */
    public static void broadcast(Message<? extends Message.Context<? extends ServerCommonPacketListener>> message) {
        Objects.requireNonNull(message, "message is null");
        broadcast((CustomPacketPayload) message);
    }

    /**
     * Send a payload to the server.
     *
     * @param payload the payload to send
     */
    @ApiStatus.Experimental
    public static void broadcast(CustomPacketPayload payload) {
        Objects.requireNonNull(payload, "payload is null");
        if (NetworkingHelper.hasChannel(NetworkingHelper.getClientPacketListener(), payload.type())) {
            broadcast(NetworkingHelper.toServerboundPacket(payload));
        }
    }

    /**
     * Send a packet to the server.
     *
     * @param packet the packet to send
     */
    public static void broadcast(Packet<?> packet) {
        Objects.requireNonNull(packet, "packet is null");
        ClientGamePacketListener packetListener = NetworkingHelper.getClientPacketListener();
        Connection connection = NetworkingHelper.getConnection(packetListener);
        connection.send(packet);
    }

    /**
     * Send a message to clients.
     *
     * @param playerSet the players to send to
     * @param message   the message to send
     */
    public static void broadcast(PlayerSet playerSet, Message<? extends Message.Context<? extends ClientCommonPacketListener>> message) {
        Objects.requireNonNull(playerSet, "player set is null");
        Objects.requireNonNull(message, "message is null");
        broadcast(playerSet, (CustomPacketPayload) message);
    }

    /**
     * Send a payload to clients.
     *
     * @param playerSet the players to send to
     * @param payload   the payload to send
     */
    @ApiStatus.Experimental
    public static void broadcast(PlayerSet playerSet, CustomPacketPayload payload) {
        Objects.requireNonNull(playerSet, "player set is null");
        Objects.requireNonNull(payload, "payload is null");
        broadcast((Consumer<ServerPlayer> serverPlayerConsumer) -> {
            playerSet.apply((ServerPlayer serverPlayer) -> {
                if (NetworkingHelper.hasChannel(serverPlayer.connection, payload.type())) {
                    serverPlayerConsumer.accept(serverPlayer);
                }
            });
        }, NetworkingHelper.toClientboundPacket(payload));
    }

    /**
     * Send a packet to clients.
     *
     * @param playerSet the players to send to
     * @param packet    the packet to send
     */
    public static void broadcast(PlayerSet playerSet, Packet<?> packet) {
        Objects.requireNonNull(playerSet, "player set is null");
        Objects.requireNonNull(packet, "packet is null");
        playerSet.apply((ServerPlayer serverPlayer) -> {
            if (!EntityHelper.isFakePlayer(serverPlayer)) {
                serverPlayer.connection.send(packet);
            }
        });
    }
}
