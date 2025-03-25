package fuzs.puzzleslib.api.network.v3;

import org.jetbrains.annotations.ApiStatus;

/**
 * Network message template providing a handler that runs when the message is received.
 *
 * @param <T> the implemented message type
 * @param <H> the message listener type
 */
public interface MessageV3<T, H> {

    /**
     * Create a handler for this message, usually {@link ClientMessageListener} or {@link ServerMessageListener}.
     *
     * @return the handler instance
     */
    H getHandler();

    /**
     * An internal helper for supporting {@link fuzs.puzzleslib.api.network.v2.MessageV2}.
     *
     * @return this instance for serialization
     */
    @ApiStatus.Internal
    default T unwrap() {
        return (T) this;
    }
}
