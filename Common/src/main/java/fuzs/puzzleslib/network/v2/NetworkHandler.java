package fuzs.puzzleslib.network.v2;

import fuzs.puzzleslib.proxy.Proxy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * handler for network communications of all puzzles lib mods
 */
public interface NetworkHandler {

    /**
     * creates a packet heading to the client side
     *
     * @param message message to create packet from
     * @return packet for message
     */
    <T extends Record & ClientboundMessage<T>> Packet<?> toClientboundPacket(T message);

    /**
     * creates a packet heading to the server side
     *
     * @param message message to create packet from
     * @return packet for message
     */
    <T extends Record & ServerboundMessage<T>> Packet<?> toServerboundPacket(T message);

    /**
     * send message from client to server
     *
     * @param message message to send
     */
    default <T extends Record & ServerboundMessage<T>> void sendToServer(T message) {
        Proxy.INSTANCE.getClientConnection().send(this.toServerboundPacket(message));
    }

    /**
     * send message from server to client
     *
     * @param message message to send
     * @param player  client player to send to
     */
    default <T extends Record & ClientboundMessage<T>> void sendTo(T message, ServerPlayer player) {
        player.connection.send(this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients
     *
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAll(T message) {
        Proxy.INSTANCE.getGameServer().getPlayerList().broadcastAll(this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients except one
     *
     * @param message message to send
     * @param exclude client to exclude
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllExcept(T message, ServerPlayer exclude) {
        for (ServerPlayer player : Proxy.INSTANCE.getGameServer().getPlayerList().getPlayers()) {
            if (player != exclude) this.sendTo(message, player);
        }
    }

    /**
     * send message from server to all clients near given position
     *
     * @param message message to send
     * @param pos     source position
     * @param level   dimension key provider level
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(T message, BlockPos pos, Level level) {
        this.sendToAllNearExcept(message, null, pos.getX(), pos.getY(), pos.getZ(), 64.0, level);
    }

    /**
     * send message from server to all clients near given position
     *
     * @param message  message to send
     * @param posX     source position x
     * @param posY     source position y
     * @param posZ     source position z
     * @param distance distance from source to receive message
     * @param level    dimension key provider level
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(T message, double posX, double posY, double posZ, double distance, Level level) {
        this.sendToAllNearExcept(message, null, posX, posY, posZ, 64.0, level);
    }

    /**
     * send message from server to all clients near given position
     *
     * @param message  message to send
     * @param exclude  exclude player having caused this event
     * @param posX     source position x
     * @param posY     source position y
     * @param posZ     source position z
     * @param distance distance from source to receive message
     * @param level    dimension key provider level
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllNearExcept(T message, @Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, Level level) {
        Proxy.INSTANCE.getGameServer().getPlayerList().broadcast(exclude, posX, posY, posZ, distance, level.dimension(), this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients tracking <code>entity</code>
     *
     * @param message message to send
     * @param entity  the tracked entity
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(T message, Entity entity) {
        ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).broadcast(entity, this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients tracking <code>entity</code> including the entity itself
     *
     * @param message message to send
     * @param entity  the tracked entity
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllTrackingAndSelf(T message, Entity entity) {
        ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).broadcastAndSend(entity, this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients in dimension
     *
     * @param message message to send
     * @param level   dimension key provider level
     */
    default <T extends Record & ClientboundMessage<T>> void sendToDimension(T message, Level level) {
        this.sendToDimension(message, level.dimension());
    }

    /**
     * send message from server to all clients in dimension
     *
     * @param message   message to send
     * @param dimension dimension to send message in
     */
    default <T extends Record & ClientboundMessage<T>> void sendToDimension(T message, ResourceKey<Level> dimension) {
        Proxy.INSTANCE.getGameServer().getPlayerList().broadcastAll(this.toClientboundPacket(message), dimension);
    }
}
