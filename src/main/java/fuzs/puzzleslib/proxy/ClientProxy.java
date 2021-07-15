package fuzs.puzzleslib.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * client proxy class
 */
public class ClientProxy implements IProxy<Minecraft> {

    /**
     * private singleton constructor
     */
    private ClientProxy() {

    }

    @Override
    public Minecraft getGameInstance() {

        return LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    @Override
    public PlayerEntity getPlayer(@Nullable PlayerEntity player) {

        return player != null ? player : this.getGameInstance().player;
    }

    /**
     * TODO rename this back to #getInstance
     * @return {@link ClientProxy} instance
     */
    public static ClientProxy getInstance2() {

        return ClientProxy.ClientProxyHolder.INSTANCE;
    }

    /**
     * instance holder class for lazy and thread-safe initialization
     */
    private static class ClientProxyHolder {

        private static final ClientProxy INSTANCE = new ClientProxy();

    }

}
