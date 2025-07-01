package fuzs.puzzleslib.api.client.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;

@FunctionalInterface
public interface ClientPlayerCopyCallback {
    EventInvoker<ClientPlayerCopyCallback> EVENT = EventInvoker.lookup(ClientPlayerCopyCallback.class);

    /**
     * Called when the local player is copied to a new local player instance during respawning.
     *
     * @param oldPlayer           the old player
     * @param newPlayer           the newly created player
     * @param multiPlayerGameMode the multiplayer game mode controller for the player
     * @param connection          the network connection to the server for this player
     */
    void onCopy(LocalPlayer oldPlayer, LocalPlayer newPlayer, MultiPlayerGameMode multiPlayerGameMode, Connection connection);
}
