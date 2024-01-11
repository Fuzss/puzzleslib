package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
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
    default <T extends Record & ClientboundMessage<T>> void sendToAllExcept(ServerPlayer exclude, T message) {
        NetworkHandlerV3.super.sendToAllExcept(exclude, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(BlockPos pos, Level level, T message) {
        NetworkHandlerV3.super.sendToAllNear(pos, level, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(double posX, double posY, double posZ, double distance, Level level, T message) {
        NetworkHandlerV3.super.sendToAllNear(posX, posY, posZ, distance, level, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllNearExcept(@Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, Level level, T message) {
        NetworkHandlerV3.super.sendToAllNearExcept(exclude, posX, posY, posZ, distance, level, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(Entity entity, T message) {
        NetworkHandlerV3.super.sendToAllTracking(entity, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllTrackingAndSelf(Entity entity, T message) {
        NetworkHandlerV3.super.sendToAllTrackingAndSelf(entity, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToDimension(Level level, T message) {
        NetworkHandlerV3.super.sendToDimension(level, message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToDimension(ResourceKey<Level> dimension, T message) {
        NetworkHandlerV3.super.sendToDimension(dimension, message);
    }
}
