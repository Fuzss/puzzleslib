package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.gui.components.DebugScreenOverlay;

import java.util.List;

public final class GatherDebugInformationEvents {
    public static final EventInvoker<Game> GAME = EventInvoker.lookup(Game.class);
    public static final EventInvoker<System> SYSTEM = EventInvoker.lookup(System.class);

    private GatherDebugInformationEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Game {

        /**
         * An event that runs when gathering all game information text lines via
         * {@link DebugScreenOverlay#getGameInformation()}.
         * <p>
         * These are to be rendered on the left side of the debug screen overlay.
         *
         * @param lines the game information text lines
         */
        void onGatherGameInformation(List<String> lines);
    }

    @FunctionalInterface
    public interface System {

        /**
         * An event that runs when gathering all system information text lines via
         * {@link DebugScreenOverlay#getSystemInformation()}.
         * <p>
         * These are to be rendered on the right side of the debug screen overlay.
         *
         * @param lines the system information text lines
         */
        void onGatherSystemInformation(List<String> lines);
    }
}
