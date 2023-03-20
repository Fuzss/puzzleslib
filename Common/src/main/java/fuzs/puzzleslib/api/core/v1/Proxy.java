package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.impl.core.CommonFactories;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * proxy base class for client and server implementations
 * mainly used for handling content not present on a physical server
 */
public interface Proxy {
    /**
     * sided proxy depending on physical side
     */
    @SuppressWarnings("Convert2MethodRef")
    Proxy INSTANCE = DistTypeExecutor.getForDistType(() -> CommonFactories.INSTANCE.getClientProxy(), () -> CommonFactories.INSTANCE.getServerProxy());

    /**
     * @return client player from Minecraft singleton when on physical client, otherwise null
     */
    Player getClientPlayer();

    /**
     * @return client level from Minecraft singleton when on physical client, otherwise null
     */
    Level getClientLevel();

    /**
     * @return Minecraft singleton instance on physical client, otherwise null
     */
    Object getClientInstance();

    /**
     * @return the connection to the server on physical client, otherwise null
     */
    Connection getClientConnection();

    /**
     * @return current game server, null when not in a world
     */
    MinecraftServer getGameServer();

    /**
     * useful for item tooltips
     * @return is the control key (command on mac) pressed
     */
    boolean hasControlDown();

    /**
     * useful for item tooltips
     * @return is the shift key pressed
     */
    boolean hasShiftDown();

    /**
     * useful for item tooltips
     * @return is the alt key pressed
     */
    boolean hasAltDown();
}
