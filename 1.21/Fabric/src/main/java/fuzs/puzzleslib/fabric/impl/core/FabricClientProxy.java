package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.network.v2.MessageV2;
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

import java.util.function.IntFunction;

public class FabricClientProxy extends FabricServerProxy implements ClientProxyImpl {

    @Override
    public <T extends MessageV2<T>> void registerLegacyClientReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type) {
        ClientPlayNetworking.registerGlobalReceiver(type, (CustomPacketPayloadAdapter<T> payload, ClientPlayNetworking.Context context) -> {
            context.client().execute(() -> {
                payload.unwrap().makeHandler().handle(payload.unwrap(), context.player(), context.client());
            });
        });
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type) {
        ClientPlayNetworking.registerGlobalReceiver(type, (CustomPacketPayloadAdapter<T> payload, ClientPlayNetworking.Context context) -> {
            context.client().execute(() -> {
                payload.unwrap().getHandler().handle(payload.unwrap(), context.client(), context.player().connection, context.player(), context.client().level);
            });
        });
    }

    @Override
    public boolean shouldStartDestroyBlock(BlockPos blockPos) {
        MultiPlayerGameModeFabricAccessor gameMode = (MultiPlayerGameModeFabricAccessor) Minecraft.getInstance().gameMode;
        return !gameMode.puzzleslib$getIsDestroying() || !gameMode.puzzleslib$callSameDestroyTarget(blockPos);
    }

    @Override
    public void startClientPrediction(Level level, IntFunction<Packet<ServerGamePacketListener>> predictiveAction) {
        ((MultiPlayerGameModeFabricAccessor) Minecraft.getInstance().gameMode).puzzleslib$callStartPrediction((ClientLevel) level, predictiveAction::apply);
    }
}
