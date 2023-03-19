package fuzs.puzzleslib.impl.network;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v2.MessageDirection;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.impl.core.FabricProxy;
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
 */
public class NetworkHandlerFabricV2 implements NetworkHandlerV2 {
    /**
     * store network handlers created for a mod to avoid duplicate channels
     */
    private static final Map<String, NetworkHandlerFabricV2> MOD_TO_NETWORK = Maps.newConcurrentMap();

    /**
     * registry for class to identifier relation
     */
    private final Map<Class<? extends MessageV2<?>>, MessageData> messages = Maps.newIdentityHashMap();
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
    private NetworkHandlerFabricV2(String modId) {
        this.modId = modId;
    }

    @Override
    public <T extends MessageV2<T>> void register(Class<? extends T> clazz, Supplier<T> supplier, MessageDirection direction) {
        ResourceLocation channelName = this.nextIdentifier();
        this.messages.put(clazz, new MessageData(clazz, channelName, direction));
        final Function<FriendlyByteBuf, T> decode = buf -> Util.make(supplier.get(), message -> message.read(buf));
        switch (direction) {
            case TO_CLIENT -> ((FabricProxy) Proxy.INSTANCE).registerLegacyClientReceiver(channelName, decode);
            case TO_SERVER -> ((FabricProxy) Proxy.INSTANCE).registerLegacyServerReceiver(channelName, decode);
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
    public Packet<?> toServerboundPacket(MessageV2<?> message) {
        if (this.messages.get(message.getClass()).direction() != MessageDirection.TO_SERVER) throw new IllegalStateException("Attempted sending message to wrong side, expected %s, was %s".formatted(MessageDirection.TO_SERVER, MessageDirection.TO_CLIENT));
        return this.toPacket(ClientPlayNetworking::createC2SPacket, message);
    }

    @Override
    public Packet<?> toClientboundPacket(MessageV2<?> message) {
        if (this.messages.get(message.getClass()).direction() != MessageDirection.TO_CLIENT) throw new IllegalStateException("Attempted sending message to wrong side, expected %s, was %s".formatted(MessageDirection.TO_CLIENT, MessageDirection.TO_SERVER));
        return this.toPacket(ServerPlayNetworking::createS2CPacket, message);
    }

    /**
     * @param packetFactory     packet factory for client or server
     * @param message           message to create packet from
     * @return                  packet for message
     */
    private Packet<?> toPacket(BiFunction<ResourceLocation, FriendlyByteBuf, Packet<?>> packetFactory, MessageV2<?> message) {
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
    public synchronized static NetworkHandlerV2 of(String modId) {
        return MOD_TO_NETWORK.computeIfAbsent(modId, NetworkHandlerFabricV2::new);
    }

    /**
     * basic data class for data from registering messages
     *
     * @param clazz         message base class
     * @param identifier    registered identifier
     * @param direction     direction message is sent
     */
    private record MessageData(Class<? extends MessageV2<?>> clazz, ResourceLocation identifier, MessageDirection direction) {

    }
}
