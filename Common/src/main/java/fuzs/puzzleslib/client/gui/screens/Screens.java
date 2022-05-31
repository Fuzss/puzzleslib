package fuzs.puzzleslib.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;

/**
 * a helper class for accessing encapsulated fields on a screen
 * on Forge those are all exposed, but Fabric requires special accessors
 *
 * for adding buttons, those are handled very different on both mod loaders:
 * on Forge add buttons during init event with appropriate helper methods
 * on Fabric adding is done via custom ButtonList
 */
public interface Screens {

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
}
