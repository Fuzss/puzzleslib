package fuzs.puzzleslib.api.network.v3;

import fuzs.puzzleslib.api.core.v1.Buildable;
import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializer;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializers;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Handler for network communications between clients and a server.
 */
public interface NetworkHandlerV3 {

    /**
     * Creates a new network handler builder.
     *
     * @param modId id for channel name
     * @return builder for mod specific network handler with default channel
     */
    static Builder builder(String modId) {
        return ModContext.get(modId).getNetworkHandlerV3$Builder();
    }

    /**
     * creates a packet heading to the client side
     *
     * @param message message to create packet from
     * @return packet for message
     */
    <T extends Record & ClientboundMessage<T>> Packet<ClientGamePacketListener> toClientboundPacket(T message);

    /**
     * creates a packet heading to the server side
     *
     * @param message message to create packet from
     * @return packet for message
     */
    <T extends Record & ServerboundMessage<T>> Packet<ServerGamePacketListener> toServerboundPacket(T message);

    /**
     * send message from client to server
     *
     * @param message message to send
     */
    default <T extends Record & ServerboundMessage<T>> void sendToServer(T message) {
        Proxy.INSTANCE.getClientPacketListener().send(this.toServerboundPacket(message));
    }

    /**
     * send message from server to client
     *
     * @param player  client player to send to
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendTo(ServerPlayer player, T message) {
        player.connection.send(this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients
     *
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAll(T message) {
        CommonAbstractions.INSTANCE.getGameServer().getPlayerList().broadcastAll(this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients except one
     *
     * @param exclude client to exclude
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllExcept(ServerPlayer exclude, T message) {
        for (ServerPlayer player : CommonAbstractions.INSTANCE.getGameServer().getPlayerList().getPlayers()) {
            if (player != exclude) this.sendTo(player, message);
        }
    }

    /**
     * send message from server to all clients near given position
     *
     * @param pos     source position
     * @param level   dimension key provider level
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(BlockPos pos, Level level, T message) {
        this.sendToAllNearExcept(null, pos.getX(), pos.getY(), pos.getZ(), 64.0, level, message);
    }

    /**
     * send message from server to all clients near given position
     *
     * @param posX     source position x
     * @param posY     source position y
     * @param posZ     source position z
     * @param distance distance from source to receive message
     * @param level    dimension key provider level
     * @param message  message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(double posX, double posY, double posZ, double distance, Level level, T message) {
        this.sendToAllNearExcept(null, posX, posY, posZ, 64.0, level, message);
    }

    /**
     * send message from server to all clients near given position
     *
     * @param exclude  exclude player having caused this event
     * @param posX     source position x
     * @param posY     source position y
     * @param posZ     source position z
     * @param distance distance from source to receive message
     * @param level    dimension key provider level
     * @param message  message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllNearExcept(@Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, Level level, T message) {
        CommonAbstractions.INSTANCE.getGameServer().getPlayerList().broadcast(exclude, posX, posY, posZ, distance, level.dimension(), this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients tracking <code>entity</code>
     *
     * @param entity  the tracked entity
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(Entity entity, T message) {
        ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).broadcast(entity, this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients tracking <code>entity</code> including the entity itself
     *
     * @param entity  the tracked entity
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllTrackingAndSelf(Entity entity, T message) {
        ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).broadcastAndSend(entity, this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients in dimension
     *
     * @param level   dimension key provider level
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToDimension(Level level, T message) {
        this.sendToDimension(level.dimension(), message);
    }

    /**
     * send message from server to all clients in dimension
     *
     * @param dimension dimension to send message in
     * @param message   message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToDimension(ResourceKey<Level> dimension, T message) {
        CommonAbstractions.INSTANCE.getGameServer().getPlayerList().broadcastAll(this.toClientboundPacket(message), dimension);
    }

    /**
     * A builder for a network handler, allows for registering messages.
     */
    interface Builder extends NetworkHandlerRegistry, Buildable {

        /**
         * Register a new {@link MessageSerializer} by providing a {@link net.minecraft.network.FriendlyByteBuf.Writer} and a {@link net.minecraft.network.FriendlyByteBuf.Reader},
         * similarly to vanilla's {@link EntityDataSerializer}
         *
         * @param type   type to serialize, inheritance is not supported
         * @param writer writer to byte buffer
         * @param reader reader from byte buffer
         * @param <T>    data type
         * @return this builder instance
         */
        default <T> Builder registerSerializer(Class<T> type, FriendlyByteBuf.Writer<T> writer, FriendlyByteBuf.Reader<T> reader) {
            MessageSerializers.registerSerializer(type, writer, reader);
            return this;
        }

        /**
         * Register a serializer for a data type handled by vanilla's registry system.
         *
         * @param type        registry content type to serialize
         * @param resourceKey registry resource key
         * @param <T>         data type
         * @return this builder instance
         */
        default <T> Builder registerSerializer(Class<? super T> type, ResourceKey<Registry<T>> resourceKey) {
            MessageSerializers.registerSerializer(type, resourceKey);
            return this;
        }

        /**
         * Register a custom serializer for container types. Subclasses are supported, meaning e.g. any map implementation will be handled by a provider registered for {@link Map}.
         *
         * <p>All types extending collection are by default deserialized in a {@link LinkedHashSet}. To enable a specific collection type, a unique serializer must be registered.
         * This is already done for {@link List}s, which are deserialized as {@link ArrayList}.
         *
         * @param type    container type
         * @param factory new empty collection provider (preferable with pre-configured size)
         * @param <T>     container type
         * @return this builder instance
         */
        default <T> Builder registerContainerProvider(Class<T> type, Function<Type[], MessageSerializer<? extends T>> factory) {
            MessageSerializers.registerContainerProvider(type, factory);
            return this;
        }

        /**
         * Register a message that will be sent to clients.
         *
         * @param clazz message class type
         * @param <T>   message implementation
         * @return this builder instance
         */
        <T extends Record & ClientboundMessage<T>> Builder registerClientbound(Class<T> clazz);

        /**
         * Register a message that will be sent to servers.
         *
         * @param clazz message class type
         * @param <T>   message implementation
         * @return this builder instance
         */
        <T extends Record & ServerboundMessage<T>> Builder registerServerbound(Class<T> clazz);

        /**
         * Are servers without this mod or vanilla servers compatible.
         *
         * <p>Only supported on Forge right now.
         *
         * @return this builder instance
         */
        Builder clientAcceptsVanillaOrMissing();

        /**
         * Are clients without this mod or vanilla clients compatible.
         *
         * <p>Only supported on Forge right now.
         *
         * @return this builder instance
         */
        Builder serverAcceptsVanillaOrMissing();

        /**
         * Are clients and servers without this mod or vanilla clients and servers compatible.
         *
         * <p>Only supported on Forge right now.
         *
         * @return this builder instance
         */
        default Builder allAcceptVanillaOrMissing() {
            return this.clientAcceptsVanillaOrMissing().serverAcceptsVanillaOrMissing();
        }
    }
}
