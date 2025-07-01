package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;

import java.util.List;

@FunctionalInterface
public interface RenderTooltipCallback {
    EventInvoker<RenderTooltipCallback> EVENT = EventInvoker.lookup(RenderTooltipCallback.class);

    /**
     * Called just before a tooltip is drawn on a screen, allows for preventing the tooltip from drawing.
     *
     * @param guiGraphics the gui graphics instance
     * @param font        the font instance
     * @param mouseX      x position of the mouse cursor
     * @param mouseY      y position of the mouse cursor
     * @param components  components to render in the tooltip
     * @param positioner  positioner for placing the tooltip in relation to provided mouse coordinates
     * @return {@link EventResult#INTERRUPT} to prevent the tooltip from rendering, allows for fully taking over
     *         rendering, {@link EventResult#PASS} to allow the vanilla tooltip to render as usual
     */
    EventResult onRenderTooltip(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner positioner);
}
