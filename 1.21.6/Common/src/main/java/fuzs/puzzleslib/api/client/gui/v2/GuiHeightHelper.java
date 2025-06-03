package fuzs.puzzleslib.api.client.gui.v2;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.gui.Gui;

/**
 * A helper class for handling the render height for gui components rendered directly above the hotbar, such as health
 * and food indicators.
 */
public final class GuiHeightHelper {

    private GuiHeightHelper() {
        // NO-OP
    }

    /**
     * Returns the current render height for hotbar decorations on the left side.
     * <p>
     * In vanilla this includes player health and armor level.
     *
     * @param gui the gui instance
     * @return the hotbar decorations render height
     */
    public static int getLeftHeight(Gui gui) {
        return ClientProxyImpl.get().getGuiLeftHeight(gui);
    }

    /**
     * Returns the current render height for hotbar decorations on the right side.
     * <p>
     * In vanilla this includes player food level, vehicle health and air supply.
     *
     * @param gui the gui instance
     * @return the hotbar decorations render height
     */
    public static int getRightHeight(Gui gui) {
        return ClientProxyImpl.get().getGuiRightHeight(gui);
    }

    /**
     * Returns the highest current render height for hotbar decorations on either side.
     *
     * @param gui the gui instance
     * @return the hotbar decorations render height
     */
    public static int getMaxHeight(Gui gui) {
        return Math.max(getLeftHeight(gui), getRightHeight(gui));
    }

    /**
     * Returns the lowest current render height for hotbar decorations on either side.
     *
     * @param gui the gui instance
     * @return the hotbar decorations render height
     */
    public static int getMinHeight(Gui gui) {
        return Math.min(getLeftHeight(gui), getRightHeight(gui));
    }

    /**
     * Set the render height for hotbar decorations on the left side.
     *
     * @param gui        the gui instance
     * @param leftHeight the hotbar decorations render height
     */
    public static void setLeftHeight(Gui gui, int leftHeight) {
        ClientProxyImpl.get().setGuiLeftHeight(gui, leftHeight);
    }

    /**
     * Set the render height for hotbar decorations on the right side.
     *
     * @param gui         the gui instance
     * @param rightHeight the hotbar decorations render height
     */
    public static void setRightHeight(Gui gui, int rightHeight) {
        ClientProxyImpl.get().setGuiRightHeight(gui, rightHeight);
    }

    /**
     * Add to the current render height for hotbar decorations on the left side.
     *
     * @param gui        the gui instance
     * @param leftHeight the additional hotbar decorations render height
     */
    public static void addLeftHeight(Gui gui, int leftHeight) {
        setLeftHeight(gui, getLeftHeight(gui) + leftHeight);
    }

    /**
     * Add to the current render height for hotbar decorations on the right side.
     *
     * @param gui         the gui instance
     * @param rightHeight the additional hotbar decorations render height
     */
    public static void addRightHeight(Gui gui, int rightHeight) {
        setRightHeight(gui, getRightHeight(gui) + rightHeight);
    }
}
