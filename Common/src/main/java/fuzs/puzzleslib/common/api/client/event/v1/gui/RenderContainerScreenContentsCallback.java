package fuzs.puzzleslib.common.api.client.event.v1.gui;

import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

@FunctionalInterface
public interface RenderContainerScreenContentsCallback {
    EventInvoker<RenderContainerScreenContentsCallback> EVENT = EventInvoker.lookup(
            RenderContainerScreenContentsCallback.class);

    /**
     * Called for {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen AbstractContainerScreens},
     * after the screen foreground is drawn (like text labels) via
     * {@link AbstractContainerScreen#renderContents(GuiGraphicsExtractor, int, int, float)}.
     *
     * @param screen      the currently displayed screen
     * @param guiGraphics the gui graphics component
     * @param mouseX      the x-position of the mouse
     * @param mouseY      the y-position of the mouse
     */
    void onRenderContainerScreenContents(AbstractContainerScreen<?> screen, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY);
}
