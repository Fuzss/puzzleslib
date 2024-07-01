package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.concurrent.CompletableFuture;

public class NeoForgeServerProxy implements NeoForgeProxy {

    @Override
    public <T extends Record & ClientboundMessage<T>> CompletableFuture<Void> registerClientReceiverV2(T message, PlayPayloadContext context) {
        return CompletableFuture.allOf();
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> CompletableFuture<Void> registerServerReceiverV2(T message, PlayPayloadContext context) {
        return context.workHandler().submitAsync(() -> {
            ServerPlayer player = context.player().map(ServerPlayer.class::cast).orElseThrow(() -> new NullPointerException("player is null"));
            message.getHandler().handle(message, CommonAbstractions.INSTANCE.getMinecraftServer(), player.connection, player, player.serverLevel());
        });
    }
}
