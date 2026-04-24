package fuzs.puzzleslib.common.api.client.event.v1.gui;

import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;

import java.util.List;

@FunctionalInterface
public interface RenderTooltipCallback {
    EventInvoker<RenderTooltipCallback> EVENT = EventInvoker.lookup(RenderTooltipCallback.class);

    /**
     * Called just before a tooltip is drawn on a screen, allows for preventing the tooltip from drawing.
     *
     * @param guiGraphics       the gui graphics instance
     * @param font              the font instance
     * @param mouseX            the mouse cursor x-position
     * @param mouseY            the mouse cursor y-position
     * @param components        the components to render in the tooltip
     * @param tooltipPositioner the positioner for placing the tooltip in relation to provided mouse coordinates
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the tooltip from rendering, allows for fully taking over rendering</li>
     *         <li>{@link EventResult#PASS PASS} to allow the vanilla tooltip to render as usual</li>
     *         </ul>
     */
    EventResult onRenderTooltip(GuiGraphicsExtractor guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner tooltipPositioner);
}
