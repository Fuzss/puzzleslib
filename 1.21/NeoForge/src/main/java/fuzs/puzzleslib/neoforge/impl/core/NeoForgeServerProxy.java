package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.CustomPacketPayloadAdapter;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class NeoForgeServerProxy implements NeoForgeProxy {

    @Override
    public <M1, M2> CompletableFuture<Void> registerClientReceiver(CustomPacketPayloadAdapter<M1> payload, IPayloadContext context, Function<M1, ClientboundMessage<M2>> adapter) {
        return CompletableFuture.allOf();
    }

    @Override
    public <M1, M2> CompletableFuture<Void> registerServerReceiver(CustomPacketPayloadAdapter<M1> payload, IPayloadContext context, Function<M1, ServerboundMessage<M2>> adapter) {
        return context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            ServerboundMessage<M2> message = adapter.apply(payload.unwrap());
            message.getHandler().handle((M2) message, player.server, player.connection, player, player.serverLevel());
        });
    }
}
