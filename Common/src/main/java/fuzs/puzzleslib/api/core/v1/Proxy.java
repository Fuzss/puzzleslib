package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.impl.core.CommonFactories;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
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
     * Used to check if the control key (command on Mac) is pressed, useful for item tooltips.
     * <p>Always returns <code>false</code> on the server side.
     */
    boolean hasControlDown();

    /**
     * Used to check if the shift key is pressed, useful for item tooltips.
     * <p>Always returns <code>false</code> on the server side.
     *
     * @return is the shift key pressed
     */
    boolean hasShiftDown();

    /**
     * Used to check if the alt key is pressed, useful for item tooltips.
     * <p>Always returns <code>false</code> on the server side.
     *
     * @return is the alt key pressed
     */
    boolean hasAltDown();

    /**
     * Retrieves the name of the currently set key for a <code>net.minecraft.client.KeyMapping</code>.
     * <p>Returns an empty component on the server-side.
     *
     * @param identifier the key identifier from <code>net.minecraft.client.KeyMapping#getName</code>
     * @return the component or an empty component
     */
    Component getKeyMappingComponent(String identifier);
}
