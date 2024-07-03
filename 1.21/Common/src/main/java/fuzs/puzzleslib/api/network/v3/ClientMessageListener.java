package fuzs.puzzleslib.api.network.v3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

/**
 * Client-side handler for messages received by the client.
 * <p>
 * This is implemented as an anonymous class, to force implementations to be a class as well, to prevent issues
 * concerning loading client-only classes on a dedicated server.
 *
 * @param <T> the message to handle
 */
public abstract class ClientMessageListener<T> {

    /**
     * Called to handle the given message.
     *
     * @param message the message to handle
     * @param client  the minecraft client
     * @param handler the client player packet handler
     * @param player  the client player
     * @param level   the local client level
     */
    public abstract void handle(T message, Minecraft client, ClientPacketListener handler, LocalPlayer player, ClientLevel level);
}
