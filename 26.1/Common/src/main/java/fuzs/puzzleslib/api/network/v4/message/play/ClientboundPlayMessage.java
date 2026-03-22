package fuzs.puzzleslib.api.network.v4.message.play;

import fuzs.puzzleslib.api.network.v4.NetworkingHelper;
import fuzs.puzzleslib.api.network.v4.message.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;

/**
 * Template for a message sent by the server and received by a client during the play phase.
 */
public interface ClientboundPlayMessage extends Message<ClientboundPlayMessage.Context> {

    @Override
    default Packet<ClientCommonPacketListener> toPacket() {
        return NetworkingHelper.toClientboundPacket(this);
    }

    /**
     * The context provided in {@link #getListener()}.
     */
    interface Context extends Message.Context<ClientPacketListener> {

        /**
         * @return the client
         */
        Minecraft client();

        /**
         * @return the player
         */
        LocalPlayer player();

        /**
         * @return the level
         */
        ClientLevel level();
    }
}
