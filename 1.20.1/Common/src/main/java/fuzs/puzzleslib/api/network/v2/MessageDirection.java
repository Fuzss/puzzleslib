package fuzs.puzzleslib.api.network.v2;

/**
 * just like net.minecraftforge.network.NetworkDirection, only for registering messages
 * we use this because the naming is very intuitive, and it stores some useful information
 */
@Deprecated(forRemoval = true)
public enum MessageDirection {
    /**
     * message heading to client
     */
    TO_CLIENT,
    /**
     * message heading to server
     */
    TO_SERVER
}
