package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.CustomPacketPayloadAdapter;
import fuzs.puzzleslib.impl.core.ProxyImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.level.Level;

import java.util.function.IntFunction;

public interface FabricProxy extends ProxyImpl {

    <T extends MessageV2<T>> void registerLegacyClientReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type);

    <T extends MessageV2<T>> void registerLegacyServerReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type);
    
    <T extends Record & ClientboundMessage<T>> void registerClientReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type);

    <T extends Record & ServerboundMessage<T>> void registerServerReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type);

    default boolean shouldStartDestroyBlock(BlockPos blockPos) {
        throw new RuntimeException("Should start destroy block accessed for wrong side!");
    }

    default void startClientPrediction(Level level, IntFunction<Packet<ServerGamePacketListener>> predictiveAction) {
        throw new RuntimeException("Start client prediction accessed for wrong side!");
    }
}
