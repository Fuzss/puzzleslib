package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class ForgeServerProxy implements ForgeProxy {

    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientReceiverV2(T message, Supplier<NetworkEvent.Context> supplier) {

    }

    @Override
    public <T extends Record & ServerboundMessage<T>> void registerServerReceiverV2(T message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            Objects.requireNonNull(player, "player is null");
            message.getHandler().handle(message, this.getGameServer(), player.connection, player, player.serverLevel());
        });
        context.setPacketHandled(true);
    }
}
