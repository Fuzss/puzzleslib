package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerCopyEvents {
    public static final EventInvoker<Copy> COPY = EventInvoker.lookup(Copy.class);
    public static final EventInvoker<Respawn> RESPAWN = EventInvoker.lookup(Respawn.class);

    private PlayerCopyEvents() {

    }

    @FunctionalInterface
    public interface Copy {

        /**
         * Called when player data is copied to a new player.
         *
         * @param originalPlayer     the old player
         * @param newPlayer          the new player
         * @param originalStillAlive whether the copy was made when returning from the End dimension, otherwise caused by the old player having died
         */
        void onCopy(ServerPlayer originalPlayer, ServerPlayer newPlayer, boolean originalStillAlive);
    }

    @FunctionalInterface
    public interface Respawn {

        /**
         * Called after player a has been respawned.
         *
         * @param player             the new player
         * @param originalStillAlive whether the copy was made when returning from the End dimension, otherwise caused by the old player having died
         */
        void onRespawn(ServerPlayer player, boolean originalStillAlive);
    }
}
