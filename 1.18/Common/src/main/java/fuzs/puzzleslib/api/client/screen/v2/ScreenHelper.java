package fuzs.puzzleslib.api.client.screen.v2;

import fuzs.puzzleslib.impl.client.core.ClientFactories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

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
