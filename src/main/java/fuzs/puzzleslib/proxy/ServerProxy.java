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

    @Override
    public MinecraftServer getInstance() {

        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }

    @Nonnull
    @Override
    public PlayerEntity getPlayer(PlayerEntity player) {

        return player;
    }

}
