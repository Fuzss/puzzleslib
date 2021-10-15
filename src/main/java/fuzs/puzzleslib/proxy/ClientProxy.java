package fuzs.puzzleslib.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

/**
 * client proxy class
 */
public class ClientProxy extends ServerProxy {

    @Override
    public PlayerEntity getClientPlayer() {

        return Minecraft.getInstance().player;
    }

    @Override
    public Object getClientInstance() {

        return Minecraft.getInstance();
    }

}
