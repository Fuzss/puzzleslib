package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.CustomPacketPayloadAdapter;
import fuzs.puzzleslib.impl.core.ProxyImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public interface FabricProxy extends ProxyImpl {

    <M1, M2> void registerClientReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<M1>> type, BiConsumer<Throwable, Consumer<Component>> disconnectExceptionally, Function<M1, ClientboundMessage<M2>> messageAdapter);

    <M1, M2> void registerServerReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<M1>> type, BiConsumer<Throwable, Consumer<Component>> disconnectExceptionally, Function<M1, ServerboundMessage<M2>> messageAdapter);

    default boolean shouldStartDestroyBlock(BlockPos blockPos) {
        throw new RuntimeException("Should start destroy block accessed for wrong side!");
    }

    default void startClientPrediction(Level level, IntFunction<Packet<ServerGamePacketListener>> predictiveAction) {
        throw new RuntimeException("Start client prediction accessed for wrong side!");
    }
}
