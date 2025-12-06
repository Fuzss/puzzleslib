package fuzs.puzzleslib.api.client.gui.v2;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * A {@link net.minecraft.client.gui.screens.Screen} related helper class.
 */
public final class ScreenHelper {

    private ScreenHelper() {
        // NO-OP
    }

    /**
     * Get the current partial tick time; respecting whether the game is paused.
     *
     * @return the current partial tick time
     */
    public static float getPartialTick() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
    }

    /**
     * @return the current mouse x-position
     */
    public static int getMouseX() {
        Minecraft minecraft = Minecraft.getInstance();
        return (int) (minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow()
                .getScreenWidth());
    }

    /**
     * @return the current mouse y-position
     */
    public static int getMouseY() {
        Minecraft minecraft = Minecraft.getInstance();
        return (int) (minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow()
                .getScreenHeight());
    }

    /**
     * Checks if the mouse cursor is hovering in a defined region.
     *
     * @param posX   the x-start of the hover area
     * @param posY   the y-start of the hover area
     * @param width  the width from x-start of the hover area
     * @param height the height from y-start of the hover area
     * @param mouseX the mouse x-position
     * @param mouseY the mouse y-position
     * @return is the mouse cursor hovering the defined region
     */
    public static boolean isHovering(int posX, int posY, int width, int height, double mouseX, double mouseY) {
        return mouseX >= posX && mouseX < posX + width && mouseY >= posY && mouseY < posY + height;
    }

    /**
     * Can a mob effect render in the player inventory via
     * {@link net.minecraft.client.gui.screens.inventory.EffectsInInventory}.
     *
     * @param mobEffect the mob effect
     * @return is rendering permitted
     */
    public static boolean isEffectVisibleInInventory(MobEffectInstance mobEffect) {
        return ClientProxyImpl.get().isEffectVisibleInInventory(mobEffect);
    }

    /**
     * Can a mob effect render in the {@link Gui}.
     *
     * @param mobEffect the mob effect
     * @return is rendering permitted
     */
    public static boolean isEffectVisibleInGui(MobEffectInstance mobEffect) {
        return ClientProxyImpl.get().isEffectVisibleInGui(mobEffect);
    }
}
