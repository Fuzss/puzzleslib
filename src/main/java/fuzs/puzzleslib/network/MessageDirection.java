package fuzs.puzzleslib.network;

import net.minecraftforge.fml.LogicalSide;

/**
 * just like {@link net.minecraftforge.fmllegacy.network.NetworkDirection}, only for registering messages
 * we use this because the naming is very intuitive, and it stores some useful information
 */
public enum MessageDirection {
    /**
     * message heading to client
     */
    TO_CLIENT(LogicalSide.CLIENT),
    /**
     * message heading to server
     */
    TO_SERVER(LogicalSide.SERVER);

    /**
     * side this direction is received on
     */
    private final LogicalSide receptionSide;

    /**
     * @param receptionSide side this direction is received on
     */
    MessageDirection(LogicalSide receptionSide) {
        this.receptionSide = receptionSide;
    }

    /**
     * @return side this direction is received on
     */
    public LogicalSide getReceptionSide() {
        return this.receptionSide;
    }
}
