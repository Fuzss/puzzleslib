package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Minecraft;

/**
 * TODO implement these on NeoForge for 1.21.5 with the new events
 */
public final class ClientLifecycleEvents {
    public static final EventInvoker<Started> STARTED = EventInvoker.lookup(Started.class);
    public static final EventInvoker<Stopping> STOPPING = EventInvoker.lookup(Stopping.class);

    private ClientLifecycleEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Started {

        /**
         * Called the client has started and is about to tick for the first time.
         *
         * @param minecraft the client instance
         */
        void onClientStarted(Minecraft minecraft);
    }

    @FunctionalInterface
    public interface Stopping {

        /**
         * Called the client begins an orderly shutdown. This is caused by quitting while in game, or closing the game
         * window.
         *
         * @param minecraft the client instance
         */
        void onClientStopping(Minecraft minecraft);
    }
}
