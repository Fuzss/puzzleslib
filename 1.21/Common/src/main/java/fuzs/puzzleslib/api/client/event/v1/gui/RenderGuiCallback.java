package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public interface RenderGuiCallback {
    EventInvoker<RenderGuiCallback> EVENT = EventInvoker.lookup(RenderGuiCallback.class);

    /**
     * Called at the end of {@link net.minecraft.client.gui.Gui#render(GuiGraphics, DeltaTracker)} after vanilla has
     * drawn all elements.
     * <p>
     * Allows for rendering additional elements on the screen.
     * <p>
     * Screen width and height can easily be retrieved from {@link Minecraft#getWindow()}, but are kept in here for
     * legacy compatibility.
     *
     * @param minecraft    minecraft singleton instance
     * @param guiGraphics  the gui graphics component
     * @param deltaTracker partial tick time
     */
    void onRenderGui(Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker);
}
