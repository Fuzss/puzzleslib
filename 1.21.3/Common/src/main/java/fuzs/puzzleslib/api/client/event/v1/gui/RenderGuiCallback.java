package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

@Deprecated(forRemoval = true)
@FunctionalInterface
public interface RenderGuiCallback {
    EventInvoker<RenderGuiCallback> EVENT = EventInvoker.lookup(RenderGuiCallback.class);

    /**
     * Called at the end of {@link net.minecraft.client.gui.Gui#render(GuiGraphics, DeltaTracker)} after vanilla has
     * drawn all elements. Allows for rendering additional elements on the screen.
     *
     * @param minecraft    the minecraft instance
     * @param guiGraphics  the gui graphics component
     * @param deltaTracker the delta tracker, get the partial tick via
     *                     {@link DeltaTracker#getGameTimeDeltaPartialTick(boolean)} by passing {@code false}
     */
    void onRenderGui(Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker);
}
