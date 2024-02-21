package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Different behaviours for automatically syncing capability components to tracking remotes.
 */
public enum SyncStrategy {
    /**
     * Default state, no syncing is done automatically.
     */
    MANUAL {
        @Override
        public PlayerSet getPlayerSet(Entity entity) {
            return PlayerSet.ofNone();
        }
    },
    /**
     * Syncing is done automatically with the capability holder and every {@link ServerPlayer} tracking them.
     * <p>Useful for capabilities that affect rendering (e.g. a glider is equipped for gliding).
     */
    TRACKING {
        @Override
        public PlayerSet getPlayerSet(Entity entity) {
            return PlayerSet.nearEntity(entity);
        }
    },
    /**
     * Syncing is done automatically with just the capability holder if it is a {@link ServerPlayer}.
     * <p>Useful for capabilities that control an ability, like the amount of midair jumps left.
     */
    PLAYER {
        @Override
        public PlayerSet getPlayerSet(Entity entity) {
            return PlayerSet.ofEntity(entity);
        }
    };

    /**
     * Send a {@link CapabilityComponent} to remotes.
     *
     * @param entity  the capability component holder
     * @param message the message to send
     */
    public abstract PlayerSet getPlayerSet(Entity entity);
}
