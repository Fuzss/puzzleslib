package fuzs.puzzleslib.api.network.v3;

/**
 * Template for a message sent by the server and received by clients.
 *
 * @param <T> the message type
 */
public interface ClientboundMessage<T> extends MessageV3<T, ClientMessageListener<T>> {

}
