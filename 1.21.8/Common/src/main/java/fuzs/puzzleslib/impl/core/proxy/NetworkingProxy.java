package fuzs.puzzleslib.impl.core.proxy;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.network.ConfigurationTask;

public interface NetworkingProxy {

    boolean hasChannel(PacketListener packetListener, CustomPacketPayload.Type<?> type);

    default ClientGamePacketListener getClientPacketListener() {
        throw new RuntimeException("Client connection accessed for wrong side!");
    }

    Connection getConnection(PacketListener packetListener);

    Packet<ClientCommonPacketListener> toClientboundPacket(CustomPacketPayload payload);

    Packet<ServerCommonPacketListener> toServerboundPacket(CustomPacketPayload payload);

    void finishConfigurationTask(ServerConfigurationPacketListener packetListener, ConfigurationTask.Type type);
}
