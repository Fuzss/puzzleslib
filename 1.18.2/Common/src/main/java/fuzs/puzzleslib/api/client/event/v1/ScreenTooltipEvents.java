package fuzs.puzzleslib.api.client.event.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import java.util.List;

public final class ScreenTooltipEvents {
    public static final EventInvoker<Render> RENDER = EventInvoker.lookup(Render.class);

    private ScreenTooltipEvents() {

    }

    @FunctionalInterface
    public interface Render {

        /**
         * Called just before a tooltip is drawn on a screen, allows for preventing the tooltip from drawing.
         *
         * @param poseStack    the gui graphics instance
         * @param mouseX       x position of the mouse cursor
         * @param mouseY       y position of the mouse cursor
         * @param screenWidth  current width of the screen
         * @param screenHeight current height of the screen
         * @param font         the font instance
         * @param components   components to render in the tooltip
         * @return {@link EventResult#INTERRUPT} to prevent the tooltip from rendering, allows for fully taking over rendering,
         * {@link EventResult#PASS} to allow the vanilla tooltip to render as usual
         */
        EventResult onRenderTooltip(PoseStack poseStack, int mouseX, int mouseY, int screenWidth, int screenHeight, Font font, List<ClientTooltipComponent> components);
    }
}
