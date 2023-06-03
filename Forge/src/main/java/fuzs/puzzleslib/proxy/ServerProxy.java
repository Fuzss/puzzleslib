package fuzs.puzzleslib.proxy;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * server proxy class
 */
public class ServerProxy implements IProxy {

    @Override
    public Player getClientPlayer() {
        return null;
    }

    @Override
    public Object getClientInstance() {
        return null;
    }

    @Override
    public MinecraftServer getGameServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public boolean hasControlDown() {
        return false;
    }

    @Override
    public boolean hasShiftDown() {
        return false;
    }

    @Override
    public boolean hasAltDown() {
        return false;
    }
}
