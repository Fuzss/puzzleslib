package fuzs.puzzleslib.proxy;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
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
}
