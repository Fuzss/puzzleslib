package fuzs.puzzleslib.api.network.v2;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

/**
 * network message template
 *
 * @param <T> the message type for the handler
 */
public interface MessageV2<T extends MessageV2<T>> {

    /**
     * writes message data to buffer
     *
     * @param buf    network data byte buffer
     */
    void write(final FriendlyByteBuf buf);

    /**
     * reads message data from buffer
     *
     * @param buf    network data byte buffer
     */
    void read(final FriendlyByteBuf buf);

    /**
     * @return message handler for message on reception side
     */
    MessageHandler<T> makeHandler();

    /**
     * this is a class, so it cannot be implemented as a functional interface to avoid client only calls somehow running into problems on a dedicated server
     *
     * @param <T>   this message
     */
    abstract class MessageHandler<T extends MessageV2<T>> {

        /**
         * handle given message
         * handler implemented as separate class to hopefully avoid invoking client class on the server
         *
         * @param message       message to handle
         * @param player        server or client player
         * @param instance  server or client instance
         */
        public abstract void handle(T message, Player player, Object instance);
    }
}
