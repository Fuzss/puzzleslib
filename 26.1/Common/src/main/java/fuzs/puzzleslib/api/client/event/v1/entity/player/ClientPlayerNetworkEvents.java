package fuzs.puzzleslib.api.client.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;

public final class ClientPlayerNetworkEvents {
    public static final EventInvoker<Join> JOIN = EventInvoker.lookup(Join.class);
    public static final EventInvoker<Leave> LEAVE = EventInvoker.lookup(Leave.class);

    private ClientPlayerNetworkEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Join {

        /**
         * Called when a player joins the server, the player is already initialized.
         *
         * @param player              the player logging out
         * @param multiPlayerGameMode the multiplayer game mode controller for the player
         * @param connection          the network connection to the server for this player
         */
        void onPlayerJoin(LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection);
    }

    @FunctionalInterface
    public interface Leave {

        /**
         * Called when a player disconnects from the server, but also occurs before joining a new single player level or
         * server.
         *
         * @param player              the player logging out
         * @param multiPlayerGameMode the multiplayer game mode controller for the player
         * @param connection          the network connection to the server for this player
         */
        void onPlayerLeave(LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection);
    }
}
