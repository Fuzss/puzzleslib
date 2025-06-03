package fuzs.puzzleslib.api.network.v4.message.configuration;

import fuzs.puzzleslib.api.network.v4.NetworkingHelper;
import fuzs.puzzleslib.api.network.v4.message.Message;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

/**
 * Template for a message sent by the client and received by the server during the configuration phase.
 */
public interface ServerboundConfigurationMessage extends Message<ServerboundConfigurationMessage.Context> {

    @Override
    default Packet<ServerCommonPacketListener> toPacket() {
        return NetworkingHelper.toServerboundPacket(this);
    }

    /**
     * The context provided in {@link #getListener()}.
     */
    interface Context extends Message.Context<ServerConfigurationPacketListenerImpl> {

        /**
         * @return the server
         */
        MinecraftServer server();
    }
}
