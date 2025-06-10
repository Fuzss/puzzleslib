package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.gui.components.DebugScreenOverlay;

import java.util.List;

public final class GatherDebugTextEvents {
    public static final EventInvoker<GameInformation> GAME_INFORMATION = EventInvoker.lookup(GameInformation.class);
    public static final EventInvoker<SystemInformation> SYSTEM_INFORMATION = EventInvoker.lookup(SystemInformation.class);

    private GatherDebugTextEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface GameInformation {

        /**
         * An event that runs when gathering all game information text lines via
         * {@link DebugScreenOverlay#getGameInformation()}. These are to be rendered on the left side of the debug
         * screen overlay.
         *
         * @param lines the game information text lines
         */
        void onGatherGameInformation(List<String> lines);
    }

    @FunctionalInterface
    public interface SystemInformation {

        /**
         * An event that runs when gathering all system information text lines via
         * {@link DebugScreenOverlay#getSystemInformation()}. These are to be rendered on the right side of the debug
         * screen overlay.
         *
         * @param lines the system information text lines
         */
        void onGatherSystemInformation(List<String> lines);
    }
}
