package fuzs.puzzleslib.network;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.proxy.Proxy;
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

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * handler for network communications of all puzzles lib mods
 */
public class FabricNetworkHandler implements NetworkHandler {
    /**
     * registry for class to identifier relation
     */
    private final Map<Class<? extends Message<?>>, MessageData> messages = Maps.newIdentityHashMap();
    /**
     * mod id for channel identifier
     */
    private final String modId;
    /**
     * message index
     */
    private final AtomicInteger discriminator = new AtomicInteger();

    /**
     * @param modId mod id for channel identifier
     */
    private FabricNetworkHandler(String modId) {
        this.modId = modId;
    }

    @Override
    public <T extends Message<T>> void register(Class<? extends T> clazz, Supplier<T> supplier, MessageDirection direction) {
        ResourceLocation channelName = this.nextIdentifier();
        this.messages.put(clazz, new MessageData(clazz, channelName, direction));
        final Function<FriendlyByteBuf, Message<?>> decode = buf -> Util.make(supplier.get(), message -> message.read(buf));
        switch (direction) {
            case TO_CLIENT -> Proxy.INSTANCE.registerClientReceiver(channelName, decode);
            case TO_SERVER -> Proxy.INSTANCE.registerServerReceiver(channelName, decode);
        }
    }

    @Override
    public void sendToServer(Message<?> message) {
        Objects.requireNonNull(Minecraft.getInstance().getConnection(), "Cannot send packets when not in game!");
        Minecraft.getInstance().getConnection().send(this.toServerboundPacket(message));
    }

    @Override
    public void sendTo(Message<?> message, ServerPlayer player) {
        player.connection.send(this.toClientboundPacket(message));
    }

    @Override
    public void sendToAll(Message<?> message) {
        Proxy.INSTANCE.getGameServer().getPlayerList().broadcastAll(this.toClientboundPacket(message));
    }

    @Override
    public void sendToAllExcept(Message<?> message, ServerPlayer exclude) {
        final Packet<?> packet = this.toClientboundPacket(message);
        for (ServerPlayer player : Proxy.INSTANCE.getGameServer().getPlayerList().getPlayers()) {
            if (player != exclude) player.connection.send(packet);
        }
    }

    @Override
    public void sendToAllNear(Message<?> message, BlockPos pos, Level level) {
        this.sendToAllNearExcept(message, null, pos.getX(), pos.getY(), pos.getZ(), 64.0, level);
    }

    @Override
    public void sendToAllNearExcept(Message<?> message, @Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, Level level) {
        Proxy.INSTANCE.getGameServer().getPlayerList().broadcast(exclude, posX, posY, posZ, distance, level.dimension(), this.toClientboundPacket(message));
    }

    @Override
    public void sendToDimension(Message<?> message, Level level) {
        this.sendToDimension(message, level.dimension());
    }

    @Override
    public void sendToDimension(Message<?> message, ResourceKey<Level> dimension) {
        Proxy.INSTANCE.getGameServer().getPlayerList().broadcastAll(this.toClientboundPacket(message), dimension);
    }

    /**
     * use discriminator to generate identifier for package
     *
     * @return unique identifier
     */
    private ResourceLocation nextIdentifier() {
        return new ResourceLocation(this.modId, "play/" + this.discriminator.getAndIncrement());
    }

    /**
     * @param message   message to create packet from
     * @return          packet for message
     */
    private Packet<?> toServerboundPacket(Message<?> message) {
        if (this.messages.get(message.getClass()).direction() != MessageDirection.TO_SERVER) throw new IllegalStateException("Attempted sending message to wrong side, expected %s, was %s".formatted(MessageDirection.TO_SERVER, MessageDirection.TO_CLIENT));
        return this.toPacket(ClientPlayNetworking::createC2SPacket, message);
    }

    /**
     * @param message   message to create packet from
     * @return          packet for message
     */
    private Packet<?> toClientboundPacket(Message<?> message) {
        if (this.messages.get(message.getClass()).direction() != MessageDirection.TO_CLIENT) throw new IllegalStateException("Attempted sending message to wrong side, expected %s, was %s".formatted(MessageDirection.TO_CLIENT, MessageDirection.TO_SERVER));
        return this.toPacket(ServerPlayNetworking::createS2CPacket, message);
    }

    /**
     * @param packetFactory     packet factory for client or server
     * @param message           message to create packet from
     * @return                  packet for message
     */
    private Packet<?> toPacket(BiFunction<ResourceLocation, FriendlyByteBuf, Packet<?>> packetFactory, Message<?> message) {
        ResourceLocation identifier = this.messages.get(message.getClass()).identifier();
        FriendlyByteBuf byteBuf = PacketByteBufs.create();
        message.write(byteBuf);
        return packetFactory.apply(identifier, byteBuf);
    }

    /**
     * creates a new network handler
     *
     * @param modId id for channel name
     * @return mod specific network handler with default channel
     */
    public static FabricNetworkHandler of(String modId) {
        return new FabricNetworkHandler(modId);
    }

    /**
     * basic data class for data from registering messages
     *
     * @param clazz         message base class
     * @param identifier    registered identifier
     * @param direction     direction message is sent
     */
    private record MessageData(Class<? extends Message<?>> clazz, ResourceLocation identifier, MessageDirection direction) {

    }
}
