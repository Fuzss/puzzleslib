package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

import java.util.Collection;

public record ClientboundModListMessage(Collection<String> modList) implements ClientboundMessage<ClientboundModListMessage> {

    @Override
    public ClientMessageListener<ClientboundModListMessage> getHandler() {
        return new ClientMessageListener<>() {

            @Override
            public void handle(ClientboundModListMessage message, Minecraft minecraft, ClientPacketListener clientPacketListener, LocalPlayer player, ClientLevel clientLevel) {
                ModContext.acceptServersideMods(message.modList);
            }
        };
    }
}
