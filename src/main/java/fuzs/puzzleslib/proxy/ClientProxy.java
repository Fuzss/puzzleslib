package fuzs.puzzleslib.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;

/**
 * client proxy class
 */
public class ClientProxy extends ServerProxy {

    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public Object getClientInstance() {
        return Minecraft.getInstance();
    }

    @Override
    public boolean hasControlDown() {
        return Screen.hasControlDown();
    }

    @Override
    public boolean hasShiftDown() {
        return Screen.hasShiftDown();
    }

    @Override
    public boolean hasAltDown() {
        return Screen.hasAltDown();
    }
}
