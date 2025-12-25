package fuzs.puzzleslib.api.network.v4;

import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A distributor for client-bound packets.
 */
@FunctionalInterface
public interface PlayerSet {

    /**
     * Send a packet to clients.
     *
     * @param serverPlayerConsumer the action to apply to all server players
     */
    void apply(Consumer<ServerPlayer> serverPlayerConsumer);

    /**
     * Send a message from the server to no player.
     */
    static PlayerSet ofNone() {
        return (Consumer<ServerPlayer> serverPlayerConsumer) -> {
            // NO-OP
        };
    }

    /**
     * Send a message from the server to an entity.
     * <p>
     * When the entity is not a {@link ServerPlayer} no message is sent.
     *
     * @param entity the entity to send to
     */
    static PlayerSet ofEntity(Entity entity) {
        Objects.requireNonNull(entity, "entity is null");
        return (Consumer<ServerPlayer> serverPlayerConsumer) -> {
            if (entity instanceof ServerPlayer serverPlayer) {
                ofPlayer(serverPlayer).apply(serverPlayerConsumer);
            }
        };
    }

    /**
     * Send a message from the server to a player.
     *
     * @param serverPlayer the player to send to
     */
    static PlayerSet ofPlayer(ServerPlayer serverPlayer) {
        Objects.requireNonNull(serverPlayer, "server player is null");
        return (Consumer<ServerPlayer> serverPlayerConsumer) -> {
            if (serverPlayer.connection != null) {
                serverPlayerConsumer.accept(serverPlayer);
            } else {
                PuzzlesLib.LOGGER.warn(
                        "{} has not yet been fully initialized and is therefore unable to receive any packets",
                        serverPlayer.getPlainTextName());
            }
        };
    }

    /**
     * Send a message from the server to all players tracking a given entity.
     * <p>
     * When the entity is a player, it will receive the message as well, otherwise use
     * {@link #nearPlayer(ServerPlayer)}.
     * <p>
     * The implementation is copied from
     * {@link net.minecraft.server.level.ServerChunkCache#sendToTrackingPlayersAndSelf(Entity, Packet)}.
     *
     * @param entity the tracked entity
     */
    static PlayerSet nearEntity(Entity entity) {
        Objects.requireNonNull(entity, "entity is null");
        return (Consumer<ServerPlayer> serverPlayerConsumer) -> {
            if (entity.level() instanceof ServerLevel serverLevel) {
                ChunkMap chunkMap = serverLevel.getChunkSource().chunkMap;
                ChunkMap.TrackedEntity trackedEntity = chunkMap.entityMap.get(entity.getId());
                if (trackedEntity != null) {
                    for (ServerPlayerConnection serverPlayerConnection : trackedEntity.seenBy) {
                        ofPlayer(serverPlayerConnection.getPlayer()).apply(serverPlayerConsumer);
                    }

                    if (entity instanceof ServerPlayer serverPlayer) {
                        ofPlayer(serverPlayer).apply(serverPlayerConsumer);
                    }
                }
            }
        };
    }

    /**
     * Send a message from the server to all other players tracking a given player.
     * <p>
     * The player will not receive the message, for that use {@link #nearEntity(Entity)}.
     * <p>
     * The implementation is copied from
     * {@link net.minecraft.server.level.ServerChunkCache#sendToTrackingPlayers(Entity, Packet)}.
     *
     * @param serverPlayer the tracked player
     */
    static PlayerSet nearPlayer(ServerPlayer serverPlayer) {
        Objects.requireNonNull(serverPlayer, "server player is null");
        return (Consumer<ServerPlayer> serverPlayerConsumer) -> {
            ChunkMap chunkMap = serverPlayer.level().getChunkSource().chunkMap;
            ChunkMap.TrackedEntity trackedEntity = chunkMap.entityMap.get(serverPlayer.getId());
            if (trackedEntity != null) {
                for (ServerPlayerConnection serverPlayerConnection : trackedEntity.seenBy) {
                    ofPlayer(serverPlayerConnection.getPlayer()).apply(serverPlayerConsumer);
                }
            }
        };
    }

    /**
     * Send a message from the server to all players on the server.
     *
     * @param serverPlayer the player to exclude from being sent to
     */
    static PlayerSet ofOthers(ServerPlayer serverPlayer) {
        Objects.requireNonNull(serverPlayer, "server player is null");
        return (Consumer<ServerPlayer> serverPlayerConsumer) -> {
            serverPlayer.level()
                    .getServer()
                    .getPlayerList()
                    .getPlayers()
                    .forEach((ServerPlayer currentServerPlayer) -> {
                        if (currentServerPlayer != serverPlayer) {
                            ofPlayer(currentServerPlayer).apply(serverPlayerConsumer);
                        }
                    });
        };
    }

