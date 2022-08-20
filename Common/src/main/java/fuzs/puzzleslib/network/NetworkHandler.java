package fuzs.puzzleslib.network;

import fuzs.puzzleslib.network.message.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * handler for network communications of all puzzles lib mods
 */
public interface NetworkHandler {

    /**
     * register a message for a side
     *
     * @param clazz     message class type
     * @param supplier supplier for message (called when receiving at executing end)
     *                 we use this additional supplier to avoid having to invoke the class via reflection
     *                 and so that a default constructor in every message cannot be forgotten
     * @param direction side this message is to be executed at
     * @param <T> message implementation
     */
    <T extends Message<T>> void register(Class<? extends T> clazz, Supplier<T> supplier, MessageDirection direction);

    /**
     * send message from client to server
     *
     * @param message message to send
     */
    void sendToServer(Message<?> message);

    /**
     * send message from server to client
     *
     * @param message message to send
     * @param player client player to send to
     */
    void sendTo(Message<?> message, ServerPlayer player);

    /**
     * send message from server to all clients
     *
     * @param message message to send
     */
    void sendToAll(Message<?> message);

    /**
     * send message from server to all clients except one
     *
     * @param message message to send
     * @param exclude client to exclude
     */
    void sendToAllExcept(Message<?> message, ServerPlayer exclude);

    /**
     * send message from server to all clients near given position
     *
     * @param message message to send
     * @param pos source position
     * @param level dimension key provider level
     */
    void sendToAllNear(Message<?> message, BlockPos pos, Level level);

    /**
     * send message from server to all clients near given position
     *
     * @param message message to send
     * @param exclude exclude player having caused this event
     * @param posX     source position x
     * @param posY     source position y
     * @param posZ     source position z
     * @param distance distance from source to receive message
     * @param level dimension key provider level
     */
    void sendToAllNearExcept(Message<?> message, @Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, Level level);

    /**
     * send message from server to all clients in dimension
     *
     * @param message message to send
     * @param level dimension key provider level
     */
    void sendToDimension(Message<?> message, Level level);

    /**
     * send message from server to all clients in dimension
     *
     * @param message message to send
     * @param dimension dimension to send message in
     */
    void sendToDimension(Message<?> message, ResourceKey<Level> dimension);
}
