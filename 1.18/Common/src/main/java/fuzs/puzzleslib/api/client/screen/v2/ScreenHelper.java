package fuzs.puzzleslib.api.client.screen.v2;

import fuzs.puzzleslib.impl.client.core.ClientFactories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A helper class for accessing encapsulated fields on a screen.
 * On Forge those are all exposed via added getter methods, but Fabric requires mixin accessors.
 */
public interface ScreenHelper {
    ScreenHelper INSTANCE = ClientFactories.INSTANCE.getScreenHelper();

    /**
     * @param screen screen instance
     * @return minecraft singleton
     */
    Minecraft getMinecraft(Screen screen);

    /**
     * @param screen screen instance
     * @return font renderer
     */
    Font getFont(Screen screen);

    /**
     * @param screen screen instance
     * @return item renderer
     */
    @Deprecated(forRemoval = true)
    ItemRenderer getItemRenderer(Screen screen);

    /**
     * @param screen container screen instance
     * @return width of container interface
     */
    int getImageWidth(AbstractContainerScreen<?> screen);

    /**
     * @param screen container screen instance
     * @return height of container interface
     */
    int getImageHeight(AbstractContainerScreen<?> screen);

    /**
     * @param screen container screen instance
     * @return left position of container interface
     */
    int getLeftPos(AbstractContainerScreen<?> screen);

    /**
     * @param screen container screen instance
     * @return top position of container interface
     */
    int getTopPos(AbstractContainerScreen<?> screen);

    /**
     * @param screen container screen instance
     * @return the slot the mouse is currently hovering (which an item tooltip is shown for and where the hovered slot overlay is drawn)
     */
    @Nullable Slot getHoveredSlot(AbstractContainerScreen<?> screen);

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
        return (int) (minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth());
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
        return (int) (minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight());
    }
}