    /**
     * Send a message from the server to all players on the server.
     *
     * @param minecraftServer the server for retrieving the player list
     */
    static PlayerSet ofAll(MinecraftServer minecraftServer) {
        return (Consumer<ServerPlayer> serverPlayerConsumer) -> {
            minecraftServer.getPlayerList().getPlayers().forEach((ServerPlayer serverPlayer) -> {
                ofPlayer(serverPlayer).apply(serverPlayerConsumer);
            });
        };
    }

    /**
     * Send a message from the server to all players in a level.
     *
     * @param level the level
     */
    static PlayerSet inLevel(Level level) {
        Objects.requireNonNull(level, "level is null");
        return (Consumer<ServerPlayer> serverPlayerConsumer) -> {
            if (level instanceof ServerLevel serverLevel) {
                inLevel(serverLevel).apply(serverPlayerConsumer);
            }
        };
    }

    /**
     * Send a message from the server to all players in a level.
     *
     * @param serverLevel the level
     */
    static PlayerSet inLevel(ServerLevel serverLevel) {
        Objects.requireNonNull(serverLevel, "server level is null");
        return (Consumer<ServerPlayer> serverPlayerConsumer) -> {
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                ofPlayer(serverPlayer).apply(serverPlayerConsumer);
            }
        };
    }

    /**
     * Send a message from the server to all players near a given position.
     *
     * @param position    the source position
     * @param serverLevel the current level
     */
    static PlayerSet nearPosition(Vec3i position, ServerLevel serverLevel) {
        Objects.requireNonNull(position, "position is null");
        return nearPosition(position.getX(), position.getY(), position.getZ(), serverLevel);
    }

    /**
     * Send a message from the server to all players near a given position.
     *
     * @param posX        the source position x
     * @param posY        the source position y
     * @param posZ        the source position z
     * @param serverLevel the current level
     */
    static PlayerSet nearPosition(double posX, double posY, double posZ, ServerLevel serverLevel) {
        return nearPosition(null, posX, posY, posZ, 64.0, serverLevel);
    }

    /**
     * Send a message from the server to all players near a given position.
     * <p>
     * The implementation is copied from
     * {@link net.minecraft.server.players.PlayerList#broadcast(Player, double, double, double, double, ResourceKey,
     * Packet)}.
     *
     * @param excludePlayer the exclude player having caused this event
     * @param posX          the source position x
     * @param posY          the source position y
     * @param posZ          the source position z
     * @param distance      the maximum Euclidean distance from the source to be able to receive the message
     * @param serverLevel   the current level
     */
    static PlayerSet nearPosition(@Nullable ServerPlayer excludePlayer, double posX, double posY, double posZ, double distance, ServerLevel serverLevel) {
        Objects.requireNonNull(serverLevel, "server level is null");
        return (Consumer<ServerPlayer> serverPlayerConsumer) -> {
            for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
                if (serverPlayer != excludePlayer && serverPlayer.level().dimension() == serverLevel.dimension()) {
                    double deltaX = posX - serverPlayer.getX();
                    double deltaY = posY - serverPlayer.getY();
                    double deltaZ = posZ - serverPlayer.getZ();
                    if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ < distance * distance) {
                        ofPlayer(serverPlayer).apply(serverPlayerConsumer);
                    }
                }
            }
        };
    }

    /**
     * Send a message from the server to all players tracking a block entity at a certain block position.
     *
     * @param blockEntity the block entity a player must track to receive this message
     */
    static PlayerSet nearBlockEntity(BlockEntity blockEntity) {
        Objects.requireNonNull(blockEntity, "block entity is null");
        return (Consumer<ServerPlayer> serverPlayerConsumer) -> {
            if (blockEntity.getLevel() instanceof ServerLevel serverLevel) {
                nearPosition(blockEntity.getBlockPos(), serverLevel).apply(serverPlayerConsumer);
            }
        };
    }

    /**
     * Send a message from the server to all players tracking a chunk.
     *
     * @param levelChunk the chunk a player must track to receive this message
     */
    static PlayerSet nearChunk(LevelChunk levelChunk) {
        Objects.requireNonNull(levelChunk, "chunk is null");
        return (Consumer<ServerPlayer> serverPlayerConsumer) -> {
            if (levelChunk.getLevel() instanceof ServerLevel serverLevel) {
                nearChunk(serverLevel, levelChunk.getPos()).apply(serverPlayerConsumer);
            }
        };
    }

    /**
     * Send a message from the server to all players tracking a chunk.
     *
     * @param serverLevel the level containing the chunk
     * @param chunkPos    the chunk pos a player must track to receive this message
     */
    static PlayerSet nearChunk(ServerLevel serverLevel, ChunkPos chunkPos) {
        Objects.requireNonNull(serverLevel, "server level is null");
        Objects.requireNonNull(chunkPos, "chunk pos is null");
        return (Consumer<ServerPlayer> serverPlayerConsumer) -> {
            serverLevel.getChunkSource().chunkMap.getPlayers(chunkPos, false).forEach((ServerPlayer serverPlayer) -> {
                ofPlayer(serverPlayer).apply(serverPlayerConsumer);
            });
        };
    }
}
