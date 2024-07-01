package fuzs.puzzleslib.forge.impl.core;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.Objects;

public class ForgeServerProxy implements ForgeProxy {

    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientReceiverV2(T message, CustomPayloadEvent.Context context) {
        // NO-OP
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> void registerServerReceiverV2(T message, CustomPayloadEvent.Context context) {
        ServerPlayer player = context.getSender();
        Objects.requireNonNull(player, "player is null");
        message.getHandler().handle(message, CommonAbstractions.INSTANCE.getMinecraftServer(), player.connection, player, player.serverLevel());
    }
}
