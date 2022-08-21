package fuzs.puzzleslib.network;

import fuzs.puzzleslib.core.DistTypeConverter;
import fuzs.puzzleslib.proxy.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.Objects;
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
    public <T extends Message<T>> void register(Class<? extends T> clazz, Supplier<T> supplier, MessageDirection direction) {
        BiConsumer<T, FriendlyByteBuf> encode = Message::write;
        Function<FriendlyByteBuf, T> decode = buf -> {
            T message = supplier.get();
            message.read(buf);
            return message;
        };
        BiConsumer<T, Supplier<NetworkEvent.Context>> handle = (msg, ctxSup) -> {
            NetworkEvent.Context ctx = ctxSup.get();
            final LogicalSide receptionSide = DistTypeConverter.toLogicalSide(direction.getReceptionSide());
            LogicalSide expectedReceptionSide = ctx.getDirection().getReceptionSide();
            if (expectedReceptionSide != receptionSide) {
                throw new IllegalStateException(String.format("Received message on wrong side, expected %s, was %s", receptionSide, expectedReceptionSide));
            }
            Player player;
            if (receptionSide.isClient()) {
                player = Proxy.INSTANCE.getClientPlayer();
            } else {
                player = ctx.getSender();
            }
            ctx.enqueueWork(() -> msg.handle(player, LogicalSidedProvider.WORKQUEUE.get(receptionSide)));
            ctx.setPacketHandled(true);
        };
        this.channel.registerMessage(this.discriminator.getAndIncrement(), (Class<T>) clazz, encode, decode, handle);
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
            if (player != exclude) {
                player.connection.send(packet);
            }
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
     * @param message   message to create packet from
     * @return          packet for message
     */
    private Packet<?> toServerboundPacket(Message<?> message) {
        return this.channel.toVanillaPacket(message, NetworkDirection.PLAY_TO_SERVER);
    }

    /**
     * @param message   message to create packet from
     * @return          packet for message
     */
    private Packet<?> toClientboundPacket(Message<?> message) {
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
