package fuzs.puzzleslib.api.network.v3;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.Buildable;
import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializer;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializers;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistry;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;
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
        return NetworkHandlerV3.builder(modId, null);
    }

    /**
     * Creates a new network handler builder.
     *
     * @param modId   id for channel name
     * @param context an internal id for this channel in case multiple are registered using the same mod id
     * @return builder for mod specific network handler with default channel
     */
    static Builder builder(String modId, @Nullable String context) {
        return ModContext.get(modId).getNetworkHandlerV3$Builder(context);
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
     * Send message from server to clients.
     *
     * @param playerSet players to send to
     * @param message   message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendMessage(PlayerSet playerSet, T message) {
        playerSet.broadcast(this.toClientboundPacket(message));
    }

    /**
     * Send message from client to server.
     *
     * @param message message to send
     */
    default <T extends Record & ServerboundMessage<T>> void sendToServer(T message) {
        ClientPacketListener clientPacketListener = Proxy.INSTANCE.getClientPacketListener();
        Objects.requireNonNull(clientPacketListener, "client packet listener is null");
        clientPacketListener.send(this.toServerboundPacket(message));
    }

    /**
     * Send message from server to a player.
     *
     * @param player  player to send to
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendTo(ServerPlayer player, T message) {
        Objects.requireNonNull(player, "player is null");
        player.connection.send(this.toClientboundPacket(message));
    }

    /**
     * Send message from server to all players.
     *
     * @param server  server for retrieving the player list
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAll(MinecraftServer server, T message) {
        this.sendToAll(server, null, message);
    }

    /**
     * Send message from server to all players except one player.
     *
     * @param server  server for retrieving the player list
     * @param exclude player to exclude
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAll(MinecraftServer server, @Nullable ServerPlayer exclude, T message) {
        Objects.requireNonNull(server, "server is null");
        this.sendToAll(server.getPlayerList().getPlayers(), exclude, message);
    }

    /**
     * Send message from server to all players except one player.
     *
     * @param playerList all players to send the message to
     * @param exclude    player to exclude
     * @param message    message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAll(Collection<ServerPlayer> playerList, @Nullable ServerPlayer exclude, T message) {
        Objects.requireNonNull(playerList, "player list is null");
        for (ServerPlayer player : playerList) {
            if (player != exclude) this.sendTo(player, message);
        }
    }

    /**
     * Send message from server to all players in a level.
     *
     * @param level   the level
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAll(ServerLevel level, T message) {
        Objects.requireNonNull(level, "level is null");
        for (ServerPlayer player : level.players()) {
            this.sendTo(player, message);
        }
    }

    /**
     * Send message from server to all players near a given position.
     *
     * @param pos     source position
     * @param level   the current level
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(Vec3i pos, ServerLevel level, T message) {
        Objects.requireNonNull(pos, "pos is null");
        this.sendToAllNear(pos.getX(), pos.getY(), pos.getZ(), level, message);
    }

    /**
     * Send message from server to all players near a given position.
     *
     * @param posX    source position x
     * @param posY    source position y
     * @param posZ    source position z
     * @param level   the current level
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(double posX, double posY, double posZ, ServerLevel level, T message) {
        this.sendToAllNear(null, posX, posY, posZ, 64.0, level, message);
    }

    /**
     * Send message from server to all players near a given position.
     *
     * @param exclude  exclude player having caused this event
     * @param posX     source position x
     * @param posY     source position y
     * @param posZ     source position z
     * @param distance allowed distance from source to receive message
     * @param level    the current level
     * @param message  message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(@Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, ServerLevel level, T message) {
        Objects.requireNonNull(level, "level is null");
        level.getServer()
                .getPlayerList()
                .broadcast(exclude, posX, posY, posZ, distance, level.dimension(), this.toClientboundPacket(message));
    }

    /**
     * Send message from server to all players tracking a block entity at a certain block position.
     *
     * @param blockEntity the block entity a player must track to receive this message
     * @param message     message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(BlockEntity blockEntity, T message) {
        Objects.requireNonNull(blockEntity, "block entity is null");
        Level level = blockEntity.getLevel();
        Objects.requireNonNull(level, "block entity level is null");
        Preconditions.checkState(!level.isClientSide, "block entity level is client level");
        this.sendToAllNear((Vec3i) blockEntity.getBlockPos(), (ServerLevel) level, message);
    }

    /**
     * Send message from server to all players tracking a chunk.
     *
     * @param chunk   the chunk a player must track to receive this message
     * @param message message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(LevelChunk chunk, T message) {
        Objects.requireNonNull(chunk, "chunk is null");
        Preconditions.checkState(!chunk.getLevel().isClientSide, "chunk level is client level");
        this.sendToAllTracking((ServerLevel) chunk.getLevel(), chunk.getPos(), message);
    }

    /**
     * Send message from server to all players tracking a chunk.
     *
     * @param level    the level containing the chunk
     * @param chunkPos the chunk pos a player must track to receive this message
     * @param message  message to send
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(ServerLevel level, ChunkPos chunkPos, T message) {
        Objects.requireNonNull(level, "level is null");
        Objects.requireNonNull(chunkPos, "chunk pos is null");
        List<ServerPlayer> players = level.getChunkSource().chunkMap.getPlayers(chunkPos, false);
        this.sendToAll(players, null, message);
    }

    /**
     * Send message from server to all players tracking a given entity.
     *
     * @param entity      the tracked entity
     * @param message     message to send
     * @param includeSelf when the tracked entity is a player will they receive the message as well
     */
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(Entity entity, T message, boolean includeSelf) {
        Objects.requireNonNull(entity, "entity is null");
        Preconditions.checkState(!entity.getCommandSenderWorld().isClientSide, "entity level is client level");
        ServerChunkCache chunkSource = ((ServerLevel) entity.getCommandSenderWorld()).getChunkSource();
        if (includeSelf) {
            chunkSource.broadcastAndSend(entity, this.toClientboundPacket(message));
        } else {
            chunkSource.broadcast(entity, this.toClientboundPacket(message));
        }
    }

    @Deprecated(forRemoval = true)
    default <T extends Record & ClientboundMessage<T>> void sendToAll(T message) {
        this.sendToAll(CommonAbstractions.INSTANCE.getMinecraftServer(), message);
    }

    @Deprecated(forRemoval = true)
    default <T extends Record & ClientboundMessage<T>> void sendToAll(@Nullable ServerPlayer exclude, T message) {
        this.sendToAll(CommonAbstractions.INSTANCE.getMinecraftServer(), exclude, message);
    }

    @Deprecated(forRemoval = true)
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(BlockPos pos, Level level, T message) {
        Objects.requireNonNull(level, "level is null");
        Preconditions.checkState(!level.isClientSide, "level is client level");
        this.sendToAllNear((Vec3i) pos, (ServerLevel) level, message);
    }

    @Deprecated(forRemoval = true)
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(double posX, double posY, double posZ, double distance, Level level, T message) {
        Objects.requireNonNull(level, "level is null");
        Preconditions.checkState(!level.isClientSide, "level is client level");
        this.sendToAllNear(posX, posY, posZ, (ServerLevel) level, message);
    }

    @Deprecated(forRemoval = true)
    default <T extends Record & ClientboundMessage<T>> void sendToAllNearExcept(@Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, Level level, T message) {
        Objects.requireNonNull(level, "level is null");
        Preconditions.checkState(!level.isClientSide, "level is client level");
        this.sendToAllNear(exclude, posX, posY, posZ, distance, (ServerLevel) level, message);
    }

    @Deprecated(forRemoval = true)
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(Entity entity, T message) {
        this.sendToAllTracking(entity, message, false);
    }

    @Deprecated(forRemoval = true)
    default <T extends Record & ClientboundMessage<T>> void sendToAllTrackingAndSelf(Entity entity, T message) {
        this.sendToAllTracking(entity, message, true);
    }

    @Deprecated(forRemoval = true)
    default <T extends Record & ClientboundMessage<T>> void sendToDimension(Level level, T message) {
        Objects.requireNonNull(level, "level is null");
        Preconditions.checkState(!level.isClientSide, "level is client level");
        this.sendToAll((ServerLevel) level, message);
    }

    @Deprecated(forRemoval = true)
    default <T extends Record & ClientboundMessage<T>> void sendToDimension(ResourceKey<Level> resourceKey, T message) {
        Objects.requireNonNull(resourceKey, "resource key is null");
        ServerLevel level = CommonAbstractions.INSTANCE.getMinecraftServer().getLevel(resourceKey);
        Objects.requireNonNull(level, "level is null");
        this.sendToAll(level, message);
    }

    /**
     * A builder for a network handler, allows for registering messages.
     */
    interface Builder extends NetworkHandlerRegistry, Buildable {

        /**
         * Register a new {@link MessageSerializer} by providing a {@link net.minecraft.network.FriendlyByteBuf.Writer}
         * and a {@link net.minecraft.network.FriendlyByteBuf.Reader}, similarly to vanilla's
         * {@link EntityDataSerializer}
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
         * Register a custom serializer for container types. Subclasses are supported, meaning e.g. any map
         * implementation will be handled by a provider registered for {@link Map}.
         *
         * <p>All types extending collection are by default deserialized in a {@link LinkedHashSet}. To enable a
         * specific collection type, a unique serializer must be registered.
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
