package fuzs.puzzleslib.api.client.event.v1.gui;

import com.mojang.blaze3d.platform.Window;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public final class GatherDebugTextEvents {
    public static final EventInvoker<Left> LEFT = EventInvoker.lookup(Left.class);
    public static final EventInvoker<Right> RIGHT = EventInvoker.lookup(Right.class);

    private GatherDebugTextEvents() {

    }

    @FunctionalInterface
    public interface Left {

        /**
         * An event that runs just before rendering all game information text lines on the left side of the debug screen
         * overlay in {@link net.minecraft.client.gui.components.DebugScreenOverlay#drawGameInformation(GuiGraphics)}.
         *
         * @param window       game window instance
         * @param guiGraphics  gui graphics instance
         * @param deltaTracker current partial tick time
         * @param lines        game information text lines about to be rendered
         */
        void onGatherLeftDebugText(Window window, GuiGraphics guiGraphics, DeltaTracker deltaTracker, List<String> lines);
    }

    @FunctionalInterface
    public interface Right {

        /**
         * An event that runs just before rendering all system information text lines on the right side of the debug
         * screen overlay in
         * {@link net.minecraft.client.gui.components.DebugScreenOverlay#drawSystemInformation(GuiGraphics)}.
         *
         * @param window       game window instance
         * @param guiGraphics  gui graphics instance
         * @param deltaTracker current partial tick time
         * @param lines        system information text lines about to be rendered
         */
        void onGatherRightDebugText(Window window, GuiGraphics guiGraphics, DeltaTracker deltaTracker, List<String> lines);
    }
}
