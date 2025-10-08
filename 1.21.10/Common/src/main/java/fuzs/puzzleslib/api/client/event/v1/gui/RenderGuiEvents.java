package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

public final class RenderGuiEvents {
    public static final EventInvoker<Before> BEFORE = EventInvoker.lookup(Before.class);
    public static final EventInvoker<After> AFTER = EventInvoker.lookup(After.class);

    private RenderGuiEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Before {

        /**
         * Called at the beginning of {@link Gui#render(GuiGraphics, DeltaTracker)}, before vanilla has drawn any gui
         * elements.
         * <p>
         * For rendering additional elements on the screen use
         * {@link fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext}.
         *
         * @param guiGraphics  the gui graphics component
         * @param deltaTracker the delta tracker, get the partial tick via
         *                     {@link DeltaTracker#getGameTimeDeltaPartialTick(boolean)} by passing {@code false}
         */
        void onBeforeRenderGui(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    }

    @FunctionalInterface
    public interface After {

        /**
         * Called at the end of {@link Gui#render(GuiGraphics, DeltaTracker)}, after vanilla has drawn all gui
         * elements.
         * <p>
         * For rendering additional elements on the screen use
         * {@link fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext}.
         *
         * @param guiGraphics  the gui graphics component
         * @param deltaTracker the delta tracker, get the partial tick via
         *                     {@link DeltaTracker#getGameTimeDeltaPartialTick(boolean)} by passing {@code false}
         */
        void onAfterRenderGui(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    }
}
