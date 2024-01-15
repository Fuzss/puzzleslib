package fuzs.puzzleslib.api.capability.v2.data;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.server.level.ServerPlayer;

/**
 * Different behaviours for automatically syncing this capability to players.
 */
public interface SyncStrategy {

    <T extends Record & ClientboundMessage<T>> void sendTo(T message, ServerPlayer player);
}
