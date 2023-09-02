package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class ForgeClientProxy extends ForgeServerProxy implements ClientProxyImpl {

    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientReceiverV2(T message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            Objects.requireNonNull(player, "player is null");
            message.getHandler().handle(message, minecraft, player.connection, player, minecraft.level);
        });
        context.setPacketHandled(true);
    }
}
