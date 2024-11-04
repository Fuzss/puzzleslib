package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;

public interface NetworkHandlerRegistry extends NetworkHandler {

    @Deprecated
    @Override
    <T> Packet<ClientCommonPacketListener> toClientboundPacket(ClientboundMessage<T> message);

    @Deprecated
    @Override
    <T> Packet<ServerCommonPacketListener> toServerboundPacket(ServerboundMessage<T> message);

    @Deprecated
    @Override
    <T> void sendMessage(PlayerSet playerSet, ClientboundMessage<T> message);

    @Deprecated
    @Override
    <T> void sendMessage(ServerboundMessage<T> message);
}
