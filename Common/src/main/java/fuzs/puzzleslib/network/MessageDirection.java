package fuzs.puzzleslib.network;

import fuzs.puzzleslib.core.DistType;

/**
 * just like net.minecraftforge.network.NetworkDirection, only for registering messages
 * we use this because the naming is very intuitive, and it stores some useful information
 *
 * @deprecated no longer needed in new implementation
 */
@Deprecated(forRemoval = true)
public enum MessageDirection {
    /**
     * message heading to client
     */
    TO_CLIENT(DistType.CLIENT),
    /**
     * message heading to server
     */
    TO_SERVER(DistType.SERVER);

    /**
     * side this direction is received on
     */
    private final DistType receptionSide;

    /**
     * @param receptionSide side this direction is received on
     */
    MessageDirection(DistType receptionSide) {
        this.receptionSide = receptionSide;
    }

    /**
     * @return side this direction is received on
     */
    public DistType getReceptionSide() {
        return this.receptionSide;
    }

    /**
     * @return the other side duh
     */
    public MessageDirection other() {
        return this == TO_CLIENT ? TO_SERVER : TO_CLIENT;
    }
}
