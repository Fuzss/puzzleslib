package fuzs.puzzleslib.api.capability.v2.data;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.capability.SyncStrategyImpl;
import net.minecraft.server.level.ServerPlayer;

/**
 * Different behaviours for automatically syncing this capability to players.
 */
public interface SyncStrategy {
    /**
     * Default state, no syncing is done automatically.
     */
    SyncStrategy MANUAL = new SyncStrategyImpl((message, player) -> {

    });
    /**
     * Syncing is done automatically with the capability holder.
     */
    SyncStrategy SELF = new SyncStrategyImpl((message, player) -> {
        PuzzlesLibMod.NETWORK.sendTo(player, message);
    });
    /**
     * Syncing is done automatically with the capability holder and every player tracking them.
     * <p>Useful for capabilities that affect rendering (e.g. a glider is gliding).
     */
    SyncStrategy SELF_AND_TRACKING = new SyncStrategyImpl((message, entity) -> {
        PuzzlesLibMod.NETWORK.sendToAllTracking(entity, message, true);
    });

    <T extends Record & ClientboundMessage<T>> void sendTo(T message, ServerPlayer player);
}
