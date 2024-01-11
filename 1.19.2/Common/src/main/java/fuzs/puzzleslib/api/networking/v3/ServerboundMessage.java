package fuzs.puzzleslib.api.networking.v3;

/**
 * Template for a message sent by clients and received on the server.
 *
 * @param <T> message type
 */
public interface ServerboundMessage<T extends Record> extends MessageV3<ServerMessageListener<T>> {

}
