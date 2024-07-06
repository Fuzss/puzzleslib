package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.impl.core.ClientProxyImpl;
import fuzs.puzzleslib.impl.network.codec.CustomPacketPayloadAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class NeoForgeClientProxy extends NeoForgeServerProxy implements ClientProxyImpl {

    @Override
    public <M1, M2> CompletableFuture<Void> registerClientReceiver(CustomPacketPayloadAdapter<M1> payload, IPayloadContext context, Function<M1, ClientboundMessage<M2>> adapter) {
        return context.enqueueWork(() -> {
            LocalPlayer player = (LocalPlayer) context.player();
            Objects.requireNonNull(player, "player is null");
            ClientboundMessage<M2> message = adapter.apply(payload.unwrap());
            message.getHandler()
                    .handle(message.unwrap(), Minecraft.getInstance(), player.connection, player, player.clientLevel);
        });
    }
}
