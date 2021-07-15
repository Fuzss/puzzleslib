package fuzs.puzzleslib.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nonnull;

/**
 * server proxy class
 */
public class ServerProxy implements IProxy<MinecraftServer> {

    /**
     * private singleton constructor
     */
    private ServerProxy() {

    }

    @Override
    public MinecraftServer getGameInstance() {

        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }

    @Nonnull
    @Override
    public PlayerEntity getPlayer(PlayerEntity player) {

        return player;
    }

    /**
     * TODO rename this back to #getInstance
     * @return {@link ServerProxy} instance
     */
    public static ServerProxy getInstance2() {

        return ServerProxy.ServerProxyHolder.INSTANCE;
    }

    /**
     * instance holder class for lazy and thread-safe initialization
     */
    private static class ServerProxyHolder {

        private static final ServerProxy INSTANCE = new ServerProxy();

    }

}
