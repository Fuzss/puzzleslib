package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerNetworkEvents {
    public static final EventInvoker<LoggedIn> LOGGED_IN = EventInvoker.lookup(LoggedIn.class);
    public static final EventInvoker<LoggedOut> LOGGED_OUT = EventInvoker.lookup(LoggedOut.class);

    private PlayerNetworkEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface LoggedIn {

        /**
         * Called when a player joins the server and is added to the {@link net.minecraft.server.players.PlayerList}.
         *
         * @param serverPlayer the player logging in
         */
        void onLoggedIn(ServerPlayer serverPlayer);
    }

    @FunctionalInterface
    public interface LoggedOut {

        /**
         * Called when a player disconnects from the server and is removed from the
         * {@link net.minecraft.server.players.PlayerList}.
         *
         * @param serverPlayer the player logging out
         */
        void onLoggedOut(ServerPlayer serverPlayer);
    }
}
