package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

public record ClientboundLegacyMessageAdapter<T extends MessageV2<T>>(T message) implements ClientboundMessage<T> {

    @Override
    public ClientMessageListener<T> getHandler() {
        return new ClientMessageListener<>() {

            @Override
            public void handle(T message, Minecraft client, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
                message.makeHandler().handle(message, player, client);
            }
        };
    }

    @Override
    public T unwrap() {
        return this.message;
    }
}
