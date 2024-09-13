package fuzs.puzzleslib.api.client.gui.v2.screen;

import net.minecraft.client.Minecraft;

/**
 * A {@link net.minecraft.client.gui.screens.Screen} related helper class.
 */
public final class ScreenHelper {

    private ScreenHelper() {
        // NO-OP
    }

    /**
     * @return current mouse x position
     */
    public static int getMouseX() {
        Minecraft minecraft = Minecraft.getInstance();
        return (int) (minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() /
                minecraft.getWindow().getScreenWidth());
    }

    /**
     * @return current mouse y position
     */
    public static int getMouseY() {
        Minecraft minecraft = Minecraft.getInstance();
        return (int) (minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() /
                minecraft.getWindow().getScreenHeight());
    }

    /**
     * Checks if the mouse cursor is hovering a defined region.
     *
     * @param posX   the x-start of the hover area
     * @param posY   the y-start of the hover area
     * @param width  the width from x-start of the hover area
     * @param height the height from y-start of the hover area
     * @param mouseX mouse x position
     * @param mouseY mouse y position
     * @return is the mouse cursor hovering the defined region
     */
    public static boolean isHovering(int posX, int posY, int width, int height, double mouseX, double mouseY) {
        return mouseX >= posX && mouseX < posX + width && mouseY >= posY && mouseY < posY + height;
    }
}
