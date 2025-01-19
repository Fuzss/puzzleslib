package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class PlayerTrackingEvents {
    public static final EventInvoker<Start> START = EventInvoker.lookup(Start.class);
    public static final EventInvoker<Stop> STOP = EventInvoker.lookup(Stop.class);

    private PlayerTrackingEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Start {

        /**
         * Called before an entity starts being tracked by a player, meaning the player receives updates about the
         * entity like its motion.
         *
         * @param trackedEntity the entity that will be tracked
         * @param serverPlayer  the player that will track the entity
         */
        void onStartTracking(Entity trackedEntity, ServerPlayer serverPlayer);
    }

    @FunctionalInterface
    public interface Stop {

        /**
         * Called after an entity stops being tracked by a player.
         *
         * @param trackedEntity the entity that is no longer being tracked
         * @param serverPlayer  the player that is no longer tracking the entity
         */
        void onStopTracking(Entity trackedEntity, ServerPlayer serverPlayer);
    }
}
