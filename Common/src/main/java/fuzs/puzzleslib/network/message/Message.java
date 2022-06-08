package fuzs.puzzleslib.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

/**
 * network message template
 */
public interface Message {

    /**
     * writes message data to buffer
     * @param buf network data byte buffer
     */
    void write(final FriendlyByteBuf buf);

    /**
     * reads message data from buffer
     * @param buf network data byte buffer
     */
    void read(final FriendlyByteBuf buf);

    /**
     * handles message on receiving side
     * @param player       server or client player
     * @param gameInstance  server or client instance
     */
    default void handle(Player player, Object gameInstance) {
        this.makeHandler().handle(this, player, gameInstance);
    }

    /**
     * @param <T> this message
     * @return packet handler for message
     */
    <T extends Message> PacketHandler<T> makeHandler();

    /**
     * this is a class, so it cannot be implemented as a functional interface to avoid client only calls somehow running into problems on a dedicated server
     * @param <T> this message
     */
    abstract class PacketHandler<T extends Message> {

        /**
         * handle given packet
         * handler implemented as separate class to hopefully avoid invoking client class on the server
         * @param packet packet to handle
         * @param player       server or client player
         * @param gameInstance  server or client instance
         */
        public abstract void handle(T packet, Player player, Object gameInstance);
    }
}
