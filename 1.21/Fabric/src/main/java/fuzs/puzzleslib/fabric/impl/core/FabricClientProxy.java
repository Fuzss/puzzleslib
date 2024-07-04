package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.CustomPacketPayloadAdapter;
import fuzs.puzzleslib.fabric.mixin.client.accessor.MultiPlayerGameModeFabricAccessor;
import fuzs.puzzleslib.impl.core.ClientProxyImpl;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

public class FabricClientProxy extends FabricServerProxy implements ClientProxyImpl {

    @Override
    public <M1, M2> void registerClientReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<M1>> type, Function<M1, ClientboundMessage<M2>> adapter) {
        ClientPlayNetworking.registerGlobalReceiver(type,
                (CustomPacketPayloadAdapter<M1> payload, ClientPlayNetworking.Context context) -> {
                    context.client().execute(() -> {
                        Objects.requireNonNull(context.player(), "player is null");
                        ClientboundMessage<M2> message = adapter.apply(payload.unwrap());
                        message.getHandler()
                                .handle((M2) message,
                                        context.client(),
                                        context.player().connection,
                                        context.player(),
                                        context.client().level
                                );
                    });
                }
        );
    }

    @Override
    public boolean shouldStartDestroyBlock(BlockPos blockPos) {
        MultiPlayerGameModeFabricAccessor gameMode = (MultiPlayerGameModeFabricAccessor) Minecraft.getInstance().gameMode;
        return !gameMode.puzzleslib$getIsDestroying() || !gameMode.puzzleslib$callSameDestroyTarget(blockPos);
    }

    @Override
    public void startClientPrediction(Level level, IntFunction<Packet<ServerGamePacketListener>> predictiveAction) {
        ((MultiPlayerGameModeFabricAccessor) Minecraft.getInstance().gameMode).puzzleslib$callStartPrediction((ClientLevel) level,
                predictiveAction::apply
        );
    }
}
