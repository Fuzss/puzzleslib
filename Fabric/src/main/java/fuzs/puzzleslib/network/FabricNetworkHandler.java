package fuzs.puzzleslib.network;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.proxy.FabricProxy;
import fuzs.puzzleslib.proxy.Proxy;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * handler for network communications of all puzzles lib mods
 *
 * @deprecated migrate to {@link fuzs.puzzleslib.impl.networking.FabricNetworkHandler}
 */
@Deprecated(forRemoval = true)
public class FabricNetworkHandler implements NetworkHandler {
    /**
     * store network handlers created for a mod to avoid duplicate channels
     */
    private static final Map<String, FabricNetworkHandler> MOD_TO_NETWORK = Maps.newConcurrentMap();

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
        final Function<FriendlyByteBuf, T> decode = buf -> Util.make(supplier.get(), message -> message.read(buf));
        switch (direction) {
            case TO_CLIENT -> ((FabricProxy) Proxy.INSTANCE).registerClientReceiver(channelName, decode);
            case TO_SERVER -> ((FabricProxy) Proxy.INSTANCE).registerServerReceiver(channelName, decode);
        }
    }

    /**
     * use discriminator to generate identifier for package
     *
     * @return unique identifier
     */
    private ResourceLocation nextIdentifier() {
        return new ResourceLocation(this.modId, "play/" + this.discriminator.getAndIncrement());
    }

    @Override
    public Packet<?> toServerboundPacket(Message<?> message) {
        if (this.messages.get(message.getClass()).direction() != MessageDirection.TO_SERVER) throw new IllegalStateException("Attempted sending message to wrong side, expected %s, was %s".formatted(MessageDirection.TO_SERVER, MessageDirection.TO_CLIENT));
        return this.toPacket(ClientPlayNetworking::createC2SPacket, message);
    }

    @Override
    public Packet<?> toClientboundPacket(Message<?> message) {
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
     * creates a new network handler for <code>modId</code> or returns an existing one
     *
     * @param modId id for channel name
     * @return mod specific network handler with default channel
     */
    public synchronized static NetworkHandler of(String modId) {
        return MOD_TO_NETWORK.computeIfAbsent(modId, FabricNetworkHandler::new);
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
