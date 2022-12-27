package fuzs.puzzleslib.network.v2;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

/**
 * this is a class, so it cannot be implemented as a functional interface to avoid client only calls somehow running into problems on a dedicated server
 */
public interface ClientMessageListener<T extends Record> {

    /**
     * handle given message
     *
     * @param message message to handle
     * @param player  server or client player
     */
    void handle(T message, Minecraft client, ClientPacketListener handler, LocalPlayer player, ClientLevel level);
}
