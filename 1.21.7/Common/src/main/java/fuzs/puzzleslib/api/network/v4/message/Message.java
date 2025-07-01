package fuzs.puzzleslib.api.network.v4.message;

import fuzs.puzzleslib.api.network.v4.NetworkingHelper;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Network message template providing a handler that runs when the message is received.
 *
 * @param <T> the message listener context
 */
public interface Message<T extends Message.Context<?>> extends CustomPacketPayload {

    @Override
    default Type<?> type() {
        return NetworkingHelper.getPayloadType(this.getClass());
    }

    /**
     * Create a vanilla packet from the message.
     *
     * @return the packet
     */
    Packet<?> toPacket();

    /**
     * Create a handler for the message, accepting a corresponding {@link Context}.
     *
     * @return the handler instance
     */
    MessageListener<T> getListener();

    /**
     * The context provided in {@link #getListener()}.
     */
    interface Context<T extends PacketListener> {

        /**
         * @return the packet listener
         */
        T packetListener();

        /**
         * Send a reply to the sender.
         *
         * @param payload the payload to return to the sender
         */
        void reply(CustomPacketPayload payload);

        /**
         * Disconnect the client when receiving the message is unsuccessful.
         *
         * @param component the message shown on the disconnection screen
         */
        void disconnect(Component component);
    }
}
