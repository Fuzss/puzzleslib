package fuzs.puzzleslib.network.v2;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

/**
 * this is a class, so it cannot be implemented as a functional interface to avoid client only calls somehow running into problems on a dedicated server
 */
public abstract class ServerMessageListener<T extends Record> {

    /**
     * handle given message
     *
     * @param message message to handle
     * @param player  server or client player
     */
    public abstract void handle(T message, MinecraftServer server, ServerGamePacketListenerImpl handler, ServerPlayer player, ServerLevel level);
}
