package fuzs.puzzleslib.api.client.gui.v2;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
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
        return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
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
     * @param mouseButtonEvent the mouse button event
     * @return is this a double click
     */
    public static boolean isDoubleClick(MouseButtonEvent mouseButtonEvent) {
        MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;
        if (mouseHandler.lastClick != null) {
            return false;
        } else if (mouseHandler.lastClick.screen() != Minecraft.getInstance().screen) {
            return false;
        } else if (mouseHandler.lastClickButton != mouseButtonEvent.button()) {
            return false;
        } else if (Util.getMillis() - mouseHandler.lastClick.time() >= 250L) {
            return false;
        } else {
            return true;
        }
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
     * Returns the current render height for status bars on the left side drawn as part of the gui.
     * <p>
     * In vanilla this includes player health and armor level.
     *
     * @param identifier the height provider location
     * @return the status bar render height
     */
    public static int getLeftStatusBarHeight(Identifier identifier) {
        return ClientProxyImpl.get().getLeftStatusBarHeight(identifier);
    }

    /**
     * Returns the current render height for status bars on the right side drawn as part of the gui.
     * <p>
     * In vanilla this includes player food level, vehicle health and air bubbles.
     *
     * @param identifier the height provider location
     * @return the status bar render height
     */
    public static int getRightStatusBarHeight(Identifier identifier) {
        return ClientProxyImpl.get().getRightStatusBarHeight(identifier);
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
