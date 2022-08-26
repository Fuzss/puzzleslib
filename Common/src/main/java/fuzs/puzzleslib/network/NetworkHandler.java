package fuzs.puzzleslib.network;

import fuzs.puzzleslib.proxy.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * handler for network communications of all puzzles lib mods
 */
public interface NetworkHandler {

    /**
     * register a message for a side
     *
     * @param clazz             message class type
     * @param supplier          supplier for message (called when receiving at executing end)
     *                          we use this additional supplier to avoid having to invoke the class via reflection
     *                          and so that a default constructor in every message cannot be forgotten
     * @param direction         side this message is to be executed at
     * @param <T>               message implementation
     */
    <T extends Message<T>> void register(Class<? extends T> clazz, Supplier<T> supplier, MessageDirection direction);

    /**
     * creates a packet heading to the server side
     *
     * @param message       message to create packet from
     * @return              packet for message
     */
    Packet<?> toServerboundPacket(Message<?> message);

    /**
     * creates a packet heading to the client side
     *
     * @param message       message to create packet from
     * @return              packet for message
     */
    Packet<?> toClientboundPacket(Message<?> message);

    /**
     * send message from client to server
     *
     * @param message       message to send
     */
    default void sendToServer(Message<?> message) {
        Objects.requireNonNull(Minecraft.getInstance().getConnection(), "Cannot send packets when not in game!");
        Minecraft.getInstance().getConnection().send(this.toServerboundPacket(message));
    }

    /**
     * send message from server to client
     *
     * @param message       message to send
     * @param player        client player to send to
     */
    default void sendTo(Message<?> message, ServerPlayer player) {
        player.connection.send(this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients
     *
     * @param message       message to send
     */
    default void sendToAll(Message<?> message) {
        Proxy.INSTANCE.getGameServer().getPlayerList().broadcastAll(this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients except one
     *
     * @param message       message to send
     * @param exclude       client to exclude
     */
    default void sendToAllExcept(Message<?> message, ServerPlayer exclude) {
        for (ServerPlayer player : Proxy.INSTANCE.getGameServer().getPlayerList().getPlayers()) {
            if (player != exclude) this.sendTo(message, player);
        }
    }

    /**
     * send message from server to all clients near given position
     *
     * @param message       message to send
     * @param pos           source position
     * @param level         dimension key provider level
     */
    default void sendToAllNear(Message<?> message, BlockPos pos, Level level) {
        this.sendToAllNearExcept(message, null, pos.getX(), pos.getY(), pos.getZ(), 64.0, level);
    }

    /**
     * send message from server to all clients near given position
     *
     * @param message       message to send
     * @param exclude       exclude player having caused this event
     * @param posX          source position x
     * @param posY          source position y
     * @param posZ          source position z
     * @param distance      distance from source to receive message
     * @param level         dimension key provider level
     */
    default void sendToAllNearExcept(Message<?> message, @Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, Level level) {
        Proxy.INSTANCE.getGameServer().getPlayerList().broadcast(exclude, posX, posY, posZ, distance, level.dimension(), this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients tracking <code>entity</code>
     *
     * @param message       message to send
     * @param entity        the tracked entity
     */
    default void sendToAllTracking(Message<?> message, Entity entity) {
        ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).broadcast(entity, this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients tracking <code>entity</code> including the entity itself
     *
     * @param message       message to send
     * @param entity        the tracked entity
     */
    default void sendToAllTrackingAndSelf(Message<?> message, Entity entity) {
        ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).broadcastAndSend(entity, this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients in dimension
     *
     * @param message       message to send
     * @param level         dimension key provider level
     */
    default void sendToDimension(Message<?> message, Level level) {
        this.sendToDimension(message, level.dimension());
    }

    /**
     * send message from server to all clients in dimension
     *
     * @param message       message to send
     * @param dimension     dimension to send message in
     */
    default void sendToDimension(Message<?> message, ResourceKey<Level> dimension) {
        Proxy.INSTANCE.getGameServer().getPlayerList().broadcastAll(this.toClientboundPacket(message), dimension);
    }
}
