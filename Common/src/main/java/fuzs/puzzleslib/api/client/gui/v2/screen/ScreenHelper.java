package fuzs.puzzleslib.api.client.gui.v2.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A helper class for accessing encapsulated fields on a screen.
 *
 * @deprecated replaced with {@link ScreenHelperV2}
 */
@Deprecated(forRemoval = true)
public interface ScreenHelper {
    ScreenHelper INSTANCE = new ScreenHelper() {
        // NO-OP
    };

    /**
     * @param screen screen instance
     * @return minecraft singleton
     */
    default Minecraft getMinecraft(Screen screen) {
        Objects.requireNonNull(screen, "screen is null");
        return screen.minecraft;
    }

    /**
     * @param screen screen instance
     * @return font renderer
     */
    default Font getFont(Screen screen) {
        Objects.requireNonNull(screen, "screen is null");
        return screen.font;
    }

    /**
     * @param screen container screen instance
     * @return width of container interface
     */
    default int getImageWidth(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return screen.imageWidth;
    }

    /**
     * @param screen container screen instance
     * @return height of container interface
     */
    default int getImageHeight(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return screen.imageHeight;
    }

    /**
     * @param screen container screen instance
     * @return left position of container interface
     */
    default int getLeftPos(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return screen.leftPos;
    }

    /**
     * @param screen container screen instance
     * @return top position of container interface
     */
    default int getTopPos(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return screen.topPos;
    }

    /**
     * @param screen container screen instance
     * @return the slot the mouse is currently hovering (which an item tooltip is shown for and where the hovered slot
     *         overlay is drawn)
     */
    @Nullable
    default Slot getHoveredSlot(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "screen is null");
        return screen.hoveredSlot;
    }

    /**
     * @param screen screen instance
     * @return current mouse x position
     */
    default int getMouseX(Screen screen) {
        Objects.requireNonNull(screen, "screen is null");
        return this.getMouseX(this.getMinecraft(screen));
    }

    /**
     * @param minecraft minecraft singleton
     * @return current mouse x position
     */
    default int getMouseX(Minecraft minecraft) {
        Objects.requireNonNull(minecraft, "minecraft is null");
        return (int) (minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow()
                .getScreenWidth());
    }

    /**
     * @param screen screen instance
     * @return current mouse y position
     */
    default int getMouseY(Screen screen) {
        Objects.requireNonNull(screen, "screen is null");
        return this.getMouseY(this.getMinecraft(screen));
    }

    /**
     * @param minecraft minecraft singleton
     * @return current mouse y position
     */
    default int getMouseY(Minecraft minecraft) {
        Objects.requireNonNull(minecraft, "minecraft is null");
        return (int) (minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow()
                .getScreenHeight());
    }

    /**
     * Iterates of all slots owned by a container to find if any of them is hovered.
     *
     * @param screen the current container screen to source slots from
     * @param mouseX mouse x position
     * @param mouseY mouse y position
     * @return the active slot or null
     */
    @Nullable
    default Slot findSlot(AbstractContainerScreen<?> screen, double mouseX, double mouseY) {
        return screen.findSlot(mouseX, mouseY);
    }

    /**
     * Checks if the mouse cursor is hovering over a given slot.
     *
     * @param screen the current container screen owning the slot
     * @param slot   the slot to check if hovered
     * @param mouseX mouse x position
     * @param mouseY mouse y position
     * @return is the mouse cursor hovering the slot
     */
    default boolean isHovering(AbstractContainerScreen<?> screen, Slot slot, double mouseX, double mouseY) {
        return screen.isHovering(slot, mouseX, mouseY);
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
    default boolean isHovering(int posX, int posY, int width, int height, double mouseX, double mouseY) {
        return mouseX >= posX && mouseX < posX + width && mouseY >= posY && mouseY < posY + height;
    }
}
