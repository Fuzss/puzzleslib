package fuzs.puzzleslib.common.api.client.event.v1.gui;

import fuzs.puzzleslib.common.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class RenderGuiEvents {
    public static final EventInvoker<Before> BEFORE = EventInvoker.lookup(Before.class);
    public static final EventInvoker<After> AFTER = EventInvoker.lookup(After.class);

    private RenderGuiEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Before {

        /**
         * Called at the beginning of {@link Gui#extractRenderState(GuiGraphicsExtractor, DeltaTracker)}, before vanilla
         * has drawn any gui elements.
         * <p>
         * For rendering additional elements on the screen use {@link GuiLayersContext}.
         *
         * @param guiGraphics  the gui graphics component
         * @param deltaTracker the delta tracker, get the partial tick via
         *                     {@link DeltaTracker#getGameTimeDeltaPartialTick(boolean)} by passing {@code false}
         */
        void onBeforeRenderGui(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker);
    }

    @FunctionalInterface
    public interface After {

        /**
         * Called at the end of {@link Gui#extractRenderState(GuiGraphicsExtractor, DeltaTracker)}, after vanilla has
         * drawn all gui elements.
         * <p>
         * For rendering additional elements on the screen use {@link GuiLayersContext}.
         *
         * @param guiGraphics  the gui graphics component
         * @param deltaTracker the delta tracker, get the partial tick via
         *                     {@link DeltaTracker#getGameTimeDeltaPartialTick(boolean)} by passing {@code false}
         */
        void onAfterRenderGui(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker);
    }
}
