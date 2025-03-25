package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerCopyEvents {
    public static final EventInvoker<Copy> COPY = EventInvoker.lookup(Copy.class);
    public static final EventInvoker<Respawn> RESPAWN = EventInvoker.lookup(Respawn.class);

    private PlayerCopyEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Copy {

        /**
         * Called when player data is copied to a new player.
         *
         * @param originalServerPlayer the old player
         * @param newServerPlayer      the new player
         * @param originalStillAlive   whether the copy was made when returning from the End dimension, otherwise caused
         *                             by the old player having died
         */
        void onCopy(ServerPlayer originalServerPlayer, ServerPlayer newServerPlayer, boolean originalStillAlive);
    }

    @FunctionalInterface
    public interface Respawn {

        /**
         * Called after player a has been respawned.
         *
         * @param serverPlayer       the new player
         * @param originalStillAlive whether the copy was made when returning from the End dimension, otherwise caused
         *                           by the old player having died
         */
        void onRespawn(ServerPlayer serverPlayer, boolean originalStillAlive);
    }
}
