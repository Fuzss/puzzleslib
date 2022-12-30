package fuzs.puzzleslib.api.networking.v3;

/**
 * Template for a message sent by the server and received by clients.
 *
 * @param <T> message type
 */
public interface ClientboundMessage<T extends Record> extends Message<ClientMessageListener<T>> {

}
