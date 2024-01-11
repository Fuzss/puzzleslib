package fuzs.puzzleslib.api.network.v3;

/**
 * Network message template providing a handler that runs when the message is received.
 */
public interface MessageV3<T> {

    /**
     * Create a handler for this message, usually {@link ClientMessageListener} or {@link ServerMessageListener}.
     *
     * @return the handler instance
     */
    T getHandler();
}
