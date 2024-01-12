package fuzs.puzzleslib.forge.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.impl.core.ClientProxyImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.Objects;

public class ForgeClientProxy extends ForgeServerProxy implements ClientProxyImpl {

    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientReceiverV2(T message, CustomPayloadEvent.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Objects.requireNonNull(player, "player is null");
        message.getHandler().handle(message, minecraft, player.connection, player, minecraft.level);
    }
}
