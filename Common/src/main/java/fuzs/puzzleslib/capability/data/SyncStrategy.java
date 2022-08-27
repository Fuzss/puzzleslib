package fuzs.puzzleslib.capability.data;

import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.network.Message;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

/**
 * different behaviours for automatically syncing this capability
 */
public enum SyncStrategy {
    /**
     * default state, no syncing is done automatically
     */
    MANUAL((o1, o2) -> {}),
    /**
     * syncing is done automatically, but only with the capability holder
     */
    SELF(PuzzlesLib.NETWORK::sendTo),
    /**
     * syncing is done automatically, with the capability holder and every player tracking them
     * useful for capabilities that affect rendering (e.g. a glider is gliding)
     */
    SELF_AND_TRACKING(PuzzlesLib.NETWORK::sendToAllTrackingAndSelf);

    /**
     * message handler
     */
    public final BiConsumer<Message<?>, ServerPlayer> sender;

    /**
     * @param sender message handler
     */
    SyncStrategy(BiConsumer<Message<?>, ServerPlayer> sender) {
        this.sender = sender;
    }
}
