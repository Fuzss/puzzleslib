package fuzs.puzzleslib.api.network.v3;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

/**
 * Server-side handler for messages received by the server.
 * <p>
 * This is implemented as an anonymous class, to force implementations to be a class as well, to prevent issues
 * concerning loading server-only classes on a client.
 *
 * @param <T> the message to handle
 */
public abstract class ServerMessageListener<T> {

    /**
     * Called to handle the given message.
     *
     * @param message the message to handle
     * @param server  the minecraft server
     * @param handler the server player packet handler
     * @param player  the server player
     * @param level   the level of the player
     */
    public abstract void handle(T message, MinecraftServer server, ServerGamePacketListenerImpl handler, ServerPlayer player, ServerLevel level);
}
