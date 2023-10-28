package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public interface NetworkHandlerRegistry extends NetworkHandlerV3 {

    @Deprecated
    @Override
    <T extends Record & ClientboundMessage<T>> Packet<ClientGamePacketListener> toClientboundPacket(T message);

    @Deprecated
    @Override
    <T extends Record & ServerboundMessage<T>> Packet<ServerGamePacketListener> toServerboundPacket(T message);

    @Deprecated
    @Override
    default <T extends Record & ServerboundMessage<T>> void sendToServer(T message) {
        NetworkHandlerV3.super.sendToServer(message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendTo(ServerPlayer player, T message) {
        NetworkHandlerV3.super.sendTo(player, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAll(T message) {
        NetworkHandlerV3.super.sendToAll(message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllExcept(@Nullable ServerPlayer exclude, T message) {
        NetworkHandlerV3.super.sendToAllExcept(exclude, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAll(ServerLevel level, T message) {
        NetworkHandlerV3.super.sendToAll(level, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(Vec3i pos, ServerLevel level, T message) {
        NetworkHandlerV3.super.sendToAllNear(pos, level, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(double posX, double posY, double posZ, ServerLevel level, T message) {
        NetworkHandlerV3.super.sendToAllNear(posX, posY, posZ, level, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllNearExcept(@Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, ServerLevel level, T message) {
        NetworkHandlerV3.super.sendToAllNearExcept(exclude, posX, posY, posZ, distance, level, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(BlockEntity blockEntity, T message) {
        NetworkHandlerV3.super.sendToAllTracking(blockEntity, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(LevelChunk chunk, T message) {
        NetworkHandlerV3.super.sendToAllTracking(chunk, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(ServerLevel level, ChunkPos chunkPos, T message) {
        NetworkHandlerV3.super.sendToAllTracking(level, chunkPos, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllTrackingExcept(Entity entity, T message) {
        NetworkHandlerV3.super.sendToAllTrackingExcept(entity, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(ServerPlayer player, T message) {
        NetworkHandlerV3.super.sendToAllTracking(player, message);
    }
}
