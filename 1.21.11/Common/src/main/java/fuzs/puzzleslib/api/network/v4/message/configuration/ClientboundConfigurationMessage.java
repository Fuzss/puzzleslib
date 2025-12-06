package fuzs.puzzleslib.api.network.v4.message.configuration;

import fuzs.puzzleslib.api.network.v4.NetworkingHelper;
import fuzs.puzzleslib.api.network.v4.message.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;

/**
 * Template for a message sent by the server and received by a client during the configuration phase.
 */
public interface ClientboundConfigurationMessage extends Message<ClientboundConfigurationMessage.Context> {

    @Override
    default Packet<ClientCommonPacketListener> toPacket() {
        return NetworkingHelper.toClientboundPacket(this);
    }

    /**
     * The context provided in {@link #getListener()}.
     */
    interface Context extends Message.Context<ClientConfigurationPacketListenerImpl> {

        /**
         * @return the client
         */
        Minecraft client();
    }
}
