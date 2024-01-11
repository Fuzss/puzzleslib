package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;

public final class ClientPlayerEvents {
    public static final EventInvoker<LoggedIn> LOGGED_IN = EventInvoker.lookup(LoggedIn.class);
    public static final EventInvoker<LoggedOut> LOGGED_OUT = EventInvoker.lookup(LoggedOut.class);
    public static final EventInvoker<Copy> COPY = EventInvoker.lookup(Copy.class);

    private ClientPlayerEvents() {

    }

    @FunctionalInterface
    public interface LoggedIn {

        /**
         * Called when a player joins the server, the player is already initialized.
         *
         * @param player              the player logging out
         * @param multiPlayerGameMode the multiplayer game mode controller for the player
         * @param connection          the network connection to the server for this player
         */
        void onLoggedIn(LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection);
    }

    @FunctionalInterface
    public interface LoggedOut {

        /**
         * Called when a player disconnects from the server.
         *
         * @param player              the player logging out
         * @param multiPlayerGameMode the multiplayer game mode controller for the player
         * @param connection          the network connection to the server for this player
         */
        void onLoggedOut(LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection);
    }

    @FunctionalInterface
    public interface Copy {

        /**
         * Called when the local player is copied to a new local player instance during respawning.
         *
         * @param oldPlayer           the old player
         * @param newPlayer           the newly created player
         * @param multiPlayerGameMode the multiplayer game mode controller for the player
         * @param connection          the network connection to the server for this player
         */
        void onCopy(LocalPlayer oldPlayer, LocalPlayer newPlayer, MultiPlayerGameMode multiPlayerGameMode, Connection connection);
    }
}
