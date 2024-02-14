package fuzs.puzzleslib.api.client.event.v1.renderer;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public interface RenderGuiCallback {
    EventInvoker<RenderGuiCallback> EVENT = EventInvoker.lookup(RenderGuiCallback.class);

    /**
     * Called at the end of {@link net.minecraft.client.gui.Gui#render(GuiGraphics, float)} after vanilla has drawn all
     * elements.
     * <p>Allows for rendering additional elements on the screen.
     *
     * @param minecraft    minecraft singleton instance
     * @param guiGraphics  the gui graphics component
     * @param partialTick  partial tick time
     * @param screenWidth  window width
     * @param screenHeight window height
     */
    void onRenderGui(Minecraft minecraft, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight);
}
