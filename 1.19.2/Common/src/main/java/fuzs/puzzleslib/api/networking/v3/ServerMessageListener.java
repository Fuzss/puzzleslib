package fuzs.puzzleslib.api.networking.v3;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

/**
 * Server-side handler for messages received by the server.
 * <p>This is implemented as an anonymous class, to force implementations to be a class as well, to prevent issues concerning loading server-only classes on a client.
 */
public abstract class ServerMessageListener<T extends Record> {

    /**
     * Called to handle the given message.
     *
     * @param message message to handle
     * @param server  minecraft server instance
     * @param handler handler for vanilla packets
     * @param player  server player entity
     * @param level the current level of <code>player</code>
     */
    public abstract void handle(T message, MinecraftServer server, ServerGamePacketListenerImpl handler, ServerPlayer player, ServerLevel level);
}
