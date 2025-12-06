package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerNetworkEvents {
    public static final EventInvoker<Join> JOIN = EventInvoker.lookup(Join.class);
    public static final EventInvoker<Leave> LEAVE = EventInvoker.lookup(Leave.class);

    private PlayerNetworkEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Join {

        /**
         * Called when a player joins the server and is added to the {@link net.minecraft.server.players.PlayerList}.
         *
         * @param serverPlayer the player logging in
         */
        void onPlayerJoin(ServerPlayer serverPlayer);
    }

    @FunctionalInterface
    public interface Leave {

        /**
         * Called when a player disconnects from the server and is removed from the
         * {@link net.minecraft.server.players.PlayerList}.
         *
         * @param serverPlayer the player logging out
         */
        void onPlayerLeave(ServerPlayer serverPlayer);
    }
}
