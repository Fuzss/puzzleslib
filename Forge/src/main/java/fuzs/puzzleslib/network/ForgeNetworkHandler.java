package fuzs.puzzleslib.network;

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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * handler for network communications of all puzzles lib mods
 */
public class ForgeNetworkHandler implements NetworkHandler {
    /**
     * protocol version for testing client-server compatibility of this mod
     */
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    /**
     * channel for sending messages
     */
    private final SimpleChannel channel;
    /**
     * message index
     */
    private final AtomicInteger discriminator = new AtomicInteger();

    /**
     * @param channel mod network channel
     */
    private ForgeNetworkHandler(SimpleChannel channel) {
        this.channel = channel;
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
                message.handle(player, LogicalSidedProvider.WORKQUEUE.get(receptionSide));
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
     * creates a new network handler
     *
     * @param modId id for channel name
     * @return mod specific network handler with default channel
     */
    public static ForgeNetworkHandler of(String modId) {
        return of(modId, false, false);
    }

    /**
     * creates a new network handler
     *
     * @param modId id for channel name
     * @param clientAcceptsVanillaOrMissing are servers without this mod or vanilla compatible
     * @param serverAcceptsVanillaOrMissing are clients without this mod or vanilla compatible
     * @return mod specific network handler with configured channel
     */
    public static ForgeNetworkHandler of(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        final SimpleChannel channel = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(modId, "play"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(clientAcceptsVanillaOrMissing ? NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION) : PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(serverAcceptsVanillaOrMissing ? NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION) : PROTOCOL_VERSION::equals)
                .simpleChannel();
        return new ForgeNetworkHandler(channel);
    }
}
