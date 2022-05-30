package fuzs.puzzleslib.proxy;

import fuzs.puzzleslib.core.EnvTypeExecutor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

/**
 * proxy base class
 */
public interface IProxy {
    /**
     * sided proxy depending on physical side
     */
    @SuppressWarnings("Convert2MethodRef")
    IProxy INSTANCE = EnvTypeExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    /**
     * @return client player from Minecraft singleton when on physical client, otherwise null
     */
    Player getClientPlayer();

    /**
     * @return Minecraft singleton instance on physical client, otherwise null
     */
    Object getClientInstance();

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
