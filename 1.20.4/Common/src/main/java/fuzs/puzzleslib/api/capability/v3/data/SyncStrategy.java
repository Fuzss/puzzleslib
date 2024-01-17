package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.capability.ClientboundEntityCapabilityMessage;
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
        public void send(Entity entity, ClientboundEntityCapabilityMessage message) {

        }
    },
    /**
     * Syncing is done automatically with the capability holder and every {@link ServerPlayer} tracking them.
     * <p>Useful for capabilities that affect rendering (e.g. a glider is equipped for gliding).
     */
    TRACKING {
        @Override
        public void send(Entity entity, ClientboundEntityCapabilityMessage message) {
            PuzzlesLibMod.NETWORK.sendToAllTracking(entity, message, true);
        }
    },
    /**
     * Syncing is done automatically with just the capability holder if it is a {@link ServerPlayer}.
     * <p>Useful for capabilities that control an ability, like the amount of midair jumps left.
     */
    PLAYER {
        @Override
        public void send(Entity entity, ClientboundEntityCapabilityMessage message) {
            if (entity instanceof ServerPlayer player) {
                PuzzlesLibMod.NETWORK.sendTo(player, message);
            }
        }
    };

    /**
     * Send a {@link CapabilityComponent} to remotes.
     *
     * @param entity  the capability component holder
     * @param message the message to send
     */
    public abstract void send(Entity entity, ClientboundEntityCapabilityMessage message);
}
