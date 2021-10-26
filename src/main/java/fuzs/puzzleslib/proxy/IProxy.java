package fuzs.puzzleslib.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * proxy base class
 */
public interface IProxy {

    /**
     * @return client player from Minecraft singleton when on physical client, otherwise null
     */
    PlayerEntity getClientPlayer();

    /**
     * @return Minecraft singleton instance on physical client, otherwise null
     */
    Object getClientInstance();

    /**
     * @return current game server, null when not in a world
     */
    MinecraftServer getGameServer();

}
