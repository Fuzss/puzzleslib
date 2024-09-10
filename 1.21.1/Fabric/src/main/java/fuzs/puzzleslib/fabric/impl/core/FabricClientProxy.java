package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.fabric.mixin.client.accessor.MultiPlayerGameModeFabricAccessor;
import fuzs.puzzleslib.impl.core.ClientProxyImpl;
import fuzs.puzzleslib.impl.network.codec.CustomPacketPayloadAdapter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public class FabricClientProxy extends FabricServerProxy implements ClientProxyImpl {

    @Override
    public <M1, M2> void registerClientReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<M1>> type, BiConsumer<Throwable, Consumer<Component>> disconnectExceptionally, Function<M1, ClientboundMessage<M2>> messageAdapter) {
        ClientPlayNetworking.registerGlobalReceiver(type,
                (CustomPacketPayloadAdapter<M1> payload, ClientPlayNetworking.Context context) -> {
                    context.client().submit(() -> {
                        Objects.requireNonNull(context.player(), "player is null");
                        ClientboundMessage<M2> message = messageAdapter.apply(payload.unwrap());
                        message.getHandler()
                                .handle(message.unwrap(),
                                        context.client(),
                                        context.player().connection,
                                        context.player(),
                                        context.client().level
                                );
                    }).exceptionally((Throwable throwable) -> {
                        disconnectExceptionally.accept(throwable, context.responseSender()::disconnect);
                        return null;
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
