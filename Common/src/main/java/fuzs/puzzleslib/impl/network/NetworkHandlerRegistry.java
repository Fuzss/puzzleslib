package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface NetworkHandlerRegistry extends NetworkHandler {

    @Deprecated
    @Override
    <T> Packet<ClientCommonPacketListener> toClientboundPacket(ClientboundMessage<T> message);

    @Deprecated
    @Override
    <T> Packet<ServerCommonPacketListener> toServerboundPacket(ServerboundMessage<T> message);

    @Deprecated
    @Override
    default <T> void sendMessage(PlayerSet playerSet, ClientboundMessage<T> message) {
        NetworkHandler.super.sendMessage(playerSet, message);
    }

    @Deprecated
    @Override
    default <T> void sendMessage(ServerboundMessage<T> message) {
        NetworkHandler.super.sendMessage(message);
    }

    @Deprecated
    @Override
    default <T> void sendToServer(ServerboundMessage<T> message) {
        NetworkHandler.super.sendToServer(message);
    }

    @Deprecated
    @Override
    default <T> void sendTo(ServerPlayer player, ClientboundMessage<T> message) {
        NetworkHandler.super.sendTo(player, message);
    }

    @Deprecated
    @Override
    default <T> void sendToAll(MinecraftServer server, ClientboundMessage<T> message) {
        NetworkHandler.super.sendToAll(server, message);
    }

    @Deprecated
    @Override
    default <T> void sendToAll(MinecraftServer server, @Nullable ServerPlayer exclude, ClientboundMessage<T> message) {
        NetworkHandler.super.sendToAll(server, exclude, message);
    }

    @Deprecated
    @Override
    default <T> void sendToAll(Collection<ServerPlayer> playerList, @Nullable ServerPlayer exclude, ClientboundMessage<T> message) {
        NetworkHandler.super.sendToAll(playerList, exclude, message);
    }

    @Deprecated
    @Override
    default <T> void sendToAll(ServerLevel level, ClientboundMessage<T> message) {
        NetworkHandler.super.sendToAll(level, message);
    }

    @Deprecated
    @Override
    default <T> void sendToAllNear(Vec3i pos, ServerLevel level, ClientboundMessage<T> message) {
        NetworkHandler.super.sendToAllNear(pos, level, message);
    }

    @Deprecated
    @Override
    default <T> void sendToAllNear(double posX, double posY, double posZ, ServerLevel level, ClientboundMessage<T> message) {
        NetworkHandler.super.sendToAllNear(posX, posY, posZ, level, message);
    }

    @Deprecated
    @Override
    default <T> void sendToAllNear(@Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, ServerLevel level, ClientboundMessage<T> message) {
        NetworkHandler.super.sendToAllNear(exclude, posX, posY, posZ, distance, level, message);
    }

    @Deprecated
    @Override
    default <T> void sendToAllTracking(BlockEntity blockEntity, ClientboundMessage<T> message) {
        NetworkHandler.super.sendToAllTracking(blockEntity, message);
    }

    @Deprecated
    @Override
    default <T> void sendToAllTracking(LevelChunk chunk, ClientboundMessage<T> message) {
        NetworkHandler.super.sendToAllTracking(chunk, message);
    }

    @Deprecated
    @Override
    default <T> void sendToAllTracking(ServerLevel level, ChunkPos chunkPos, ClientboundMessage<T> message) {
        NetworkHandler.super.sendToAllTracking(level, chunkPos, message);
    }

    @Deprecated
    @Override
    default <T> void sendToAllTracking(Entity entity, ClientboundMessage<T> message, boolean includeSelf) {
        NetworkHandler.super.sendToAllTracking(entity, message, includeSelf);
    }
}
