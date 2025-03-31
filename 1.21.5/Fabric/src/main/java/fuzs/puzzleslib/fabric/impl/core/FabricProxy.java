package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.level.Level;

import java.util.function.IntFunction;

public interface FabricProxy extends ProxyImpl {

    static FabricProxy get() {
        return (FabricProxy) ProxyImpl.INSTANCE;
    }

    boolean notHidden(String id);

    PayloadTypesContext createPayloadTypesContext(String modId);

    default boolean shouldStartDestroyBlock(BlockPos blockPos) {
        throw new RuntimeException("Should start destroy block accessed for wrong side!");
    }

    default void startClientPrediction(Level level, IntFunction<Packet<ServerGamePacketListener>> predictiveAction) {
        throw new RuntimeException("Start client prediction accessed for wrong side!");
    }
}
