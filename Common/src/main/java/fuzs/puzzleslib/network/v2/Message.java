package fuzs.puzzleslib.network.v2;

/**
 * network message template
 */
public interface Message<T> {

    /**
     * @return message handler for message, should be ok to implement as anonymous class
     */
    T getHandler();
}
