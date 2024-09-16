package fuzs.puzzleslib.api.network.v3;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.core.v1.utility.Buildable;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.codec.StreamCodecRegistry;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistry;
import fuzs.puzzleslib.impl.network.codec.StreamCodecRegistryImpl;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.resources.ResourceLocation;
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
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Handler for network communications between clients and a server.
 */
public interface NetworkHandler {

    /**
     * Creates a new network handler builder.
     *
     * @param modId mod id for default channel name
     * @return builder for mod specific network handler with default channel
     */
    static Builder builder(String modId) {
        return builder(ResourceLocationHelper.fromNamespaceAndPath(modId, "play"));
    }

    /**
     * Creates a new network handler builder.
     *
     * @param channelName id for channel name
     * @return builder for mod specific network handler with default channel
     */
    static Builder builder(ResourceLocation channelName) {
        return ModContext.get(channelName.getNamespace()).getNetworkHandler(channelName);
    }

    /**
     * creates a packet heading to the client side
     *
     * @param message message to create packet from
     * @return packet for message
     */
    <T> Packet<ClientCommonPacketListener> toClientboundPacket(ClientboundMessage<T> message);

    /**
     * creates a packet heading to the server side
     *
     * @param message message to create packet from
     * @return packet for message
     */
    <T> Packet<ServerCommonPacketListener> toServerboundPacket(ServerboundMessage<T> message);

    /**
     * Send message from server to clients.
     *
     * @param playerSet players to send to
     * @param message   message to send
     */
    default <T> void sendMessage(PlayerSet playerSet, ClientboundMessage<T> message) {
        playerSet.broadcast(this.toClientboundPacket(message));
    }

    /**
     * Send message from client to server.
     *
     * @param message message to send
     */
    default <T> void sendMessage(ServerboundMessage<T> message) {
        ClientPacketListener clientPacketListener = Proxy.INSTANCE.getClientPacketListener();
        Objects.requireNonNull(clientPacketListener, "client packet listener is null");
        clientPacketListener.send(this.toServerboundPacket(message));
    }

    /**
     * Send message from client to server.
     *
     * @param message message to send
     */
    default <T> void sendToServer(ServerboundMessage<T> message) {
        this.sendMessage(message);
    }

    /**
     * Send message from server to a player.
     *
     * @param player  player to send to
     * @param message message to send
     */
    default <T> void sendTo(ServerPlayer player, ClientboundMessage<T> message) {
        Objects.requireNonNull(player, "player is null");
        player.connection.send(this.toClientboundPacket(message));
    }

    /**
     * Send message from server to all players.
     *
     * @param server  server for retrieving the player list
     * @param message message to send
     */
    default <T> void sendToAll(MinecraftServer server, ClientboundMessage<T> message) {
        this.sendToAll(server, null, message);
    }

    /**
     * Send message from server to all players except one player.
     *
     * @param server  server for retrieving the player list
     * @param exclude player to exclude
     * @param message message to send
     */
    default <T> void sendToAll(MinecraftServer server, @Nullable ServerPlayer exclude, ClientboundMessage<T> message) {
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
    default <T> void sendToAll(Collection<ServerPlayer> playerList, @Nullable ServerPlayer exclude, ClientboundMessage<T> message) {
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
    default <T> void sendToAll(ServerLevel level, ClientboundMessage<T> message) {
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
    default <T> void sendToAllNear(Vec3i pos, ServerLevel level, ClientboundMessage<T> message) {
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
    default <T> void sendToAllNear(double posX, double posY, double posZ, ServerLevel level, ClientboundMessage<T> message) {
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
    default <T> void sendToAllNear(@Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, ServerLevel level, ClientboundMessage<T> message) {
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
    default <T> void sendToAllTracking(BlockEntity blockEntity, ClientboundMessage<T> message) {
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
    default <T> void sendToAllTracking(LevelChunk chunk, ClientboundMessage<T> message) {
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
    default <T> void sendToAllTracking(ServerLevel level, ChunkPos chunkPos, ClientboundMessage<T> message) {
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
    default <T> void sendToAllTracking(Entity entity, ClientboundMessage<T> message, boolean includeSelf) {
        Objects.requireNonNull(entity, "entity is null");
        Preconditions.checkState(!entity.getCommandSenderWorld().isClientSide, "entity level is client level");
        ServerChunkCache chunkSource = ((ServerLevel) entity.getCommandSenderWorld()).getChunkSource();
        if (includeSelf) {
            chunkSource.broadcastAndSend(entity, this.toClientboundPacket(message));
        } else {
            chunkSource.broadcast(entity, this.toClientboundPacket(message));
        }
    }

    /**
     * A builder for a network handler, allows for registering messages.
     */
    interface Builder extends NetworkHandlerRegistry, StreamCodecRegistry<Builder>, Buildable {

        @Override
        default <B extends ByteBuf, V> Builder registerSerializer(Class<V> type, StreamCodec<? super B, V> streamCodec) {
            StreamCodecRegistryImpl.INSTANCE.registerSerializer(type, streamCodec);
            return this;
        }

        @Override
        default <B extends ByteBuf, V> Builder registerContainerProvider(Class<V> type, Function<Type[], StreamCodec<? super B, ? extends V>> factory) {
            StreamCodecRegistryImpl.INSTANCE.registerContainerProvider(type, factory);
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
         * Register a message that will be sent to clients.
         *
         * @param <T>     message implementation
         * @param clazz   message class type
         * @param factory message factory
         */
        default <T extends MessageV2<T>> Builder registerLegacyClientbound(Class<T> clazz, Supplier<T> factory) {
            return this.registerLegacyClientbound(clazz, (FriendlyByteBuf friendlyByteBuf) -> {
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
        default <T extends MessageV2<T>> Builder registerLegacyServerbound(Class<T> clazz, Supplier<T> factory) {
            return this.registerLegacyServerbound(clazz, (FriendlyByteBuf friendlyByteBuf) -> {
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
        <T extends MessageV2<T>> Builder registerLegacyClientbound(Class<T> clazz, StreamDecoder<FriendlyByteBuf, T> factory);

        /**
         * Register a message that will be sent to servers.
         *
         * @param <T>     message implementation
         * @param clazz   message class type
         * @param factory message factory
         */
        <T extends MessageV2<T>> Builder registerLegacyServerbound(Class<T> clazz, StreamDecoder<FriendlyByteBuf, T> factory);

        /**
         * Are clients &amp; servers without this mod or vanilla clients &amp; servers compatible.
         *
         * <p>Not supported on Fabric-like environments.
         *
         * @return this builder instance
         */
        Builder optional();
    }
}
