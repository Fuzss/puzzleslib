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

    PlayerEntity getClientPlayer();

    Object getClientInstance();

    MinecraftServer getGameServer();

}
