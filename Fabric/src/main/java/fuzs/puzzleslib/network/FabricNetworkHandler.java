package fuzs.puzzleslib.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fuzs.puzzleslib.network.message.Message;
import fuzs.puzzleslib.proxy.Proxy;
import fuzs.puzzleslib.util.PuzzlesUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * handler for network communications of all puzzles lib mods
 */
public class FabricNetworkHandler implements NetworkHandler {
    /**
     * registry for class to identifier relation
     */
    private final BiMap<Class<? extends Message>, ResourceLocation> messageRegistry = HashBiMap.create();
    /**
     * mod id for channel identifier
     */
    private final String modId;
    /**
     * message index
     */
    private final AtomicInteger discriminator;

    /**
     * @param modId mod id for channel identifier
     */
    private FabricNetworkHandler(String modId) {
        this.modId = modId;
        this.discriminator = new AtomicInteger();
    }

    /**
     * register a message for a side
     * mostly from AutoRegLib, thanks Vazkii!
     * @param clazz     message class type
     * @param supplier supplier for message (called when receiving at executing end)
     *                 we use this additional supplier to avoid having to invoke the class via reflection
     *                 and so that a default constructor in every message cannot be forgotten
     * @param direction side this message is to be executed at
     * @param <T> message implementation
     */
    @Override
    public <T extends Message> void register(Class<T> clazz, Supplier<T> supplier, MessageDirection direction) {
        ResourceLocation channelName = this.nextIdentifier();
        this.messageRegistry.put(clazz, channelName);
        final Function<FriendlyByteBuf, Message> decode = buf -> Util.make(supplier.get(), message -> message.read(buf));
        switch (direction) {
            case TO_CLIENT -> Proxy.INSTANCE.registerClientReceiver(channelName, decode);
            case TO_SERVER -> Proxy.INSTANCE.registerServerReceiver(channelName, decode);
        }
    }

    /**
     * use discriminator to generate identifier for package
     * @return unique identifier
     */
    private ResourceLocation nextIdentifier() {
        return new ResourceLocation(this.modId, "play/" + this.discriminator.getAndIncrement());
    }

    /**
     * send message from client to server
     * @param message message to send
     */
    @Override
    public void sendToServer(Message message) {
        Objects.requireNonNull(Minecraft.getInstance().getConnection(), "Cannot send packets when not in game!");
        Minecraft.getInstance().getConnection().send(this.toServerboundPacket(message));
    }

    /**
     * send message from server to client
     * @param message message to send
     * @param player client player to send to
     */
    @Override
    public void sendTo(Message message, ServerPlayer player) {
        player.connection.send(this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients
     * @param message message to send
     */
    @Override
    public void sendToAll(Message message) {
        Proxy.INSTANCE.getGameServer().getPlayerList().broadcastAll(this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients except one
     * @param message message to send
     * @param exclude client to exclude
     */
    @Override
    public void sendToAllExcept(Message message, ServerPlayer exclude) {
        final Packet<?> packet = this.toClientboundPacket(message);
        for (ServerPlayer player : Proxy.INSTANCE.getGameServer().getPlayerList().getPlayers()) {
            if (player != exclude) {
                player.connection.send(packet);
            }
        }
    }

    /**
     * send message from server to all clients near given position
     * @param message message to send
     * @param pos source position
     * @param level dimension key provider level
     */
    @Override
    public void sendToAllNear(Message message, BlockPos pos, Level level) {
        this.sendToAllNearExcept(message, null, pos.getX(), pos.getY(), pos.getZ(), 64.0, level);
    }

    /**
     * send message from server to all clients near given position
     * @param message message to send
     * @param exclude exclude player having caused this event
     * @param posX     source position x
     * @param posY     source position y
     * @param posZ     source position z
     * @param distance distance from source to receive message
     * @param level dimension key provider level
     */
    @Override
    public void sendToAllNearExcept(Message message, @Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, Level level) {
        Proxy.INSTANCE.getGameServer().getPlayerList().broadcast(exclude, posX, posY, posZ, distance, level.dimension(), this.toClientboundPacket(message));
    }

    /**
     * send message from server to all clients in dimension
     * @param message message to send
     * @param level dimension key provider level
     */
    @Override
    public void sendToDimension(Message message, Level level) {
        this.sendToDimension(message, level.dimension());
    }

    /**
     * send message from server to all clients in dimension
     * @param message message to send
     * @param dimension dimension to send message in
     */
    @Override
    public void sendToDimension(Message message, ResourceKey<Level> dimension) {
        Proxy.INSTANCE.getGameServer().getPlayerList().broadcastAll(this.toClientboundPacket(message), dimension);
    }

    /**
     * @param message message to create packet from
     * @return      packet for message
     */
    private Packet<?> toServerboundPacket(Message message) {
        ResourceLocation identifier = this.messageRegistry.get(message.getClass());
        FriendlyByteBuf byteBuf = PacketByteBufs.create();
        message.write(byteBuf);
        return ClientPlayNetworking.createC2SPacket(identifier, byteBuf);
    }

    /**
     * @param message message to create packet from
     * @return      packet for message
     */
    private Packet<?> toClientboundPacket(Message message) {
        ResourceLocation identifier = this.messageRegistry.get(message.getClass());
        FriendlyByteBuf byteBuf = PacketByteBufs.create();
        message.write(byteBuf);
        return ServerPlayNetworking.createS2CPacket(identifier, byteBuf);
    }

    /**
     * creates a new network handler
     * @param modId id for channel name
     * @return mod specific network handler with default channel
     */
    public static FabricNetworkHandler of(String modId) {
        return new FabricNetworkHandler(modId);
    }
}
