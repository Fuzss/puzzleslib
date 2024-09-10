package fuzs.puzzleslib.api.network.v3;

/**
 * Template for a message sent by clients and received on the server.
 *
 * @param <T> the message type
 */
public interface ServerboundMessage<T> extends MessageV3<T, ServerMessageListener<T>> {

}
