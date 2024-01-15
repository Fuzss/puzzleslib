package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.minecraft.server.level.ServerPlayer;

/**
 * Built-in sync strategies.
 */
public enum SyncStrategies implements SyncStrategy {
    /**
     * Default state, no syncing is done automatically.
     */
    MANUAL {
        @Override
        public <T extends Record & ClientboundMessage<T>> void sendTo(T message, ServerPlayer player) {

        }
    },
    /**
     * Syncing is done automatically with the capability holder.
     */
    SELF {
        @Override
        public <T extends Record & ClientboundMessage<T>> void sendTo(T message, ServerPlayer player) {
            PuzzlesLibMod.NETWORK.sendTo(player, message);
        }
    },
    /**
     * Syncing is done automatically with the capability holder and every player tracking them.
     * <p>Useful for capabilities that affect rendering (e.g. a glider is gliding).
     */
    SELF_AND_TRACKING {
        @Override
        public <T extends Record & ClientboundMessage<T>> void sendTo(T message, ServerPlayer player) {
            PuzzlesLibMod.NETWORK.sendToAllTracking(player, message, true);
        }
    };
}
