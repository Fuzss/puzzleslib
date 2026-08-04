package fuzs.puzzleslib.common.api.client.event.v1.gui;

import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

/**
 * @deprecated use {@link ScreenEvents.AfterForeground} instead, but note that it requires manually applying the pose
 *         stack translations from {@link AbstractContainerScreen#leftPos} &amp; {@link AbstractContainerScreen#topPos}
 */
@Deprecated(forRemoval = true)
@FunctionalInterface
public interface ExtractContainerScreenContentsCallback {
    EventInvoker<ExtractContainerScreenContentsCallback> EVENT = EventInvoker.lookup(
            ExtractContainerScreenContentsCallback.class);

    /**
     * Called for {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen}, after the screen
     * foreground is drawn (like text labels) via
     * {@link AbstractContainerScreen#extractContents(GuiGraphicsExtractor, int, int, float)}.
     *
     * @param screen      the currently displayed screen
     * @param guiGraphics the gui graphics component
     * @param mouseX      the x-position of the mouse
     * @param mouseY      the y-position of the mouse
     */
    void onExtractContainerScreenContents(AbstractContainerScreen<?> screen, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY);
}
