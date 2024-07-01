package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.impl.core.ClientProxyImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class NeoForgeClientProxy extends NeoForgeServerProxy implements ClientProxyImpl {

    @Override
    public <T extends Record & ClientboundMessage<T>> CompletableFuture<Void> registerClientReceiverV2(T message, PlayPayloadContext context) {
        return context.workHandler().submitAsync(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            Objects.requireNonNull(player, "player is null");
            message.getHandler().handle(message, minecraft, player.connection, player, minecraft.level);
        });
    }
}
