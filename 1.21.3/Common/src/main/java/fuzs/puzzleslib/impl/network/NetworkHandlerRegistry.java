package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import org.jetbrains.annotations.ApiStatus;

public interface NetworkHandlerRegistry extends NetworkHandler {

    @ApiStatus.Internal
    @Override
    <T> Packet<ClientCommonPacketListener> toClientboundPacket(ClientboundMessage<T> message);

    @ApiStatus.Internal
    @Override
    <T> Packet<ServerCommonPacketListener> toServerboundPacket(ServerboundMessage<T> message);

    @ApiStatus.Internal
    @Override
    <T> void sendMessage(PlayerSet playerSet, ClientboundMessage<T> message);

    @ApiStatus.Internal
    @Override
    <T> void sendMessage(ServerboundMessage<T> message);
}
