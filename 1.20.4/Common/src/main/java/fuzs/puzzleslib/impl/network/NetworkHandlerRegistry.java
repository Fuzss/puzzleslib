package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface NetworkHandlerRegistry extends NetworkHandlerV3 {

    @ApiStatus.Internal
    @Override
    <T extends Record & ClientboundMessage<T>> Packet<ClientCommonPacketListener> toClientboundPacket(T message);

    @ApiStatus.Internal
    @Override
    <T extends Record & ServerboundMessage<T>> Packet<ServerCommonPacketListener> toServerboundPacket(T message);

    @ApiStatus.Internal
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendMessage(PlayerSet playerSet, T message) {
        NetworkHandlerV3.super.sendMessage(playerSet, message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ServerboundMessage<T>> void sendMessage(T message) {
        NetworkHandlerV3.super.sendMessage(message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ServerboundMessage<T>> void sendToServer(T message) {
        NetworkHandlerV3.super.sendToServer(message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendTo(ServerPlayer player, T message) {
        NetworkHandlerV3.super.sendTo(player, message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAll(MinecraftServer server, T message) {
        NetworkHandlerV3.super.sendToAll(server, message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAll(MinecraftServer server, @Nullable ServerPlayer exclude, T message) {
        NetworkHandlerV3.super.sendToAll(server, exclude, message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAll(Collection<ServerPlayer> playerList, @Nullable ServerPlayer exclude, T message) {
        NetworkHandlerV3.super.sendToAll(playerList, exclude, message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAll(ServerLevel level, T message) {
        NetworkHandlerV3.super.sendToAll(level, message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(Vec3i pos, ServerLevel level, T message) {
        NetworkHandlerV3.super.sendToAllNear(pos, level, message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(double posX, double posY, double posZ, ServerLevel level, T message) {
        NetworkHandlerV3.super.sendToAllNear(posX, posY, posZ, level, message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(@Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, ServerLevel level, T message) {
        NetworkHandlerV3.super.sendToAllNear(exclude, posX, posY, posZ, distance, level, message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(BlockEntity blockEntity, T message) {
        NetworkHandlerV3.super.sendToAllTracking(blockEntity, message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(LevelChunk chunk, T message) {
        NetworkHandlerV3.super.sendToAllTracking(chunk, message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(ServerLevel level, ChunkPos chunkPos, T message) {
        NetworkHandlerV3.super.sendToAllTracking(level, chunkPos, message);
    }

    @ApiStatus.Internal
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(Entity entity, T message, boolean includeSelf) {
        NetworkHandlerV3.super.sendToAllTracking(entity, message, includeSelf);
    }
}
