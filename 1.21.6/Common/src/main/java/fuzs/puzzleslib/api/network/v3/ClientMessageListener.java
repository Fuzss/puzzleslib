package fuzs.puzzleslib.api.network.v3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

/**
 * Handler for messages received by the client.
 * <p>
 * This is implemented as a class, to force any implementation to be a class as well. This is to prevent issues with
 * loading client-only classes on the server.
 *
 * @param <T> the message to handle
 */
public abstract class ClientMessageListener<T> {

    /**
     * Called to handle the given message.
     *
     * @param message              the message to handle
     * @param minecraft            the minecraft client
     * @param clientPacketListener the client player packet handler
     * @param player               the client player
     * @param clientLevel          the local client level
     */
    public abstract void handle(T message, Minecraft minecraft, ClientPacketListener clientPacketListener, LocalPlayer player, ClientLevel clientLevel);
}
