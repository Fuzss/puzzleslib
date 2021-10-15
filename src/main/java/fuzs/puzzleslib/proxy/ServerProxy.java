package fuzs.puzzleslib.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

/**
 * server proxy class
 */
public class ServerProxy implements IProxy {

    @Override
    public PlayerEntity getClientPlayer() {

        return null;
    }

    @Override
    public Object getClientInstance() {

        return null;
    }

    @Override
    public MinecraftServer getGameServer() {

        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }

}
