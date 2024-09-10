package fuzs.puzzleslib.api.network.v3;

import com.google.common.base.Preconditions;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A distributor for client-bound packets.
 */
public interface PlayerSet {

    /**
     * Send a vanilla packet to clients.
     *
     * @param packet packet to send to clients
     */
    void notify(Packet<?> packet);

    /**
     * Send message from server to no player.
     */
    static PlayerSet ofNone() {
        return (Packet<?> packet) -> {
            // NO-OP
        };
    }

    /**
     * Send message from server to an entity.
     * <p>
     * When the entity is not a {@link ServerPlayer} no message is sent.
     *
     * @param entity entity to send to
     */
    static PlayerSet ofEntity(Entity entity) {
        Objects.requireNonNull(entity, "entity is null");
        return entity instanceof ServerPlayer serverPlayer ? ofPlayer(serverPlayer) : ofNone();
    }

    /**
     * Send message from server to a player.
     *
     * @param serverPlayer player to send to
     */
    static PlayerSet ofPlayer(ServerPlayer serverPlayer) {
        Objects.requireNonNull(serverPlayer, "server player is null");
        return (Packet<?> packet) -> {
            serverPlayer.connection.send(packet);
        };
    }

    /**
     * Send message from server to all players on the server.
     *
     * @param serverPlayer player to exclude from sending to
     */
    static PlayerSet ofOthers(ServerPlayer serverPlayer) {
        Objects.requireNonNull(serverPlayer, "server player is null");
        return (Packet<?> packet) -> {
            serverPlayer.getServer().getPlayerList().getPlayers().forEach(currentServerPlayer -> {
                if (currentServerPlayer != serverPlayer) {
                    ofPlayer(currentServerPlayer).notify(packet);
                }
            });
        };
    }

    /**
     * Send message from server to all players on the server.
     *
     * @param minecraftServer server for retrieving the player list
     */
    static PlayerSet ofAll(MinecraftServer minecraftServer) {
        return (Packet<?> packet) -> {
            minecraftServer.getPlayerList().getPlayers().forEach(serverPlayer -> {
                ofPlayer(serverPlayer).notify(packet);
            });
        };
    }

    /**
     * Send message from server to all players in a level.
     *
     * @param serverLevel the level
     */
    static PlayerSet inLevel(ServerLevel serverLevel) {
        Objects.requireNonNull(serverLevel, "server level is null");
        return (Packet<?> packet) -> {
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                ofPlayer(serverPlayer).notify(packet);
            }
        };
    }

    /**
     * Send message from server to all players near a given position.
     *
     * @param position    source position
     * @param serverLevel the current level
     */
    static PlayerSet nearPosition(Vec3i position, ServerLevel serverLevel) {
        Objects.requireNonNull(position, "position is null");
        return nearPosition(position.getX(), position.getY(), position.getZ(), serverLevel);
    }

    /**
     * Send message from server to all players near a given position.
     *
     * @param posX        source position x
     * @param posY        source position y
     * @param posZ        source position z
     * @param serverLevel the current level
     */
    static PlayerSet nearPosition(double posX, double posY, double posZ, ServerLevel serverLevel) {
        return nearPosition(null, posX, posY, posZ, 64.0, serverLevel);
    }

    /**
     * Send message from server to all players near a given position.
     *
     * @param serverPlayer exclude player having caused this event
     * @param posX         source position x
     * @param posY         source position y
     * @param posZ         source position z
     * @param distance     allowed distance from source to receive message
     * @param serverLevel  the current level
     */
    static PlayerSet nearPosition(@Nullable ServerPlayer serverPlayer, double posX, double posY, double posZ, double distance, ServerLevel serverLevel) {
        Objects.requireNonNull(serverLevel, "server level is null");
        return (Packet<?> packet) -> {
            serverLevel.getServer()
                    .getPlayerList()
                    .broadcast(serverPlayer, posX, posY, posZ, distance, serverLevel.dimension(), packet);
        };
    }

    /**
     * Send message from server to all players tracking a block entity at a certain block position.
     *
     * @param blockEntity the block entity a player must track to receive this message
     */
    static PlayerSet nearBlockEntity(BlockEntity blockEntity) {
        Objects.requireNonNull(blockEntity, "block entity is null");
        Level level = blockEntity.getLevel();
        Objects.requireNonNull(level, "block entity level is null");
        Preconditions.checkState(!level.isClientSide, "block entity level is client level");
        return nearPosition(blockEntity.getBlockPos(), (ServerLevel) level);
    }

    /**
     * Send message from server to all players tracking a chunk.
     *
     * @param levelChunk the chunk a player must track to receive this message
     */
    static PlayerSet nearChunk(LevelChunk levelChunk) {
        Objects.requireNonNull(levelChunk, "chunk is null");
        Preconditions.checkState(!levelChunk.getLevel().isClientSide, "chunk level is client level");
        return nearChunk((ServerLevel) levelChunk.getLevel(), levelChunk.getPos());
    }

    /**
     * Send message from server to all players tracking a chunk.
     *
     * @param serverLevel the level containing the chunk
     * @param chunkPos    the chunk pos a player must track to receive this message
     */
    static PlayerSet nearChunk(ServerLevel serverLevel, ChunkPos chunkPos) {
        Objects.requireNonNull(serverLevel, "server level is null");
        Objects.requireNonNull(chunkPos, "chunk pos is null");
        return (Packet<?> packet) -> {
            serverLevel.getChunkSource().chunkMap.getPlayers(chunkPos, false).forEach(serverPlayer -> {
                ofPlayer(serverPlayer).notify(packet);
            });
        };
    }

    /**
     * Send message from server to all players tracking a given entity.
     * <p>
     * When the entity is a player it will receive the message as well, otherwise use {@link #nearPlayer(ServerPlayer)}.
     *
     * @param entity the tracked entity
     */
    static PlayerSet nearEntity(Entity entity) {
        Objects.requireNonNull(entity, "entity is null");
        Preconditions.checkState(!entity.getCommandSenderWorld().isClientSide, "entity level is client level");
        return (Packet<?> packet) -> {
            ((ServerLevel) entity.getCommandSenderWorld()).getChunkSource().broadcastAndSend(entity, packet);
        };
    }

    /**
     * Send message from server to all other players tracking a given player.
     * <p>
     * The player will not receive the message, for that use {@link #nearEntity(Entity)}.
     *
     * @param serverPlayer the tracked player
     */
    static PlayerSet nearPlayer(ServerPlayer serverPlayer) {
        Objects.requireNonNull(serverPlayer, "server player is null");
        return (Packet<?> packet) -> {
            serverPlayer.serverLevel().getChunkSource().broadcast(serverPlayer, packet);
        };
    }
}
