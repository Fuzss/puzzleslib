package fuzs.puzzleslib.api.network.v2;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.impl.network.ClientboundLegacyMessageAdapter;
import fuzs.puzzleslib.impl.network.ServerboundLegacyMessageAdapter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

/**
 * Network message template providing a handler that runs when the message is received.
 * <p>
 * Additionally, controls decoding and encoding directly in the message class.
 *
 * @param <T> the implemented message type
 */
public interface MessageV2<T extends MessageV2<T>> {

    /**
     * Serialize this instance to a byte buffer.
     *
     * @param friendlyByteBuf network data byte buffer
     */
    void write(FriendlyByteBuf friendlyByteBuf);

    /**
     * Deserialize this instance from a byte buffer.
     *
     * @param friendlyByteBuf network data byte buffer
     */
    void read(FriendlyByteBuf friendlyByteBuf);

    /**
     * @return message handler for message on reception side
     */
    MessageHandler<T> makeHandler();

    /**
     * @return this message wrapped as {@link fuzs.puzzleslib.api.network.v3.ClientboundMessage}
     */
    default ClientboundMessage<T> toClientboundMessage() {
        return new ClientboundLegacyMessageAdapter<>((T) this);
    }

    /**
     * @return this message wrapped as {@link fuzs.puzzleslib.api.network.v3.ServerboundMessage}
     */
    default ServerboundMessage<T> toServerboundMessage() {
        return new ServerboundLegacyMessageAdapter<>((T) this);
    }

    /**
     * Handler for received messages.
     * <p>
     * This is implemented as an anonymous class, to force implementations to be a class as well, to prevent issues
     * concerning loading server-only classes on a client.
     *
     * @param <T> the message to handle
     */
    abstract class MessageHandler<T extends MessageV2<T>> {

        /**
         * Called to handle the given message.
         *
         * @param message  the message to handle
         * @param player   the server or client player
         * @param instance the minecraft server or minecraft client instance
         */
        public abstract void handle(T message, Player player, Object instance);
    }
}
