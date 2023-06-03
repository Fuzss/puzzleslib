package fuzs.puzzleslib.client.gui.screens;

import fuzs.puzzleslib.mixin.client.accessor.AbstractContainerScreenFabricAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;

import java.util.Objects;

/**
 * a helper class for accessing encapsulated fields on a screen
 * on Forge those are all exposed, but Fabric requires special accessors
 *
 * for adding buttons, those are handled very different on both mod loaders:
 * on Forge add buttons during init event with appropriate helper methods
 * on Fabric adding is done via custom ButtonList
 */
public class Screens {
    /**
     * @param screen screen instance
     * @return minecraft singleton
     */
    public static Minecraft getMinecraft(Screen screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");
        return net.fabricmc.fabric.api.client.screen.v1.Screens.getClient(screen);
    }

    /**
     * @param screen screen instance
     * @return font renderer
     */
    public static Font getFont(Screen screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");
        return net.fabricmc.fabric.api.client.screen.v1.Screens.getTextRenderer(screen);
    }

    /**
     * @param screen screen instance
     * @return item renderer
     */
    public static ItemRenderer getItemRenderer(Screen screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");
        return net.fabricmc.fabric.api.client.screen.v1.Screens.getItemRenderer(screen);
    }

    /**
     * @param screen container screen instance
     * @return width of container interface
     */
    public static int getImageWidth(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");
        return ((AbstractContainerScreenFabricAccessor) screen).puzzleslib$getXSize();
    }

    /**
     * @param screen container screen instance
     * @return height of container interface
     */
    public static int getImageHeight(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");
        return ((AbstractContainerScreenFabricAccessor) screen).puzzleslib$getYSize();
    }

    /**
     * @param screen container screen instance
     * @return left position of container interface
     */
    public static int getLeftPos(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");
        return ((AbstractContainerScreenFabricAccessor) screen).puzzleslib$getGuiLeft();
    }

    /**
     * @param screen container screen instance
     * @return top position of container interface
     */
    public static int getTopPos(AbstractContainerScreen<?> screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");
        return ((AbstractContainerScreenFabricAccessor) screen).puzzleslib$getGuiTop();
    }
}
