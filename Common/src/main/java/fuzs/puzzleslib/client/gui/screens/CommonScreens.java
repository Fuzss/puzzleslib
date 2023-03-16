package fuzs.puzzleslib.client.gui.screens;

import fuzs.puzzleslib.util.PuzzlesUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

/**
 * a helper class for accessing encapsulated fields on a screen
 * on Forge those are all exposed, but Fabric requires special accessors
 *
 * <p>for adding buttons, those are handled very different on both mod loaders:
 * on Forge add buttons during init event with appropriate helper methods
 * on Fabric adding is done via custom ButtonList
 */
public interface CommonScreens {
    /**
     * instance of this SPI
     */
    CommonScreens INSTANCE = PuzzlesUtil.loadServiceProvider(CommonScreens.class);

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

    /**
     * @param screen container screen instance
     * @return the slot the mouse is currently hovering (which an item tooltip is shown for and where the hovered slot overlay is drawn)
     */
    @Nullable Slot getHoveredSlot(AbstractContainerScreen<?> screen);
}
