package fuzs.puzzleslib.api.network.v4.message.play;

import fuzs.puzzleslib.api.network.v4.NetworkingHelper;
import fuzs.puzzleslib.api.network.v4.message.Message;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

/**
 * Template for a message sent by the client and received by the server during the play phase.
 */
public interface ServerboundPlayMessage extends Message<ServerboundPlayMessage.Context> {

    @Override
    default Packet<ServerCommonPacketListener> toPacket() {
        return NetworkingHelper.toServerboundPacket(this);
    }

    /**
     * The context provided in {@link #getListener()}.
     */
    interface Context extends Message.Context<ServerGamePacketListenerImpl> {

        /**
         * @return the server
         */
        MinecraftServer server();

        /**
         * @return the player
         */
        ServerPlayer player();

        /**
         * @return the level
         */
        ServerLevel level();
    }
}
