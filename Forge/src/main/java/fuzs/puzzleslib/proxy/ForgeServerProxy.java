package fuzs.puzzleslib.proxy;

import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ForgeServerProxy implements Proxy {

    @Override
    public Player getClientPlayer() {
        return null;
    }

    @Override
    public Level getClientLevel() {
        return null;
    }

    @Override
    public Object getClientInstance() {
        return null;
    }

    @Override
    public Connection getClientConnection() {
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
