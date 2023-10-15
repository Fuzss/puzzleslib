package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;

import java.util.List;

public final class GatherDebugTextEvents {
    public static final EventInvoker<Left> LEFT = EventInvoker.lookup(Left.class);
    public static final EventInvoker<Right> RIGHT = EventInvoker.lookup(Right.class);

    private GatherDebugTextEvents() {

    }

    @FunctionalInterface
    public interface Left {

        /**
         * An event that runs just before rendering all left lines on the {@link net.minecraft.client.gui.components.DebugScreenOverlay}.
         * <p>Allows for modifying the list of text lines to render.
         *
         * @param lines the text lines to render
         */
        void onGatherLeftDebugText(List<String> lines);
    }

    @FunctionalInterface
    public interface Right {

        /**
         * An event that runs just before rendering all right lines on the {@link net.minecraft.client.gui.components.DebugScreenOverlay}.
         * <p>Allows for modifying the list of text lines to render.
         *
         * @param lines the text lines to render
         */
        void onGatherRightDebugText(List<String> lines);
    }
}
