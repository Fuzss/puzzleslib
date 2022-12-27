package fuzs.puzzleslib.network.v2;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.network.v2.serialization.MessageSerializers;
import fuzs.puzzleslib.proxy.ForgeProxy;
import fuzs.puzzleslib.proxy.Proxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ForgeNetworkHandler implements NetworkHandler {
    private static final Map<String, NetworkHandler> MOD_TO_NETWORK = Maps.newConcurrentMap();
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    private final SimpleChannel channel;
    private final boolean clientAcceptsVanillaOrMissing;
    private final boolean serverAcceptsVanillaOrMissing;
    private final AtomicInteger discriminator = new AtomicInteger();

    private ForgeNetworkHandler(SimpleChannel channel, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        this.channel = channel;
        this.clientAcceptsVanillaOrMissing = clientAcceptsVanillaOrMissing;
        this.serverAcceptsVanillaOrMissing = serverAcceptsVanillaOrMissing;
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> NetworkHandler registerClientbound(Class<T> clazz) {
        this.register(clazz, ((ForgeProxy) Proxy.INSTANCE)::registerClientReceiverV2);
        return this;
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> NetworkHandler registerServerbound(Class<T> clazz) {
        this.register(clazz, ((ForgeProxy) Proxy.INSTANCE)::registerServerReceiverV2);
        return this;
    }

    private <T> void register(Class<T> clazz, BiConsumer<T, Supplier<NetworkEvent.Context>> handle) {
        if (!clazz.isRecord()) throw new IllegalArgumentException("Message of type %s is not a record".formatted(clazz));
        BiConsumer<T, FriendlyByteBuf> encode = (t, friendlyByteBuf) -> {
            MessageSerializers.findByType(clazz).write(friendlyByteBuf, t);
        };
        Function<FriendlyByteBuf, T> decode = MessageSerializers.findByType(clazz)::read;
        this.channel.registerMessage(this.discriminator.getAndIncrement(), clazz, encode, decode, handle);
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> Packet<?> toClientboundPacket(T message) {
        return this.channel.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> Packet<?> toServerboundPacket(T message) {
        return this.channel.toVanillaPacket(message, NetworkDirection.PLAY_TO_SERVER);
    }

    public synchronized static NetworkHandler of(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        ForgeNetworkHandler handler = (ForgeNetworkHandler) MOD_TO_NETWORK.computeIfAbsent(modId, modId1 -> {
            return new ForgeNetworkHandler(buildSimpleChannel(modId1, clientAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing), clientAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing);
        });
        if (handler.clientAcceptsVanillaOrMissing != clientAcceptsVanillaOrMissing) throw new IllegalArgumentException("client channel settings mismatch, expected %s, but was %s".formatted(handler.clientAcceptsVanillaOrMissing, clientAcceptsVanillaOrMissing));
        if (handler.serverAcceptsVanillaOrMissing != serverAcceptsVanillaOrMissing) throw new IllegalArgumentException("server channel settings mismatch, expected %s, but was %s".formatted(handler.serverAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing));
        return handler;
    }

    private static SimpleChannel buildSimpleChannel(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(modId, "play"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(clientAcceptsVanillaOrMissing ? NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION) : PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(serverAcceptsVanillaOrMissing ? NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION) : PROTOCOL_VERSION::equals)
                .simpleChannel();
    }
}
