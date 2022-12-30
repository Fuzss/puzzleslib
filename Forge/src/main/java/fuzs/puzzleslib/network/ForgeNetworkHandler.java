package fuzs.puzzleslib.network;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.core.DistTypeConverter;
import fuzs.puzzleslib.proxy.Proxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * handler for network communications of all puzzles lib mods
 *
 * @deprecated migrate to {@link fuzs.puzzleslib.impl.networking.ForgeNetworkHandler}
 */
@Deprecated(forRemoval = true)
public class ForgeNetworkHandler implements NetworkHandler {
    /**
     * store network handlers created for a mod to avoid duplicate channels
     */
    private static final Map<String, ForgeNetworkHandler> MOD_TO_NETWORK = Maps.newConcurrentMap();
    /**
     * protocol version for testing client-server compatibility of this mod
     */
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    /**
     * channel for sending messages
     */
    private final SimpleChannel channel;
    /**
     * are servers without this mod or vanilla compatible
     * <p>only stored to ensure other handlers for this mod are created validly
     */
    private final boolean clientAcceptsVanillaOrMissing;
    /**
     * are clients without this mod or vanilla compatible
     * <p>only stored to ensure other handlers for this mod are created validly
     */
    private final boolean serverAcceptsVanillaOrMissing;
    /**
     * message index
     */
    private final AtomicInteger discriminator = new AtomicInteger();

    /**
     * @param channel mod network channel
     */
    private ForgeNetworkHandler(SimpleChannel channel, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        this.channel = channel;
        this.clientAcceptsVanillaOrMissing = clientAcceptsVanillaOrMissing;
        this.serverAcceptsVanillaOrMissing = serverAcceptsVanillaOrMissing;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Message<T>> void register(Class<? extends T> clazz, Supplier<T> factory, MessageDirection direction) {
        BiConsumer<T, FriendlyByteBuf> encode = Message::write;
        Function<FriendlyByteBuf, T> decode = buf -> {
            T message = factory.get();
            message.read(buf);
            return message;
        };
        BiConsumer<T, Supplier<NetworkEvent.Context>> handle = (message, supplier) -> {
            NetworkEvent.Context context = supplier.get();
            final LogicalSide receptionSide = DistTypeConverter.toLogicalSide(direction.getReceptionSide());
            LogicalSide expectedReceptionSide = context.getDirection().getReceptionSide();
            if (expectedReceptionSide != receptionSide) {
                throw new IllegalStateException(String.format("Received message on wrong side, expected %s, was %s", receptionSide, expectedReceptionSide));
            }
            context.enqueueWork(() -> {
                // this needs to happen in here, otherwise Minecraft#player might still be null for events fired on login/entity creation
                Player player;
                if (receptionSide.isClient()) {
                    player = Proxy.INSTANCE.getClientPlayer();
                } else {
                    player = context.getSender();
                }
                message.makeHandler().handle(message, player, LogicalSidedProvider.WORKQUEUE.get(receptionSide));
            });
            context.setPacketHandled(true);
        };
        this.channel.registerMessage(this.discriminator.getAndIncrement(), (Class<T>) clazz, encode, decode, handle);
    }

    @Override
    public Packet<?> toServerboundPacket(Message<?> message) {
        return this.channel.toVanillaPacket(message, NetworkDirection.PLAY_TO_SERVER);
    }

    @Override
    public Packet<?> toClientboundPacket(Message<?> message) {
        return this.channel.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * creates a new network handler for <code>modId</code> or returns an existing one
     *
     * @param modId id for channel name
     * @param clientAcceptsVanillaOrMissing are servers without this mod or vanilla compatible
     * @param serverAcceptsVanillaOrMissing are clients without this mod or vanilla compatible
     * @return mod specific network handler with configured channel
     */
    public synchronized static NetworkHandler of(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        ForgeNetworkHandler handler = MOD_TO_NETWORK.computeIfAbsent(modId, modId1 -> {
            return new ForgeNetworkHandler(buildSimpleChannel(modId1, clientAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing), clientAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing);
        });
        if (handler.clientAcceptsVanillaOrMissing != clientAcceptsVanillaOrMissing) throw new IllegalArgumentException("client channel settings mismatch, expected %s, but was %s".formatted(handler.clientAcceptsVanillaOrMissing, clientAcceptsVanillaOrMissing));
        if (handler.serverAcceptsVanillaOrMissing != serverAcceptsVanillaOrMissing) throw new IllegalArgumentException("server channel settings mismatch, expected %s, but was %s".formatted(handler.serverAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing));
        return handler;
    }

    /**
     * creates a configured channel
     *
     * @param modId id for channel name
     * @param clientAcceptsVanillaOrMissing are servers without this mod or vanilla compatible
     * @param serverAcceptsVanillaOrMissing are clients without this mod or vanilla compatible
     * @return configured channel
     */
    private static SimpleChannel buildSimpleChannel(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(modId, "play"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(clientAcceptsVanillaOrMissing ? NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION) : PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(serverAcceptsVanillaOrMissing ? NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION) : PROTOCOL_VERSION::equals)
                .simpleChannel();
    }
}
