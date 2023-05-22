package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface SyncDataPackContentsCallback {
    EventInvoker<SyncDataPackContentsCallback> EVENT = EventInvoker.lookup(SyncDataPackContentsCallback.class);

    /**
     * Fires before data pack contents (mainly tags and recipes) are sent to a player. This is caused either by a new player connecting to the server,
     * or when data pack contents have been reloaded, e.g. from running <code>/reload</code>. In either case the server resource manager is already up-to-date.
     *
     * @param player the player data pack contents are to be synced with
     * @param joined are contents being sent because the player just joined, otherwise contents are being sent to everyone due to a data pack reload
     */
    void onSyncDataPackContents(ServerPlayer player, boolean joined);
}
