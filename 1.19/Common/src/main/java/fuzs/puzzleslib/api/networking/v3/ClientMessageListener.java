package fuzs.puzzleslib.api.networking.v3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

/**
 * Client-side handler for messages received by the client.
 * <p>This is implemented as an anonymous class, to force implementations to be a class as well, to prevent issues concerning loading client-only classes on a dedicated server.
 */
public abstract class ClientMessageListener<T extends Record> {

    /**
     * Called to handle the given message.
     *
     * @param message message to handle
     * @param client  minecraft client instance
     * @param handler handler for vanilla packets
     * @param player  client player entity
     * @param level the local client level
     */
    public abstract void handle(T message, Minecraft client, ClientPacketListener handler, LocalPlayer player, ClientLevel level);
}
