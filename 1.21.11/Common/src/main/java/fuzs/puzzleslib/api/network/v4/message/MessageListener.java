package fuzs.puzzleslib.api.network.v4.message;

import java.util.function.Consumer;

/**
 * Handler for received messages.
 * <p>
 * This is implemented as a class, to force any implementation to be a class as well. This is to prevent issues with
 * loading client-only classes on the server.
 *
 * @param <T> the message context type
 */
public abstract class MessageListener<T extends Message.Context<?>> implements Consumer<T> {

}
