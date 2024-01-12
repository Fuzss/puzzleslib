package fuzs.puzzleslib.neoforge.impl.network;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v2.MessageDirection;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.impl.network.NetworkHandlerImplHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
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

public class NetworkHandlerForgeV2 implements NetworkHandlerV2 {
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    private final AtomicInteger discriminator = new AtomicInteger();
    private final SimpleChannel channel;
    public final boolean clientAcceptsVanillaOrMissing;
    public final boolean serverAcceptsVanillaOrMissing;

    public NetworkHandlerForgeV2(ResourceLocation channelIdentifier, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        this.channel = buildSimpleChannel(channelIdentifier, clientAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing);
        this.clientAcceptsVanillaOrMissing = clientAcceptsVanillaOrMissing;
        this.serverAcceptsVanillaOrMissing = serverAcceptsVanillaOrMissing;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends MessageV2<T>> void register(Class<? extends T> clazz, Supplier<T> factory, MessageDirection direction) {
        Function<FriendlyByteBuf, T> decode = NetworkHandlerImplHelper.getDirectMessageDecoder(factory);
        LogicalSide receptionSide = direction == MessageDirection.TO_CLIENT ? LogicalSide.CLIENT : LogicalSide.SERVER;
        this.register((Class<T>) clazz, decode, receptionSide);
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerClientbound(Class<T> clazz) {
        this.register(clazz, NetworkHandlerImplHelper.getMessageDecoder(clazz), LogicalSide.CLIENT);
        return this;
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerServerbound(Class<T> clazz) {
        this.register(clazz, NetworkHandlerImplHelper.getMessageDecoder(clazz), LogicalSide.SERVER);
        return this;
    }

    private <T extends MessageV2<T>> void register(Class<T> clazz, Function<FriendlyByteBuf, T> decode, LogicalSide receptionSide) {
        BiConsumer<T, Supplier<NetworkEvent.Context>> handle = (message, supplier) -> {
            NetworkEvent.Context context = supplier.get();
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
        this.channel.registerMessage(this.discriminator.getAndIncrement(), clazz, MessageV2::write, decode, handle);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Packet<ServerCommonPacketListener> toServerboundPacket(MessageV2<?> message) {
        return (Packet<ServerGamePacketListener>) this.channel.toVanillaPacket(message, NetworkDirection.PLAY_TO_SERVER);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Packet<ClientCommonPacketListener> toClientboundPacket(MessageV2<?> message) {
        return (Packet<ClientGamePacketListener>) this.channel.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static SimpleChannel buildSimpleChannel(ResourceLocation resourceLocation, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return NetworkRegistry.ChannelBuilder
                .named(resourceLocation)
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(clientAcceptsVanillaOrMissing ? NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION) : PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(serverAcceptsVanillaOrMissing ? NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION) : PROTOCOL_VERSION::equals)
                .simpleChannel();
    }
}
