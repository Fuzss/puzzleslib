package fuzs.puzzleslib.api.client.gui.v2.screen;

/**
 * A {@link net.minecraft.client.gui.screens.Screen} related helper class.
 */
@Deprecated
public final class ScreenHelper {

    private ScreenHelper() {
        // NO-OP
    }

    /**
     * @return current mouse x position
     */
    public static int getMouseX() {
        return fuzs.puzzleslib.api.client.gui.v2.ScreenHelper.getMouseX();
    }

    /**
     * @return current mouse y position
     */
    public static int getMouseY() {
        return fuzs.puzzleslib.api.client.gui.v2.ScreenHelper.getMouseY();
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
        return fuzs.puzzleslib.api.client.gui.v2.ScreenHelper.isHovering(posX, posY, width, height, mouseX, mouseY);
    }
}
