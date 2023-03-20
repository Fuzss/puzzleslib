package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface NetworkHandlerRegistry extends NetworkHandlerV3 {

    @Deprecated
    @Override
    <T extends Record & ClientboundMessage<T>> Packet<?> toClientboundPacket(T message);

    @Deprecated
    @Override
    <T extends Record & ServerboundMessage<T>> Packet<?> toServerboundPacket(T message);

    @Deprecated
    @Override
    default <T extends Record & ServerboundMessage<T>> void sendToServer(T message) {
        NetworkHandlerV3.super.sendToServer(message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendTo(T message, ServerPlayer player) {
        NetworkHandlerV3.super.sendTo(message, player);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAll(T message) {
        NetworkHandlerV3.super.sendToAll(message);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllExcept(T message, ServerPlayer exclude) {
        NetworkHandlerV3.super.sendToAllExcept(message, exclude);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(T message, BlockPos pos, Level level) {
        NetworkHandlerV3.super.sendToAllNear(message, pos, level);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllNear(T message, double posX, double posY, double posZ, double distance, Level level) {
        NetworkHandlerV3.super.sendToAllNear(message, posX, posY, posZ, distance, level);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllNearExcept(T message, @Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, Level level) {
        NetworkHandlerV3.super.sendToAllNearExcept(message, exclude, posX, posY, posZ, distance, level);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllTracking(T message, Entity entity) {
        NetworkHandlerV3.super.sendToAllTracking(message, entity);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToAllTrackingAndSelf(T message, Entity entity) {
        NetworkHandlerV3.super.sendToAllTrackingAndSelf(message, entity);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToDimension(T message, Level level) {
        NetworkHandlerV3.super.sendToDimension(message, level);
    }

    @Deprecated
    @Override
    default <T extends Record & ClientboundMessage<T>> void sendToDimension(T message, ResourceKey<Level> dimension) {
        NetworkHandlerV3.super.sendToDimension(message, dimension);
    }
}
