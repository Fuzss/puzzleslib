package fuzs.puzzleslib.api.network.v2;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * handler for network communications of all puzzles lib mods
 */
public interface NetworkHandlerV2 {

    /**
     * Creates a new network handler.
     *
     * @param modId    id for channel name
     * @param optional are client &amp; servers without this mod or vanilla compatible
     * @return mod specific network handler with configured channel
     */
    static NetworkHandlerV2 build(String modId, boolean optional) {
        return build(new ResourceLocation(modId, "main"), optional);
    }

    /**
     * Creates a new network handler.
     *
     * @param channelName the channel name
     * @param optional    are client &amp; servers without this mod or vanilla compatible
     * @return mod specific network handler with configured channel
     */
    static NetworkHandlerV2 build(ResourceLocation channelName, boolean optional) {
        return ModContext.get(channelName.getNamespace()).getNetworkHandlerV2(channelName, optional);
    }

    /**
     * Register a message that will be sent to clients.
     *
     * @param <T>     message implementation
     * @param clazz   message class type
     * @param factory message factory
     */
    default <T extends MessageV2<T>> NetworkHandlerV2 registerClientbound(Class<T> clazz, Supplier<T> factory) {
        return this.registerClientbound(clazz, (FriendlyByteBuf friendlyByteBuf) -> {
            T message = factory.get();
            message.read(friendlyByteBuf);
            return message;
        });
    }

    /**
     * Register a message that will be sent to servers.
     *
     * @param <T>     message implementation
     * @param clazz   message class type
     * @param factory message factory
     */
    default <T extends MessageV2<T>> NetworkHandlerV2 registerServerbound(Class<T> clazz, Supplier<T> factory) {
        return this.registerServerbound(clazz, (FriendlyByteBuf friendlyByteBuf) -> {
            T message = factory.get();
            message.read(friendlyByteBuf);
            return message;
        });
    }

    /**
     * Register a message that will be sent to clients.
     *
     * @param <T>     message implementation
     * @param clazz   message class type
     * @param factory message factory
     */
    <T extends MessageV2<T>> NetworkHandlerV2 registerClientbound(Class<T> clazz, Function<FriendlyByteBuf, T> factory);

    /**
     * Register a message that will be sent to servers.
     *
     * @param <T>     message implementation
     * @param clazz   message class type
     * @param factory message factory
     */
    <T extends MessageV2<T>> NetworkHandlerV2 registerServerbound(Class<T> clazz, Function<FriendlyByteBuf, T> factory);

    /**
     * creates a packet heading to the client side
     *
     * @param message message to create packet from
     * @return packet for message
     */
    Packet<ClientCommonPacketListener> toClientboundPacket(MessageV2<?> message);

    /**
     * creates a packet heading to the server side
     *
     * @param message message to create packet from
     * @return packet for message
     */
    Packet<ServerCommonPacketListener> toServerboundPacket(MessageV2<?> message);

    /**
     * send message from client to server
     *
     * @param message message to send
     */
    default void sendToServer(MessageV2<?> message) {
        Proxy.INSTANCE.getClientPacketListener().send(this.toServerboundPacket(message));
    }

    /**
     * send message from server to client
     *
     * @param message message to send
     * @param player  client player to send to
     */
    default void sendTo(MessageV2<?> message, ServerPlayer player) {
        player.connection.send(this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients
     *
     * @param message message to send
     */
    default void sendToAll(MessageV2<?> message) {
        CommonAbstractions.INSTANCE.getMinecraftServer().getPlayerList().broadcastAll(this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients except one
     *
     * @param message message to send
     * @param exclude client to exclude
     */
    default void sendToAllExcept(MessageV2<?> message, ServerPlayer exclude) {
        for (ServerPlayer player : CommonAbstractions.INSTANCE.getMinecraftServer().getPlayerList().getPlayers()) {
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
    default void sendToAllNear(MessageV2<?> message, BlockPos pos, Level level) {
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
    default void sendToAllNear(MessageV2<?> message, double posX, double posY, double posZ, double distance, Level level) {
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
    default void sendToAllNearExcept(MessageV2<?> message, @Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, Level level) {
        CommonAbstractions.INSTANCE.getMinecraftServer().getPlayerList().broadcast(exclude, posX, posY, posZ, distance, level.dimension(), this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients tracking <code>entity</code>
     *
     * @param message message to send
     * @param entity  the tracked entity
     */
    default void sendToAllTracking(MessageV2<?> message, Entity entity) {
        ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).broadcast(entity, this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients tracking <code>entity</code> including the entity itself
     *
     * @param message message to send
     * @param entity  the tracked entity
     */
    default void sendToAllTrackingAndSelf(MessageV2<?> message, Entity entity) {
        ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).broadcastAndSend(entity, this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients in dimension
     *
     * @param message message to send
     * @param level   dimension key provider level
     */
    default void sendToDimension(MessageV2<?> message, Level level) {
        this.sendToDimension(message, level.dimension());
    }

    /**
     * send message from server to all clients in dimension
     *
     * @param message   message to send
     * @param dimension dimension to send message in
     */
    default void sendToDimension(MessageV2<?> message, ResourceKey<Level> dimension) {
        CommonAbstractions.INSTANCE.getMinecraftServer().getPlayerList().broadcastAll(this.toClientboundPacket(message), dimension);
    }
}
